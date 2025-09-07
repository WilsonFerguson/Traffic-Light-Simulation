package GameEngine;

import library.core.*;

public class Text extends Interactable implements EventIgnorer {

    // Defaults
    public static int TEXT_SIZE = 20;
    public static color TEXT_COLOR = new color(0);

    public Text(PVector pos, String text, int textSize, color textColor) {
        this.text = text;
        this.pos = pos;
        this.textSize = textSize;
        this.textColor = textColor;

        calculateSize();
    }

    public Text(double x, double y, String text, int textSize, color textColor) {
        this(new PVector(x, y), text, textSize, textColor);
    }

    public Text(PVector pos, String text) {
        this(pos, text, TEXT_SIZE, TEXT_COLOR.copy());
    }

    public Text(double x, double y, String text) {
        this(x, y, text, TEXT_SIZE, TEXT_COLOR.copy());
    }

    public static void setDefaults(int textSize, color textColor) {
        TEXT_SIZE = textSize;
        TEXT_COLOR = textColor;
    }

    public Text setText(String text) {
        this.text = text;
        calculateSize();
        return this;
    }

    public Text setTextSize(int textSize) {
        this.textSize = textSize;
        calculateSize();
        return this;
    }

    public Text calculateSize() {
        textSize(textSize);
        size = new PVector(textWidth(text), textHeight(text));
        return this;
    }

    public void draw() {
        if (!active)
            return;

        boolean previouslyHovered = isHovered;
        isHovered = hover();
        if (!previouslyHovered && isHovered)
            onHover();
        else if (previouslyHovered && !isHovered)
            onHoverExit();

        textSize(textSize);
        fill(textColor);
        textAlign(textAlignment);
        text(text, pos.x, pos.y);
    }

}