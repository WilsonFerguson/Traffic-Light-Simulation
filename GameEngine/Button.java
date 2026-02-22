package GameEngine;

import library.core.*;

public class Button extends Interactable {

    private Runnable onClick;

    // Defaults
    private static Button defaultButton = new Button(0, 0, 300, 100, new color(200), new color(230), new color(180),
            new color(0), "Button", 20, new color(0));

    public Button(PVector pos, PVector size, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, String text, double textSize, color textColor) {
        this.pos = pos;
        this.size = size;

        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.strokeColor = strokeColor;

        this.text = text;
        this.textSize = (float) textSize;
        this.textColor = textColor;

        currentColor = defaultColor;
    }

    public Button(double x, double y, double w, double h, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, String text, double textSize, color textColor) {
        this(new PVector(x, y), new PVector(w, h), defaultColor, hoverColor, activeColor, strokeColor, text, textSize,
                textColor);
    }

    public Button(PVector pos, PVector size, String text) {
        clone(defaultButton);
        this.pos = pos;
        this.size = size;
        this.text = text;
    }

    public Button(double x, double y, double w, double h, String text) {
        this(new PVector(x, y), new PVector(w, h), text);
    }

    public Button(PVector pos, String text) {
        this(pos, defaultButton.size.copy(), text);
    }

    public Button(double x, double y, String text) {
        this(new PVector(x, y), text);
    }

    public Button copy() {
        Button copy = new Button(pos.copy(), size.copy(), defaultColor.copy(), hoverColor.copy(), activeColor.copy(),
                strokeColor.copy(), new String(text), textSize, textColor.copy());
        copy.onClick(onClick);
        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setCornerRadius(cornerRadius);
        copy.setInteractive(interactive);
        copy.setActive(active);
        copy.setTextAlignment(textAlignment);
        copy.setAlpha(alpha);
        copy.setCornerRadius(cornerRadius);
        copy.setStrokeWeight(strokeWeight);
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

    public static void setDefaults(PVector defaultSize, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, double textSize, color textColor) {
        defaultButton = new Button(PVector.zero(), defaultSize, defaultColor, hoverColor, activeColor, strokeColor, "",
                textSize, textColor);
    }

    public static Button getDefaults() {
        return defaultButton;
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
        if (interactive && active && mouseButton == LEFT && hover())
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
        fill(currentColor, currentColor.a * (alpha / 255.0f));

        if (!interactive)
            fill(defaultColor, defaultColor.a * (alpha / 255.0f));

        stroke(strokeColor, strokeColor.a * (alpha / 255.0f));
        strokeWeight(strokeWeight);
        rectMode(CENTER);
        rect(pos, size, cornerRadius);

        drawText();
    }

    public void drawText() {
        fill(textColor, textColor.a * (alpha / 255.0f));
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
