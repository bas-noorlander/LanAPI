package scripts.lanapi.core.mathematics;

/**
 * Provides math logic which is generaly father then Math.
 *
 * @author Laniax
 */
public final class FastMath {

    /**
     * Value used to round floats.
     */
    public static final float roundingValue = 0.5f;

    /**
     * Less precise Math.E but faster.
     */
    public static final float E = (float) Math.E;

    /**
     * Less precise Math.PI but faster.
     */
    public static final float PI = (float) Math.PI;

    /**
     * PI * 2
     */
    public static final float PI2 = PI * 2;


    /**
     * Ensures that the value is between min and max
     *
     * @param value
     * @param min
     * @param max
     * @return the new value, is guaranteed between min & max.
     */
    public static byte minMax(byte value, byte min, byte max) {
        if (value > max)
            return max;

        return value < min ? min : value;
    }

    /**
     * Ensures that the value is between min and max
     *
     * @param value
     * @param min
     * @param max
     * @return the new value, is guaranteed between min & max.
     */
    public static short minMax(short value, short min, short max) {
        if (value > max)
            return max;

        return value < min ? min : value;
    }

    /**
     * Ensures that the value is between min and max
     *
     * @param value
     * @param min
     * @param max
     * @return the new value, is guaranteed between min & max.
     */
    public static int minMax(int value, int min, int max) {
        if (value > max)
            return max;

        return value < min ? min : value;
    }

    /**
     * Ensures that the value is between min and max
     *
     * @param value
     * @param min
     * @param max
     * @return the new value, is guaranteed between min & max.
     */
    public static long minMax(long value, long min, long max) {
        if (value > max)
            return max;

        return value < min ? min : value;
    }

    /**
     * Ensures that the value is between min and max
     *
     * @param value
     * @param min
     * @param max
     * @return the new value, is guaranteed between min & max.
     */
    public static float minMax(float value, float min, float max) {
        if (value > max)
            return max;

        return value < min ? min : value;
    }

    /**
     * Ensures that the value is between min and max
     *
     * @param value
     * @param min
     * @param max
     * @return the new value, is guaranteed between min & max.
     */
    public static double minMax(double value, double min, double max) {
        if (value > max)
            return max;

        return value < min ? min : value;
    }

}
