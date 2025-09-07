package library.core;

import java.awt.Color;

public class color {

    public int r, g, b, a;

    public color(Color c) {
        this(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    public color(color c, double alpha) {
        this(c.r, c.g, c.b, alpha);
    }

    public color(double r, double g, double b, double alpha) {
        this.r = (int) MathHelper.constrain(r, 0, 255);
        this.g = (int) MathHelper.constrain(g, 0, 255);
        this.b = (int) MathHelper.constrain(b, 0, 255);
        this.a = (int) MathHelper.constrain(alpha, 0, 255);
    }

    public color(double r, double g, double b) {
        this(r, g, b, 255);
    }

    public color(double g, double a) {
        this(g, g, g, a);
    }

    public color(double g) {
        this(g, 255);
    }

    public color() {
        this(0);
    }

    /**
     * Values must be enclosed in parentheses. Assumes: __(colors)<br>
     * <br>
     * "colors" is either 1, 2, 3, or 4 values separated by commas.<br>
     * <br>
     * Example: color(255, 255) is the same as rgb(255) or rgb(255, 255, 255),
     * etc.<br>
     * <br>
     * Does not support non-rgb color spaces.
     */
    public color(String c) {
        c = c.toLowerCase().trim().replace(" ", "");
        int openParenIndex = c.indexOf('(');
        c = c.substring(openParenIndex + 1, c.length() - 1);

        String[] stringValues = c.split(",");
        double[] values = new double[stringValues.length];
        for (int i = 0; i < stringValues.length; i++) {
            values[i] = Double.parseDouble(stringValues[i]);
        }
        if (values.length == 1) {
            this.r = (int) MathHelper.constrain(values[0], 0, 255);
            this.g = (int) MathHelper.constrain(values[0], 0, 255);
            this.b = (int) MathHelper.constrain(values[0], 0, 255);
            this.a = 255;
        } else if (values.length == 2) {
            this.r = (int) MathHelper.constrain(values[0], 0, 255);
            this.g = (int) MathHelper.constrain(values[0], 0, 255);
            this.b = (int) MathHelper.constrain(values[0], 0, 255);
            this.a = (int) MathHelper.constrain(values[1], 0, 255);
        } else if (values.length == 3) {
            this.r = (int) MathHelper.constrain(values[0], 0, 255);
            this.g = (int) MathHelper.constrain(values[1], 0, 255);
            this.b = (int) MathHelper.constrain(values[2], 0, 255);
            this.a = 255;
        } else if (values.length == 4) {
            this.r = (int) MathHelper.constrain(values[0], 0, 255);
            this.g = (int) MathHelper.constrain(values[1], 0, 255);
            this.b = (int) MathHelper.constrain(values[2], 0, 255);
            this.a = (int) MathHelper.constrain(values[3], 0, 255);
        } else {
            throw new IllegalArgumentException("Invalid color string: " + c);
        }
    }

    public static color fromInt(int c) {
        return new color((c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF, (c >> 24) & 0xFF);
    }

    public static color fromHSB(double h, double s, double b) {
        Color c = Color.getHSBColor((float) h, (float) s, (float) b);
        return new color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    public int getRGB() {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public float getRed() {
        return r;
    }

    public float getGreen() {
        return g;
    }

    public float getBlue() {
        return b;
    }

    public float getAlpha() {
        return a;
    }

    public color setRed(double r) {
        this.r = (int) r;
        return this;
    }

    public color setGreen(double g) {
        this.g = (int) g;
        return this;
    }

    public color setBlue(double b) {
        this.b = (int) b;
        return this;
    }

    public color setAlpha(double a) {
        this.a = (int) a;
        return this;
    }

    public color setColor(color c) {
        color col = c.copy();
        this.r = col.r;
        this.g = col.g;
        this.b = col.b;
        this.a = col.a;
        return this;
    }

    float[] RGBtoHSB() {
        Color c = new Color(r, g, b);
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return hsb;
    }

    public float getHue() {
        float[] hsb = RGBtoHSB();
        return hsb[0];
    }

    public float getSaturation() {
        float[] hsb = RGBtoHSB();
        return hsb[1];
    }

    public float getBrightness() {
        float[] hsb = RGBtoHSB();
        return hsb[2];
    }

    public String toString() {
        return "color(" + r + ", " + g + ", " + b + ", " + a + ")";
    }

    public boolean equals(color c) {
        return r == c.r && g == c.g && b == c.b && a == c.a;
    }

    public Color toColor() {
        return new Color(r, g, b, a);
    }

    public color copy() {
        return new color(r, g, b, a);
    }

    /**
     * Returns a random color. RGB values are between 100 and 255.
     */
    public static color randomColor() {
        return new color(MathHelper.random(100, 255), MathHelper.random(100, 255), MathHelper.random(100, 255));
    }

}
