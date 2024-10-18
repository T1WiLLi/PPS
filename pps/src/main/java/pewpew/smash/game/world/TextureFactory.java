package pewpew.smash.game.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class TextureFactory {

    private static final TextureFactory instance = new TextureFactory();

    private static final int MAX_TEXTURES = 256;
    private static final int MAX_TEXTURE_SIZE = 16;

    private Map<TextureType, Texture> textures = new HashMap<>(MAX_TEXTURES);

    private static final Color DEEP_WATER_COLOR = new Color(0, 0, 169);
    private static final Color SHALLOW_WATER_COLOR = new Color(0, 128, 255);
    private static final int MAX_WATER_DEPTH = 120;

    public static TextureFactory getInstance() {
        return instance;
    }

    public void preloadTextures() {
        textures.put(TextureType.GRASS, loadGrassTexture());
        textures.put(TextureType.SAND, loadSandTexture());
    }

    public BufferedImage getTexture(TextureType type, int variation) {
        Texture texture = textures.get(type);
        return texture.getTextures()[variation];
    }

    public BufferedImage generateWaterTexture(int distanceToLand) {
        int textureSize = MAX_TEXTURE_SIZE;
        BufferedImage waterTexture = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = waterTexture.createGraphics();
        Color waterColor = getWaterColor(distanceToLand);

        g.setColor(waterColor);
        g.fillRect(0, 0, textureSize, textureSize);
        g.dispose();

        return waterTexture;
    }

    private Texture loadGrassTexture() {
        BufferedImage[] grassTextures = new BufferedImage[3];
        for (int i = 0; i < 3; i++) {
            grassTextures[i] = generateGrassTexture(i);
        }
        return new Texture(TextureType.GRASS, grassTextures, (byte) 16);
    }

    private BufferedImage generateGrassTexture(int variation) {
        int textureSize = MAX_TEXTURE_SIZE;
        BufferedImage texture = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = texture.createGraphics();

        PerlinNoise noise = new PerlinNoise(System.currentTimeMillis());
        for (int x = 0; x < textureSize; x++) {
            for (int y = 0; y < textureSize; y++) {
                double noiseValue = (noise.noise(x * 0.8, y * 0.8) + 1) / 2;
                int greenVariation = (int) (noiseValue * 50);
                int green = Math.min(255, Math.max(100, 139 + greenVariation));
                g.setColor(new Color(34, green, 34));
                g.fillRect(x, y, 1, 1);
            }
        }

        g.dispose();
        return texture;
    }

    private Texture loadSandTexture() {
        BufferedImage[] sandTextures = new BufferedImage[3];
        for (int i = 0; i < 3; i++) {
            sandTextures[i] = generateSandTexture(i);
        }
        return new Texture(TextureType.SAND, sandTextures, (byte) 16);
    }

    private BufferedImage generateSandTexture(int variation) {
        int textureSize = MAX_TEXTURE_SIZE;
        BufferedImage texture = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = texture.createGraphics();

        PerlinNoise noise = new PerlinNoise(System.currentTimeMillis() + variation);
        for (int x = 0; x < textureSize; x++) {
            for (int y = 0; y < textureSize; y++) {
                double noiseValue = (noise.noise(x * 0.8, y * 0.8) + 1) / 2;
                int baseR = 194;
                int baseG = 178;
                int baseB = 128;

                int variationAmount = (int) (noiseValue * 50 - 15);

                int r = Math.min(255, Math.max(150, baseR + variationAmount));
                int g = Math.min(255, Math.max(134, baseG + variationAmount));
                int b = Math.min(255, Math.max(84, baseB + variationAmount));

                g2.setColor(new Color(r, g, b));
                g2.fillRect(x, y, 1, 1);
            }
        }

        g2.dispose();
        return texture;
    }

    private Color getWaterColor(int distanceToLand) {
        if (distanceToLand >= MAX_WATER_DEPTH) {
            return DEEP_WATER_COLOR;
        } else {
            float ratio = (float) distanceToLand / MAX_WATER_DEPTH;
            ratio = 1 - (1 - ratio) * (1 - ratio);
            return interpolateColor(SHALLOW_WATER_COLOR, DEEP_WATER_COLOR, ratio);
        }
    }

    private Color interpolateColor(Color c1, Color c2, float ratio) {
        int red = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int green = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int blue = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(red, green, blue);
    }

    private record Texture(TextureType type, BufferedImage[] textures, byte textureSize) {
        public BufferedImage[] getTextures() {
            return textures;
        }
    }
}
