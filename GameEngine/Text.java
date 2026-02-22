package GameEngine;

import library.core.*;

public class Text extends Interactable implements EventIgnorer {

    // Defaults
    public static float TEXT_SIZE = 20;
    public static color TEXT_COLOR = new color(0);

    public Text(PVector pos, String text, double textSize, color textColor) {
        this.text = text;
        this.pos = pos;
        this.textSize = (float) textSize;
        this.textColor = textColor;

        calculateSize();
    }

    public Text(double x, double y, String text, double textSize, color textColor) {
        this(new PVector(x, y), text, textSize, textColor);
    }

    public Text(PVector pos, String text) {
        this(pos, text, TEXT_SIZE, TEXT_COLOR.copy());
    }

    public Text(double x, double y, String text) {
        this(x, y, text, TEXT_SIZE, TEXT_COLOR.copy());
    }

    public Text copy() {
        Text copy = new Text(pos.copy(), new String(text), textSize, textColor.copy());
        copy.setSize(size.copy());
        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setActive(active);
        copy.defaultColor = defaultColor.copy();
        copy.hoverColor = hoverColor.copy();
        copy.activeColor = activeColor.copy();
        copy.strokeColor = strokeColor.copy();
        copy.textColor = textColor.copy();
        copy.setTextAlignment(textAlignment);
        copy.setAlpha(alpha);
        copy.setCornerRadius(cornerRadius);
        copy.setStrokeWeight(strokeWeight);
        copy.setInteractive(interactive);

        return copy;
    }

    public static void setDefaults(double textSize, color textColor) {
        TEXT_SIZE = (float) textSize;
        TEXT_COLOR = textColor;
    }

    public Text setText(String text) {
        this.text = text;
        calculateSize();
        return this;
    }

    public Text setTextSize(double textSize) {
        this.textSize = (float) textSize;
        calculateSize();
        return this;
    }

    public Text setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
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
        fill(textColor, textColor.a * (alpha / 255.0f));
        textAlign(textAlignment);
        text(text, pos.x, pos.y);
    }
}
