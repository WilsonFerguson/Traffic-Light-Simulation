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
    public float saturation = 1;
    /**
     * Goes from 0-1. See {@link #col} and {@link #getColor()} for the actual color.
     */
    public float brightness = 1;
    public color col = color.fromHSB(hue, saturation, brightness);

    private PVector colorPreviewPosition;

    private float hueSelectorHeight = 0.05f;

    Runnable onChangeColor;

    // Defaults
    public static PVector DEFAULT_SIZE = new PVector(200, 200);

    public ColorPicker(PVector pos, PVector size) {
        this.pos = pos;
        this.size = size;

        colorPreviewPosition = new PVector(pos.x + size.x / 2, pos.y - size.y / 2 + size.y * hueSelectorHeight / 2);
    }

    public ColorPicker(double x, double y, double w, double h) {
        this(new PVector(x, y), new PVector(w, h));
    }

    public ColorPicker(PVector pos) {
        this(pos, DEFAULT_SIZE.copy());
    }

    public ColorPicker(double x, double y) {
        this(new PVector(x, y), DEFAULT_SIZE.copy());
    }

    public static void setDefaults(PVector defaultSize) {
        DEFAULT_SIZE = defaultSize;
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

        if (isHovered && mousePressed && interactive) {
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
                set(x, y, c);
            }
        }

        // Draw the color picker main box
        for (float i = 0; i < size.x; i++) {
            for (float j = 0; j < size.y * (1 - hueSelectorHeight); j++) {
                float h = hue;
                float s = i / size.x;
                float b = 1 - j / (size.y * (1 - hueSelectorHeight));
                color c = color.fromHSB(h, s, b);
                // set(pos.x + i, pos.y + j, c);
                // Centered:
                PVector pos = new PVector(this.pos.x + i - size.x / 2,
                        this.pos.y + j - size.y / 2 + size.y * hueSelectorHeight);
                set(pos, c);
            }
        }

        // Draw the color preview
        strokeWeight(5);
        stroke(255);
        fill(col);
        circle(colorPreviewPosition, 20);
    }

}
