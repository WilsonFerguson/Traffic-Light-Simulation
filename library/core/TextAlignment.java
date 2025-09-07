package library.core;

public enum TextAlignment {

    LEFT,
    CENTER,
    RIGHT;

    public static TextAlignment fromInt(int i) {
        switch (i) {
            case PConstants.CENTER:
                return CENTER;
            case PConstants.LEFT:
                return LEFT;
            case PConstants.RIGHT:
                return RIGHT;
            default:
                return LEFT;
        }
    }

    public int toInt() {
        switch (this) {
            case CENTER:
                return PConstants.CENTER;
            case LEFT:
                return PConstants.LEFT;
            case RIGHT:
                return PConstants.RIGHT;
            default:
                return PConstants.LEFT;
        }
    }

}
