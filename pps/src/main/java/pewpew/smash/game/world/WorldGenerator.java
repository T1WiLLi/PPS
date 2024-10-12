package pewpew.smash.game.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.util.stream.IntStream;

import pewpew.smash.game.utils.HelpMethods;

public class WorldGenerator { // 1, 2, 3, 4, 5, 6, 7, 8, 9
    private static final int tileSize = 5;
    private int worldWidth = 2000;
    private int worldHeight = 2000;
    private byte[][] world;
    private double[][] noiseCache;

    private static final byte GRASS = 1;
    private static final byte WATER = 2;
    private static final byte SAND = 3;

    private static final Color WATER_COLOR = new Color(0, 0, 169);
    private static final Color SAND_COLOR = new Color(210, 180, 140);

    public WorldGenerator() {
        this.world = new byte[this.worldWidth][this.worldHeight];
        this.noiseCache = new double[this.worldWidth][this.worldHeight];
        long startTime = System.currentTimeMillis();
        generateWorld();
        long endTime = System.currentTimeMillis();
        System.out.println("Map generation took " + (endTime - startTime) + " milliseconds.");
    }

    public byte[][] getWorldData() {
        return this.world;
    }

    private void generateWorld() {
        PerlinNoise noise = new PerlinNoise(System.currentTimeMillis());
        precomputeNoise(noise);
        generateLargeIsland();
        smoothCoastline();
        generateBeaches();
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
                    int dxRange = HelpMethods.getRandomBetween(10, 25);
                    int dyRange = HelpMethods.getRandomBetween(10, 25);
                    for (int dx = -dxRange; dx <= dxRange; dx++) {
                        for (int dy = -dyRange; dy <= dyRange; dy++) {
                            if (this.world[x + dx][y + dy] == WATER) {
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

    public static BufferedImage getWorldImage(byte[][] worldData) {
        int worldWidth = worldData.length;
        int worldHeight = worldData[0].length;
        BufferedImage image = new BufferedImage(worldWidth * tileSize, worldHeight * tileSize,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        PerlinNoise noise = new PerlinNoise(System.currentTimeMillis());
        double[][] cachedNoise = new double[worldWidth][worldHeight];

        IntStream.range(0, worldWidth).parallel().forEach(x -> {
            for (int y = 0; y < worldHeight; y++) {
                cachedNoise[x][y] = (noise.noise(x * 0.3, y * 0.3) + 1) / 2;
            }
        });

        for (int y = 0; y < worldHeight; y++) {
            for (int x = 0; x < worldWidth; x++) {
                Color color = switch (worldData[x][y]) {
                    case GRASS -> getGrassColor(cachedNoise[x][y]);
                    case WATER -> WATER_COLOR;
                    case SAND -> SAND_COLOR;
                    default -> Color.BLACK;
                };
                g.setColor(color);
                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }

        g.dispose();
        return image;
    }

    private static Color getGrassColor(double noiseValue) {
        int baseGreen = 139;
        int greenVariation = (int) (noiseValue * 30);
        int green = Math.min(255, Math.max(100, baseGreen + greenVariation));

        return new Color(34, green, 34);
    }
}