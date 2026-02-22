package GameEngine;

import library.core.*;

public class ColorPicker extends Interactable {

    /**
     * Goes from 0-1. See {@link #col} and {@link #getColor()} for the actual color.
     */
    public float hue = 0;
    /**
     * Goes from 0-1. See {@link #col} and {@link #getColor()} for the actual color.
     */
    public float saturation = 0.5f;
    /**
     * Goes from 0-1. See {@link #col} and {@link #getColor()} for the actual color.
     */
    public float brightness = 0.5f;
    public color col = color.fromHSB(hue, saturation, brightness);

    private PVector colorPreviewPosition;

    private float hueSelectorHeight = 0.05f;

    Runnable onChangeColor;

    // Defaults
    public static PVector DEFAULT_SIZE = new PVector(200, 200);
    public static color DEFAULT_STROKE_COLOR = new color(0);

    public ColorPicker(PVector pos, PVector size, color strokeColor) {
        this.pos = pos;
        this.size = size;
        this.strokeColor = strokeColor;

        cornerRadius = 0;

        setColorPreviewPosition();
    }

    public ColorPicker(double x, double y, double w, double h, color strokeColor) {
        this(new PVector(x, y), new PVector(w, h), strokeColor);
    }

    public ColorPicker(PVector pos, PVector size) {
        this(pos, size, DEFAULT_STROKE_COLOR);
    }

    public ColorPicker(double x, double y, double w, double h) {
        this(x, y, w, h, DEFAULT_STROKE_COLOR);
    }

    public ColorPicker(PVector pos) {
        this(pos, DEFAULT_SIZE.copy());
    }

    public ColorPicker(double x, double y) {
        this(new PVector(x, y), DEFAULT_SIZE.copy());
    }

    public ColorPicker copy() {
        ColorPicker copy = new ColorPicker(pos.copy(), size.copy(), strokeColor.copy());
        copy.onChangeColor(onChangeColor);
        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setCornerRadius(cornerRadius);
        copy.setStrokeWeight(strokeWeight);
        copy.setInteractive(interactive);
        copy.setActive(active);
        copy.setTextAlignment(textAlignment);
        copy.setAlpha(alpha);

        copy.hue = hue;
        copy.saturation = saturation;
        copy.brightness = brightness;
        copy.col = col.copy();
        copy.colorPreviewPosition = colorPreviewPosition.copy();
        copy.hueSelectorHeight = hueSelectorHeight;

        return copy;
    }

    public static void setDefaults(PVector defaultSize, color defaultStrokeColor) {
        DEFAULT_SIZE = defaultSize;
        DEFAULT_STROKE_COLOR = defaultStrokeColor;
    }

    /**
     * Sets the position of the preview based on the current color
     */
    private void setColorPreviewPosition() {
        float x = map(saturation, 0, 1, pos.x - size.x / 2, pos.x + size.x / 2);
        float y = map(brightness, 1, 0, pos.y - size.y / 2 + size.y * hueSelectorHeight,
                pos.y + size.y / 2);
        colorPreviewPosition = new PVector(x, y);
    }

    public ColorPicker setPos(PVector pos) {
        PVector posDiff = PVector.sub(pos, this.pos.copy());
        this.pos = pos;
        colorPreviewPosition.add(posDiff);

        return this;
    }

    public ColorPicker setPos(double x, double y) {
        return setPos(new PVector(x, y));
    }

    /**
     * @deprecated Warning: Corner radius has not been properly implemented for
     *             Color Picker and will look pretty bad.
     */
    @Deprecated
    public ColorPicker setCornerRadius(double cornerRadius) {
        super.setCornerRadius(cornerRadius);
        return this;
    }

    public ColorPicker setColor(color col) {
        this.col = col;
        this.hue = col.getHue();
        this.saturation = col.getSaturation();
        this.brightness = col.getBrightness();
        setColorPreviewPosition();
        return this;
    }

    public color getColor() {
        return col;
    }

    public ColorPicker onChangeColor(Runnable onChangeColor) {
        this.onChangeColor = onChangeColor;
        return this;
    }

    public void onChangeColor() {
        if (onChangeColor != null) {
            onChangeColor.run();
        }
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

        if (isHovered && mousePressed && mouseButton == LEFT && interactive) {
            // Check if on hue selector
            if (mouseY < pos.y + -size.y / 2 + size.y * hueSelectorHeight) {
                // On hue selector
                hue = (mouseX - pos.x) / size.x + 0.5f; // Adding 0.5 just made it work?
                col = color.fromHSB(hue, saturation, brightness);
                onChangeColor();
            } else {
                // On color selector
                saturation = map(mouseX, pos.x - size.x / 2, pos.x + size.x / 2, 0, 1);
                brightness = map(mouseY, pos.y - size.y / 2 + size.y * hueSelectorHeight, pos.y + size.y / 2, 1, 0);
                col = color.fromHSB(hue, saturation, brightness);
                colorPreviewPosition = new PVector(mouseX, mouseY);
                onChangeColor();
            }
        }

        // Hue picker (top 10% of the box)
        for (float i = 0; i < size.x; i++) {
            float h = i / size.x;
            color c = color.fromHSB(h, 1, 1);
            float x = pos.x + i - size.x / 2;
            for (float j = 0; j < size.y * hueSelectorHeight; j++) {
                float y = pos.y + j - size.y / 2;
                set(x, y, color(c, alpha));
            }
        }

        // Draw the color picker main box
        for (float i = 0; i < size.x; i++) {
            for (float j = 0; j < size.y * (1 - hueSelectorHeight); j++) {
                float h = hue;
                float s = i / size.x;
                float b = 1 - j / (size.y * (1 - hueSelectorHeight));
                color c = color.fromHSB(h, s, b);

                PVector pos = new PVector(this.pos.x + i - size.x / 2,
                        this.pos.y + j - size.y / 2 + size.y * hueSelectorHeight);
                set(pos, color(c, alpha));
            }
        }

        // Draw background for stroke
        rectMode(CENTER);
        strokeWeight(strokeWeight);
        stroke(strokeColor, strokeColor.a * (alpha / 255.0f));
        noFill();
        rect(pos, size, cornerRadius);

        // Draw the color preview
        strokeWeight(size.x / 60);
        stroke(255, alpha);
        fill(col, alpha);
        circle(colorPreviewPosition, size.x / 12);
    }
}
