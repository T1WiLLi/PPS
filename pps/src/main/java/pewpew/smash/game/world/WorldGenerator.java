package pewpew.smash.game.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

import pewpew.smash.game.utils.HelpMethods;

public class WorldGenerator { // 1, 2, 3, 4, 5, 6, 7, 8, 9
    private static final int tileSize = 5;
    private int worldWidth = 4000;
    private int worldHeight = 4000;
    private byte[][] world;
    private double[][] noiseCache;

    private static final byte GRASS = 1;
    private static final byte WATER = 2;
    private static final byte SAND = 3;

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

    public static BufferedImage getWorldImage(byte[][] world) {
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        BufferedImage image = new BufferedImage(worldWidth * tileSize, worldHeight * tileSize,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        TextureFactory textureFactory = TextureFactory.getInstance();
        int[][] distanceToLand = calculateDistanceToLand(world);

        for (int y = 0; y < worldHeight; y++) {
            for (int x = 0; x < worldWidth; x++) {
                byte tileType = world[x][y];
                BufferedImage tileTexture;

                if (tileType == GRASS) {
                    tileTexture = textureFactory.getTexture(TextureType.GRASS, (x + y) % 3);
                } else if (tileType == WATER) {
                    int distance = distanceToLand[x][y];
                    tileTexture = textureFactory.generateWaterTexture(distance);
                } else if (tileType == SAND) {
                    tileTexture = textureFactory.getTexture(TextureType.SAND, (x + y) % 3);
                } else {
                    tileTexture = null;
                }

                if (tileTexture != null) {
                    g.drawImage(tileTexture, x * tileSize, y * tileSize, null);
                }
            }
        }

        g.dispose();
        return image;
    }

    private static int[][] calculateDistanceToLand(byte[][] world) {
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        int[][] distance = new int[worldWidth][worldHeight];
        Queue<Point> queue = new LinkedList<>();

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                if (world[x][y] == GRASS || world[x][y] == SAND) {
                    distance[x][y] = 0;
                    queue.offer(new Point(x, y));
                } else {
                    distance[x][y] = Integer.MAX_VALUE;
                }
            }
        }

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        while (!queue.isEmpty()) {
            Point p = queue.poll();
            for (int[] dir : directions) {
                int nx = p.x + dir[0];
                int ny = p.y + dir[1];
                if (nx >= 0 && nx < worldWidth && ny >= 0 && ny < worldHeight) {
                    if (distance[nx][ny] > distance[p.x][p.y] + 1) {
                        distance[nx][ny] = distance[p.x][p.y] + 1;
                        queue.offer(new Point(nx, ny));
                    }
                }
            }
        }
        return distance;
    }

    private static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}