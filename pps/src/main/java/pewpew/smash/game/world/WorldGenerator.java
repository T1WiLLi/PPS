package pewpew.smash.game.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.stream.IntStream;

import java.awt.Color;

public class WorldGenerator {
    private static final int tileSize = 5;
    private static int worldWidth = 400;
    private static int worldHeight = 400;
    private byte[][] world;
    private double[][] noiseCache;
    private final long seed;

    public static final byte GRASS = 1;
    public static final byte WATER = 2;
    public static final byte SAND = 3;

    public static long generateSeed() {
        return System.currentTimeMillis() ^ new Random().nextLong();
    }

    public WorldGenerator(long seed) {
        this.seed = seed;
        this.world = new byte[worldWidth][worldHeight];
        this.noiseCache = new double[worldWidth][worldHeight];
        generateWorld();
    }

    public byte[][] getWorldData() {
        return this.world;
    }

    public long getSeed() {
        return this.seed;
    }

    public static int getWorldWidth() {
        return worldWidth * tileSize;
    }

    public static int getWorldHeight() {
        return worldHeight * tileSize;
    }

    private void generateWorld() {
        PerlinNoise noise = new PerlinNoise(seed);
        precomputeNoise(noise);
        generateLargeIsland();
        smoothCoastline();
        generateBeaches();
        spawnWorldEntitiesAndWeapons();
    }

    private void precomputeNoise(PerlinNoise noise) {
        double noiseScale = 0.003;
        IntStream.range(0, worldWidth).parallel().forEach(x -> {
            for (int y = 0; y < worldHeight; y++) {
                noiseCache[x][y] = (noise.noise(x * noiseScale, y * noiseScale) + 1) / 2;
            }
        });
    }

    private void generateLargeIsland() {
        double islandThreshold = 0.825;

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                double nx = x / (double) worldWidth - 0.5;
                double ny = y / (double) worldHeight - 0.5;

                double distanceFromCenter = Math.sqrt(nx * nx + ny * ny) * 2;
                double noiseValue = noiseCache[x][y];

                double combined = distanceFromCenter * 0.7 + noiseValue * 0.3;

                if (combined < islandThreshold) {
                    this.world[x][y] = GRASS;
                } else {
                    this.world[x][y] = WATER;
                }
            }
        }
    }

    private void smoothCoastline() {
        byte[][] tempWorld = new byte[worldWidth][worldHeight];

        int smoothIterations = 2;
        for (int iteration = 0; iteration < smoothIterations; iteration++) {
            byte[][] currentWorld = tempWorld;
            IntStream.range(1, worldWidth - 1).parallel().forEach(x -> {
                for (int y = 1; y < worldHeight - 1; y++) {
                    int landCount = 0;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (this.world[x + dx][y + dy] == GRASS) {
                                landCount++;
                            }
                        }
                    }
                    currentWorld[x][y] = (landCount > 4) ? GRASS : WATER;
                }
            });

            byte[][] swap = world;
            world = tempWorld;
            tempWorld = swap;
        }
    }

    private void generateBeaches() {
        for (int x = 1; x < worldWidth - 1; x++) {
            for (int y = 1; y < worldHeight - 1; y++) {
                if (this.world[x][y] == GRASS) {
                    boolean isCoast = false;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int nx = x + dx;
                            int ny = y + dy;
                            if (nx >= 0 && nx < worldWidth && ny >= 0 && ny < worldHeight
                                    && this.world[nx][ny] == WATER) {
                                isCoast = true;
                                break;
                            }
                        }
                        if (isCoast)
                            break;
                    }
                    if (isCoast) {
                        this.world[x][y] = SAND;
                    }
                }
            }
        }
    }

    private void spawnWorldEntitiesAndWeapons() {
        Random random = new Random(seed);
        IntStream.range(0, 50).forEach(i -> {
            int x = random.nextInt(worldWidth);
            int y = random.nextInt(worldHeight);
            if (world[x][y] == GRASS) {
                System.out.println("Spawned an entity at: " + x + ", " + y);
            }
        });
    }

    public static BufferedImage getWorldImage(byte[][] world) {
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        BufferedImage image = new BufferedImage(worldWidth * tileSize, worldHeight * tileSize,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        for (int y = 0; y < worldHeight; y++) {
            for (int x = 0; x < worldWidth; x++) {
                byte tileType = world[x][y];

                if (tileType == GRASS) {
                    g.setColor(new Color(34, 139, 34));
                    g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                } else if (tileType == WATER) {
                    g.setColor(new Color(0, 0, 255));
                    g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                } else if (tileType == SAND) {
                    g.setColor(new Color(238, 214, 175));
                    g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }
        }

        g.dispose();
        return image;
    }
}
