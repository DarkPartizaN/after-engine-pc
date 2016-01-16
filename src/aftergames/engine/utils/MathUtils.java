package aftergames.engine.utils;

import aftergames.engine.utils.geom.Vector2;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author KiQDominaN
 */
public final class MathUtils {

    private static final Random rnd = new Random();

    public static float random_chance() {
        return random_float(0, 1);
    }

    public static int random_color() {
        int r = random_int(0, 255);
        int g = random_int(0, 255);
        int b = random_int(0, 255);

        return ((0xff << 24) | (r << 16) | (g << 8) | b);
    }

    public static int random_int(int min, int max) {
        return min + rnd.nextInt(1 + max - min);
    }

    public static float random_float(float min, float max) {
        return min + rnd.nextFloat() * (1 + max - min);
    }

    public static double random_double(double min, double max) {
        return min + rnd.nextDouble() * (1 + max - min);
    }

    public static long random_long(long min, long max) {
        return min + (Math.abs(rnd.nextLong()) % (1 + max - min));
    }

    public static float sin(double angle) {
        return (float) Math.sin(Math.toRadians(angle));
    }

    public static float cos(double angle) {
        return (float) Math.cos(Math.toRadians(angle));
    }

    public static float tan(double d) {
        return (float) Math.tan(d);
    }

    public static float sqrt(double d) {
        return (float) Math.sqrt(d);
    }

    public static float angle(float x, float y) {
        return (float) Math.toDegrees(Math.atan2(x, y));
    }

    public static float angle(Vector2 a, Vector2 b) {
        float x1 = a.x, y1 = a.y;
        float x2 = b.x, y2 = b.y;

        return angle(y1 - y2, x1 - x2);
    }

    public static int min(int a, int b) {
        return (a < b) ? a : b;
    }

    public static float min(float a, float b) {
        return (a < b) ? a : b;
    }

    public static double min(double a, double b) {
        return (a < b) ? a : b;
    }

    public static int max(int a, int b) {
        return (a > b) ? a : b;
    }

    public static float max(float a, float b) {
        return (a > b) ? a : b;
    }

    public static double max(double a, double b) {
        return (a > b) ? a : b;
    }

    public static int bound(int a, int b, int c) {
        int r = b;
        if (b < a) r = a;
        if (b > c) r = c;

        return r;
    }

    public static float bound(float a, float b, float c) {
        float r = b;
        if (b < a) r = a;
        if (b > c) r = c;

        return r;
    }

    static int pow(int a, int n) {
        if (n == 0) return 1;

        if (n % 2 == 1) return MathUtils.pow(a, n - 1) * a;
        else {
            int b = MathUtils.pow(a, n / 2);
            return b * b;
        }
    }

    static float pow(float a, float n) {
        if (n == 0) return 1;

        if (n % 2 == 1) return MathUtils.pow(a, n - 1) * a;
        else {
            float b = MathUtils.pow(a, n / 2);
            return b * b;
        }
    }

    static long pow(long a, long n) {
        if (n == 0) return 1;

        if (n % 2 == 1) return pow(a, n - 1) * a;
        else {
            long b = pow(a, n / 2);
            return b * b;
        }
    }

    public static void sort(int[] array) {
        Arrays.sort(array);
    }

    public static void sort(float[] array) {
        Arrays.sort(array);
    }
}