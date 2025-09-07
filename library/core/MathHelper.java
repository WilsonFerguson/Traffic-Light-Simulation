package library.core;

public class MathHelper {
    /**
     * Returns a {@code float} distance between two points given {@code int} x1,
     * {@code int} y1, {@code int} x2, and {@code int} y2.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return float
     */
    public static float getDistance(double x1, double y1, double x2, double y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Returns a {@code float} distance between two points given {@code PVector} p1
     * and {@code PVector} p2.
     * 
     * @param p1
     * @param p2
     * @return float
     */
    public static float getDistance(PVector p1, PVector p2) {
        return getDistance(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * Returns a {@code float} random value between a {@code float} min and
     * {@code float} max.
     * 
     * @param min
     * @param max
     * @return float
     */
    public static float random(double min, double max) {
        return (float) (Math.random() * (max - min) + min);
    }

    /**
     * Returns a {@code float} random value between 0 and {@code float} max.
     * 
     * @param max
     * @return float
     */
    public static float random(double max) {
        return random(0, max);
    }

    /**
     * Returns a {@code float} random value between 0 and 1.
     * 
     * @return float
     */
    public static float random() {
        return random(1.0);
    }

    /**
     * Returns a random {@code char} from the given {@code char[]}.
     * 
     * @param array
     * @return char
     */
    public static char random(char[] array) {
        if (array.length == 0)
            return 0;
        return array[(int) random(array.length)];
    }

    /**
     * Returns a random {@code byte} from the given {@code byte[]}.
     * 
     * @param array
     * @return byte
     */
    public static byte random(byte[] array) {
        if (array.length == 0)
            return 0;
        return array[(int) random(array.length)];
    }

    /**
     * Returns a random {@code int} from the given {@code int[]}.
     * 
     * @param array
     * @return int
     */
    public static int random(int[] array) {
        if (array.length == 0)
            return 0;
        return array[(int) random(array.length)];
    }

    /**
     * Returns a random {@code long} from the given {@code long[]}.
     * 
     * @param array
     * @return long
     */
    public static long random(long[] array) {
        if (array.length == 0)
            return 0;
        return array[(int) random(array.length)];
    }

    /**
     * Returns a random {@code float} from the given {@code float[]}.
     * 
     * @param array
     * @return float
     */
    public static float random(float[] array) {
        if (array.length == 0)
            return 0;
        return array[(int) random(array.length)];
    }

    /**
     * Returns a random {@code double} from the given {@code double[]}.
     * 
     * @param array
     * @return double
     */
    public static double random(double[] array) {
        if (array.length == 0)
            return 0;
        return array[(int) random(array.length)];
    }

    /**
     * Returns a random {@code boolean} from the given {@code boolean[]}.
     * 
     * @param array
     * @return boolean
     */
    public static boolean random(boolean[] array) {
        if (array.length == 0)
            return false;
        return array[(int) random(array.length)];
    }

    /**
     * Returns a random {@code String} from the given {@code String[]}.
     * 
     * @param array
     * @return String
     */
    public static String random(String[] array) {
        if (array.length == 0)
            return "";
        return array[(int) random(array.length)];
    }

    /**
     * Returns a random {@code PVector} from the given {@code PVector[]}.
     * 
     * @param array
     * @return PVector
     */
    public static PVector random(PVector[] array) {
        if (array.length == 0)
            return PVector.zero();
        return array[(int) random(array.length)];
    }

    /**
     * Returns a random {@code Object} from the given {@code Object[]}.
     * 
     * @param array
     * @return Object
     */
    public static Object random(Object[] array) {
        if (array.length == 0)
            return null;
        return array[(int) random(array.length)];
    }

    /**
     * Returns a random {@code color} from the given {@code color[]}.
     * 
     * @param array
     * @return color
     */
    public static color random(color[] array) {
        if (array.length == 0)
            return new color(0);
        return array[(int) random(array.length)];
    }

    /**
     * Maps a given {@code float} value from one range to another.
     * 
     * @param value
     * @param start1
     * @param stop1
     * @param start2
     * @param stop2
     * @return float
     */
    public static float map(double value, double start1, double stop1, double start2, double stop2) {
        return (float) (start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1)));
    }

    /**
     * Maps a given {@code int} value from one range to another.
     * 
     * @param value
     * @param start1
     * @param stop1
     * @param start2
     * @param stop2
     * @return int
     */
    public static int map(int value, int start1, int stop1, int start2, int stop2) {
        return (int) map((float) value, (float) start1, (float) stop1, (float) start2, (float) stop2);
    }

    /**
     * Returns a constrained {@code float} value.
     */
    public static float constrain(double value, double min, double max) {
        return (float) Math.min(Math.max(value, min), max);
    }

    /**
     * Returns a constrained {@code int} value.
     */
    public static int constrain(int value, int min, int max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    /**
     * Normalizes a value from an arbitrary range to a value between 0 and 1.
     * 
     * @param value
     * @param min
     * @param max
     * 
     * @return float
     */
    public static float norm(double value, double min, double max) {
        return (float) ((value - min) / (max - min));
    }

    /**
     * Returns the digit at the given {@code int} place in the given {@code int}
     * value. <br>
     * <br>
     * Example: getDigit(1234, 10) returns 3 because it gets the tens place.
     * 
     * @param value
     * @param position
     * @return int
     */
    public static int getDigit(int value, int position) {
        // 1s place is the last digit
        // 10s place is the second to last digit etc.
        return (int) (value / position) % 10;
    }

    // Math Functions
    /**
     * Returns the absolute value of the given {@code float} value.
     * 
     * @param value
     * @return float
     */
    public static float abs(double value) {
        return (float) Math.abs(value);
    }

    /**
     * Returns the absolute value of the given {@code int} value.
     * 
     * @param value
     * @return int
     */
    public static int abs(int value) {
        return Math.abs(value);
    }

    /**
     * Returns the cosine of the given {@code float} value.
     * 
     * @param value
     * @return float
     */
    public static float cos(double value) {
        return (float) Math.cos(value);
    }

    /**
     * Returns the sine of the given {@code float} value.
     * 
     * @param value
     * @return float
     */
    public static float sin(double value) {
        return (float) Math.sin(value);
    }

    /**
     * Returns the tangent of the given {@code float} value.
     * 
     * @param value
     * @return float
     */
    public static float tan(double value) {
        return (float) Math.tan(value);
    }

    /**
     * Returns the arc cosine of the given {@code float} value.
     * 
     * @param value
     * @return float
     */
    public static float acos(double value) {
        return (float) Math.acos(value);
    }

    /**
     * Returns the arc sine of the given {@code float} value.
     * 
     * @param value
     * @return float
     */
    public static float asin(double value) {
        return (float) Math.asin(value);
    }

    /**
     * Returns the arc tangent of the given {@code float} value.
     * 
     * @param value
     * @return float
     */
    public static float atan(double value) {
        return (float) Math.atan(value);
    }

    /**
     * Returns the arc tangent of the given {@code float} y and {@code float} x.
     * 
     * @param y
     * @param x
     * @return float
     */
    public static float atan2(double y, double x) {
        return (float) Math.atan2(y, x);
    }

    /**
     * Returns the square root of the given {@code float} value.
     * 
     * @param value
     * @return float
     */
    public static float sqrt(double value) {
        return (float) Math.sqrt(value);
    }

    /**
     * Returns the given {@code float} value raised to the given {@code float}
     * 
     * @param value
     * @param power
     * @return float
     */
    public static float pow(double value, double power) {
        return (float) Math.pow(value, power);
    }
}
