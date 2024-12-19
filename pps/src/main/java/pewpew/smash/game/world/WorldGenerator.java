package pewpew.smash.game.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.stream.IntStream;
import java.awt.Color;

public class WorldGenerator {
    public static final int TILE_SIZE = 5;
    private static final int worldWidth = 1200;
    private static final int worldHeight = 1200;
    private byte[][] world;
    private final long seed;

    public static final byte GRASS = 1;
    public static final byte WATER = 2;
    public static final byte DRY_SAND = 3;
    public static final byte WET_SAND = 4;
    public static final byte GRASS_LIGHT = 5;
    public static final byte GRASS_DARK = 6;

    public static long generateSeed() {
        return System.currentTimeMillis() ^ new Random().nextLong();
    }

    public WorldGenerator(long seed) {
        this.seed = seed;
        this.world = new byte[worldWidth][worldHeight];
        generateWorld();
    }

    public byte[][] getWorldData() {
        return this.world;
    }

    public long getSeed() {
        return this.seed;
    }

    public static int getWorldWidth() {
        return worldWidth * TILE_SIZE;
    }

    public static int getWorldHeight() {
        return worldHeight * TILE_SIZE;
    }

    private void generateWorld() {
        double[][] heightMap = generateHeightMap();
        applyIslandMask(heightMap);
        assignTerrainTypes(heightMap);
        smoothCoastline();
        generateBeaches();
    }

    private double[][] generateHeightMap() {
        PerlinNoise noise = new PerlinNoise(seed);
        double baseScale = 0.003;
        int octaves = 4;
        double persistence = 0.5;

        double[][] heightMap = new double[worldWidth][worldHeight];
        IntStream.range(0, worldWidth).parallel().forEach(x -> {
            for (int y = 0; y < worldHeight; y++) {
                double amplitude = 1;
                double frequency = baseScale;
                double noiseValue = 0;

                for (int i = 0; i < octaves; i++) {
                    noiseValue += amplitude * noise.noise(x * frequency, y * frequency);
                    amplitude *= persistence;
                    frequency *= 2;
                }

                heightMap[x][y] = (noiseValue + 1) / 2;
            }
        });
        return heightMap;
    }

    private void applyIslandMask(double[][] heightMap) {
        double islandShapeIntensity = 0.9;
        double falloffExponent = 1.8;

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                double nx = (x / (double) worldWidth) - 0.5;
                double ny = (y / (double) worldHeight) - 0.5;
                double distanceFromCenter = Math.sqrt(nx * nx + ny * ny) * islandShapeIntensity;

                double mask = Math.pow(1 - distanceFromCenter, falloffExponent);
                heightMap[x][y] *= Math.max(mask, 0);
            }
        }
    }

    private void assignTerrainTypes(double[][] heightMap) {
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                double height = heightMap[x][y];

                if (height < 0.3) {
                    world[x][y] = WATER;
                } else if (height < 0.4) {
                    world[x][y] = DRY_SAND;
                } else {
                    world[x][y] = GRASS;
                }
            }
        }
    }

    private void smoothCoastline() {
        byte[][] tempWorld = new byte[worldWidth][worldHeight];

        for (int iteration = 0; iteration < 2; iteration++) {
            IntStream.range(1, worldWidth - 1).parallel().forEach(x -> {
                for (int y = 1; y < worldHeight - 1; y++) {
                    int landCount = 0;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (world[x + dx][y + dy] == GRASS) {
                                landCount++;
                            }
                        }
                    }
                    tempWorld[x][y] = (landCount > 4) ? GRASS : WATER;
                }
            });

            for (int x = 0; x < worldWidth; x++) {
                System.arraycopy(tempWorld[x], 0, world[x], 0, worldHeight);
            }
        }
    }

    private void generateBeaches() {
        int maxBeachWidth = 20;

        for (int x = 1; x < worldWidth - 1; x++) {
            for (int y = 1; y < worldHeight - 1; y++) {
                if (world[x][y] == GRASS) {
                    int waterProximity = 0;

                    for (int dx = -maxBeachWidth; dx <= maxBeachWidth; dx++) {
                        for (int dy = -maxBeachWidth; dy <= maxBeachWidth; dy++) {
                            int nx = x + dx;
                            int ny = y + dy;

                            if (nx >= 0 && nx < worldWidth && ny >= 0 && ny < worldHeight
                                    && world[nx][ny] == WATER) {
                                waterProximity++;
                            }
                        }
                    }

                    if (waterProximity > 0) {
                        if (waterProximity > (maxBeachWidth * maxBeachWidth) / 3) {
                            world[x][y] = WET_SAND;
                        } else if (waterProximity > (maxBeachWidth * maxBeachWidth) / 6) {
                            world[x][y] = DRY_SAND;
                        }
                    }
                }
            }
        }
    }

    public static BufferedImage getWorldImage(byte[][] world) {
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        BufferedImage image = new BufferedImage(worldWidth * TILE_SIZE, worldHeight * TILE_SIZE,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        for (int y = 0; y < worldHeight; y++) {
            for (int x = 0; x < worldWidth; x++) {
                byte tileType = world[x][y];

                if (tileType == GRASS) {
                    g.setColor(getGrassColor(x, y));
                } else if (tileType == WATER) {
                    g.setColor(new Color(0, 0, 255));
                } else if (tileType == DRY_SAND) {
                    g.setColor(new Color(238, 214, 175));
                } else if (tileType == WET_SAND) {
                    g.setColor(new Color(210, 180, 140));
                }
                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        g.dispose();
        return image;
    }

    private static Color getGrassColor(int x, int y) {
        int baseGreen = 139;
        int textureVariation = new Random(x * 31 + y * 17).nextInt(10) - 5;
        int green = Math.max(120, Math.min(baseGreen + textureVariation, 160));
        return new Color(34, green, 34);
    }
}
