package pewpew.smash.game.world;

import java.util.Random;

// DISCLAIMER !!! This class is not mine, it was taken from Stackoverflow, I unfortunately lost the link to the original post.
public class PerlinNoise {
    private final int[] permutation;
    private static final int PERMUTATION_SIZE = 256;

    public PerlinNoise(long seed) {
        permutation = new int[PERMUTATION_SIZE * 2];
        Random random = new Random(seed);

        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            permutation[i] = i;
        }

        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            int j = random.nextInt(PERMUTATION_SIZE);
            int temp = permutation[i];
            permutation[i] = permutation[j];
            permutation[j] = temp;
        }

        System.arraycopy(permutation, 0, permutation, PERMUTATION_SIZE, PERMUTATION_SIZE);
    }

    public double noise(double x, double y) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);

        double u = fade(x);
        double v = fade(y);

        int aa = permutation[permutation[X] + Y];
        int ab = permutation[permutation[X] + Y + 1];
        int ba = permutation[permutation[X + 1] + Y];
        int bb = permutation[permutation[X + 1] + Y + 1];

        double result = lerp(v, lerp(u, grad(permutation[aa], x, y),
                grad(permutation[ba], x - 1, y)),
                lerp(u, grad(permutation[ab], x, y - 1),
                        grad(permutation[bb], x - 1, y - 1)));
        return (result + 1) / 2;
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}