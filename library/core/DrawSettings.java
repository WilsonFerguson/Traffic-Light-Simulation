package library.core;

public class DrawSettings implements PConstants {
    int rectMode;
    int ellipseMode;
    int textAlign;

    color fillColor;
    color strokeColor;

    float strokeWeight;
    float textSize;
    String textFont;

    float rotation;
    PVector translation;
    float scale;

    public DrawSettings(int rectMode, int ellipseMode, int textAlign, color fillColor, color strokeColor,
            float strokeWeight, float textSize, String textFont, float rotation, PVector translation, float scale) {
        this.rectMode = rectMode;
        this.ellipseMode = ellipseMode;
        this.textAlign = textAlign;
        this.fillColor = fillColor.copy();
        this.strokeColor = strokeColor.copy();
        this.strokeWeight = strokeWeight;
        this.textSize = textSize;
        this.textFont = String.valueOf(textFont);
        this.rotation = rotation;
        this.translation = translation.copy();
        this.scale = scale;
    }

}
