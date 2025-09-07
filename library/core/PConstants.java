package library.core;

public interface PConstants {
    // Drawing modes
    public final int CENTER = 0;
    public final int CORNER = 4;
    public final int LEFT = 1;
    public final int RIGHT = 3;

    // Shape modes
    public final int SMOOTH = 1;
    public final int RIGID = 2;
    public final int CLOSE = 0;

    // Math
    public final float PI = (float) Math.PI;
    public final float HALF_PI = (float) Math.PI / 2;
    public final float QUARTER_PI = (float) Math.PI / 4;
    public final float TWO_PI = (float) Math.PI * 2;
    public final float TAU = (float) Math.PI * 2;
    public final float DEG_TO_RAD = (float) Math.PI / 180;
    public final float RAD_TO_DEG = 180 / (float) Math.PI;

    // Print modes
    public final String PRINT_BOLD = "\u001B[1m";
    public final String PRINT_ITALIC = "\u001B[3m";
    public final String PRINT_UNDERLINE = "\u001B[4m";
    public final String PRINT_STRIKETHROUGH = "\u001B[9m";

    // Print colors
    public final String PRINT_RESET = "\u001B[0m";
    public final String PRINT_BLACK = "\u001B[30m";
    public final String PRINT_WHITE = "\u001B[37m";
    public final String PRINT_RED = "\u001B[31m";
    public final String PRINT_GREEN = "\u001B[32m";
    public final String PRINT_YELLOW = "\u001B[33m";
    public final String PRINT_BLUE = "\u001B[34m";
    public final String PRINT_PURPLE = "\u001B[35m";

    // Print backgrounds
    public final String PRINT_BLACK_BACKGROUND = "\u001B[40m";
    public final String PRINT_WHITE_BACKGROUND = "\u001B[47m";
    public final String PRINT_RED_BACKGROUND = "\u001B[41m";
    public final String PRINT_GREEN_BACKGROUND = "\u001B[42m";
    public final String PRINT_YELLOW_BACKGROUND = "\u001B[43m";
    public final String PRINT_BLUE_BACKGROUND = "\u001B[44m";
    public final String PRINT_PURPLE_BACKGROUND = "\u001B[45m";

    // Noise functions

    /**
     * Simplex noise
     */
    public final int SIMPLEX = 0;

    /**
     * Voronoi noise (uses Simplex noise as a base)
     */
    public final int VORONOI = 1;

    /**
     * Worley noise (uses Simplex noise as a base)
     */
    public final int WORLEY = 2;

    /**
     * Ridged noise (uses Simplex noise as a base)
     */
    public final int RIDGED = 3;

    // Noise types
    public final int STANDARD = 0; // Not layered
    public final int FRACTAL = 1;

}