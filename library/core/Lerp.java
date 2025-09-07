package library.core;

public class Lerp {

    /**
     * Linearly lerps between two {@code float} values.
     * 
     * @param start
     * @param stop
     * @param amt
     * 
     * @return float
     */
    public static float lerp(double start, double stop, double amt) {
        return (float) (start + (stop - start) * amt);
    }

    /**
     * Smoothly lerps between two {@code float} values.
     * 
     * @param start
     * @param stop
     * @param amt
     * 
     * @return float
     */
    public static float lerpSmooth(double start, double stop, double amt) {
        // return lerp(start, stop, amt * amt * (3 - 2 * amt));

        double progress = lerp(-Math.PI / 2, Math.PI / 2, amt);
        progress = Math.sin(progress);
        progress = (progress / 2) + 0.5;
        return lerp(start, stop, progress);

        // return lerp(start, stop, easeInOut(amt));
    }

    /**
     * Overshoot lerps between two {@code float} values.
     * 
     * @param start
     * @param stop
     * @param amt
     */
    public static float lerpOvershoot(double start, double stop, double amt) {
        double x = amt * 4.2;
        if (x >= 4.2) {
            return (float) stop;
        }
        double progress = 1 - ((Math.sin(x) / x) * (Math.sin(3 * x) / (3 * x)));
        return lerp(start, stop, progress);
    }

    /**
     * Returns a {@code float} amount that is eased in from the given {@code float}
     * amt.
     * 
     * @param amt
     * 
     * @return float
     */
    public static float easeIn(double amt) {
        return (float) (amt * amt);
    }

    /**
     * Returns a {@code float} amount that is flipped from the given {@code float}
     * amt.
     * 
     * @param amt
     * 
     * @return float
     */
    public static float flip(double amt) {
        return (float) (1 - amt);
    }

    /**
     * Returns a {@code float} amount that is eased out from the given {@code float}
     * amt.
     * 
     * @param amt
     * 
     * @return float
     */
    public static float easeOut(double amt) {
        return flip(easeIn(flip(amt)));
    }

    /**
     * Returns a {@code float} amount that is eased in and out from the given
     * {@code float} amt.
     * 
     * @param amt
     * 
     * @return float
     */
    public static float easeInOut(double amt) {
        return lerp(easeIn(amt), easeOut(amt), amt);
    }

}
