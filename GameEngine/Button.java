package GameEngine;

import library.core.*;

public class Button extends Interactable {

    private Runnable onClick;

    // Defaults
    private static Button defaultButton = new Button(0, 0, 300, 100, new color(200), new color(230), new color(180),
            new color(0), "Button", 20, new color(0));

    private static PVector DEFAULT_SIZE = new PVector(300, 100);
    private static color DEFAULT_COLOR = new color(255);
    private static color HOVER_COLOR = new color(200);
    private static color ACTIVE_COLOR = new color(150);
    private static color STROKE_COLOR = new color(0);
    private static int TEXT_SIZE = 20;
    private static color TEXT_COLOR = new color(0);

    public Button(PVector pos, PVector size, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, String text, int textSize, color textColor) {
        this.pos = pos;
        this.size = size;

        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.strokeColor = strokeColor;

        this.text = text;
        this.textSize = textSize;
        this.textColor = textColor;

        currentColor = defaultColor;
    }

    public Button(double x, double y, double w, double h, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, String text, int textSize, color textColor) {
        this(new PVector(x, y), new PVector(w, h), defaultColor, hoverColor, activeColor, strokeColor, text, textSize,
                textColor);
    }

    public Button(PVector pos, PVector size, String text) {
        // this(pos, size, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
        // ACTIVE_COLOR.copy(), STROKE_COLOR.copy(), text,
        // TEXT_SIZE, TEXT_COLOR.copy());
        // this(pos, size, defaultButton.defaultColor.copy(),
        // defaultButton.hoverColor.copy(),
        // defaultButton.activeColor.copy(),
        // defaultButton.strokeColor.copy(), text, defaultButton.textSize,
        // defaultButton.textColor.copy());
        clone(defaultButton);
        this.pos = pos;
        this.size = size;
        this.text = text;
    }

    public Button(double x, double y, double w, double h, String text) {
        // this(x, y, w, h, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
        // ACTIVE_COLOR.copy(), STROKE_COLOR.copy(), text,
        // TEXT_SIZE, TEXT_COLOR.copy());
        // this(x, y, w, h, defaultButton.defaultColor.copy(),
        // defaultButton.hoverColor.copy(),
        // defaultButton.activeColor.copy(),
        // defaultButton.strokeColor.copy(), text, defaultButton.textSize,
        // defaultButton.textColor.copy());
        this(new PVector(x, y), new PVector(w, h), text);
    }

    public Button(PVector pos, String text) {
        // this(pos, DEFAULT_SIZE.copy(), DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
        // ACTIVE_COLOR.copy(),
        // STROKE_COLOR.copy(), text, TEXT_SIZE, TEXT_COLOR.copy());
        // this(pos, defaultButton.size.copy(), defaultButton.defaultColor.copy(),
        // defaultButton.hoverColor.copy(),
        // defaultButton.activeColor.copy(), defaultButton.strokeColor.copy(), text,
        // defaultButton.textSize,
        // defaultButton.textColor.copy());
        this(pos, defaultButton.size.copy(), text);
    }

    public Button(double x, double y, String text) {
        // this(x, y, DEFAULT_SIZE.copy().x, DEFAULT_SIZE.copy().y,
        // DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
        // ACTIVE_COLOR.copy(), STROKE_COLOR.copy(), text,
        // TEXT_SIZE, TEXT_COLOR.copy());
        // this(x, y, defaultButton.size.copy().x, defaultButton.size.copy().y,
        // defaultButton.defaultColor.copy(),
        // defaultButton.hoverColor.copy(), defaultButton.activeColor.copy(),
        // defaultButton.strokeColor.copy(),
        // text,
        // defaultButton.textSize, defaultButton.textColor.copy());
        this(new PVector(x, y), text);
    }

    public Button copy() {
        Button copy = new Button(pos.copy(), size.copy(), defaultColor.copy(), hoverColor.copy(), activeColor.copy(),
                strokeColor.copy(), text, textSize, textColor.copy());
        copy.onClick(onClick);
        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setCornerRadius(cornerRadius);
        copy.setInteractive(interactive);
        copy.setActive(active);
        copy.setTextAlignment(textAlignment);
        return copy;
    }

    public void clone(Button button) {
        pos = button.pos.copy();
        size = button.size.copy();
        defaultColor = button.defaultColor.copy();
        hoverColor = button.hoverColor.copy();
        activeColor = button.activeColor.copy();
        strokeColor = button.strokeColor.copy();
        text = button.text;
        textSize = button.textSize;
        textColor = button.textColor.copy();
        onClick = button.onClick;
        onHover = button.onHover;
        onHoverExit = button.onHoverExit;
        cornerRadius = button.cornerRadius;
        interactive = button.interactive;
        active = button.active;
        textAlignment = button.textAlignment;

        currentColor = defaultColor;
    }

    public static void setDefaults(Button defaults) {
        defaultButton = defaults;
    }

    public static Button getDefaults() {
        return defaultButton;
    }

    public static void setDefaults(PVector defaultSize, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, int textSize, color textColor) {
        DEFAULT_SIZE = defaultSize;
        DEFAULT_COLOR = defaultColor;
        HOVER_COLOR = hoverColor;
        ACTIVE_COLOR = activeColor;
        STROKE_COLOR = strokeColor;
        TEXT_SIZE = textSize;
        TEXT_COLOR = textColor;

        defaultButton = new Button(PVector.zero(), defaultSize, defaultColor, hoverColor, activeColor, strokeColor, "", textSize, textColor);
    }

    public Button setCornerRadius(double radius) {
        this.cornerRadius = (int) radius;
        return this;
    }

    public Button onClick(Runnable onClick) {
        this.onClick = onClick;
        return this;

    }

    public void onClick() {
        if (onClick != null)
            onClick.run();
    }

    public void mousePressed() {
        if (hover() && interactive && active)
            onClick();
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

        color targetColor = defaultColor;
        if (isHovered) {
            if (mousePressed) {
                targetColor = activeColor;
            } else {
                targetColor = hoverColor;
            }
        }
        currentColor = lerpColor(currentColor, targetColor, Animator.colorLerpAmount);
        fill(currentColor);

        if (!interactive)
            fill(defaultColor);

        stroke(strokeColor);
        strokeWeight(2);
        rectMode(CENTER);
        rect(pos, size, cornerRadius);

        drawText();
    }

    public void drawText() {
        fill(textColor);
        textSize(textSize);
        textAlign(textAlignment);

        PVector textPos = pos.copy();
        if (textAlignment == TextAlignment.LEFT)
            textPos.x -= size.x / 2.1;
        else if (textAlignment == TextAlignment.RIGHT)
            textPos.x += size.x / 2.1;
        else if (textAlignment == TextAlignment.CENTER)
            textPos.x = pos.x;

        text(text, textPos);
    }

}
