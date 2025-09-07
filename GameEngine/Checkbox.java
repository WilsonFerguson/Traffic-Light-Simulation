package GameEngine;

import library.core.*;

public class Checkbox extends Interactable {

    public boolean checked = false;

    private int checkThickness = 3;
    private boolean checkMark = false;

    private Runnable onToggle;

    // Defaults
    public static PVector DEFAULT_SIZE = new PVector(100, 100);
    public static color DEFAULT_COLOR = new color(255);
    public static color HOVER_COLOR = new color(200);
    public static color ACTIVE_COLOR = new color(150);
    public static color STROKE_COLOR = new color(0);
    public static color CHECK_COLOR = new color(0);

    public Checkbox(PVector pos, double w, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, color checkColor) {
        this.pos = pos;
        this.size = new PVector(w, w);

        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.strokeColor = strokeColor;

        this.textColor = checkColor;

        currentColor = defaultColor;
    }

    public Checkbox(double x, double y, double w, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, color checkColor) {
        this(new PVector(x, y), w, defaultColor, hoverColor, activeColor, strokeColor,
                checkColor);
    }

    public Checkbox(PVector pos, double w) {
        this(pos, w, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(), STROKE_COLOR.copy(),
                CHECK_COLOR.copy());
    }

    public Checkbox(double x, double y, double w) {
        this(x, y, w, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(), STROKE_COLOR.copy(),
                CHECK_COLOR.copy());
    }

    public Checkbox(PVector pos) {
        this(pos, DEFAULT_SIZE.copy().x, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(),
                STROKE_COLOR.copy(),
                CHECK_COLOR.copy());
    }

    public Checkbox(double x, double y) {
        this(x, y, DEFAULT_SIZE.copy().x, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(),
                STROKE_COLOR.copy(),
                CHECK_COLOR.copy());
    }

    public static void setDefaults(double defaultW, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, color checkColor) {
        DEFAULT_SIZE = new PVector(defaultW, defaultW);
        DEFAULT_COLOR = defaultColor;
        HOVER_COLOR = hoverColor;
        ACTIVE_COLOR = activeColor;
        STROKE_COLOR = strokeColor;
        CHECK_COLOR = checkColor;
    }

    public Checkbox setCornerRadius(double radius) {
        this.cornerRadius = (int) radius;
        return this;
    }

    public void mousePressed() {
        if (hover() && interactive && active) {
            checked = !checked;
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public Checkbox setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }

    public Checkbox onToggle(Runnable onToggle) {
        this.onToggle = onToggle;
        return this;
    }

    public void onToggle() {
        if (onToggle != null)
            onToggle.run();
    }

    public Checkbox setCheckThickness(int checkThickness) {
        this.checkThickness = checkThickness;
        return this;
    }

    /**
     * Sets whether or not the checkbox should have a check mark or an X.
     * 
     * @param checkMark
     */
    public Checkbox setCheckMark(boolean checkMark) {
        this.checkMark = checkMark;
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

        strokeWeight(checkThickness);
        stroke(textColor);
        if (checked) {
            push();
            translate(pos.x - size.x / 2, pos.y - size.y / 2);
            if (checkMark) {
                line(size.x / 4, size.y / 2, size.x / 2, size.y * 3 / 4);
                line(size.x / 2, size.y * 3 / 4, size.x * 3 / 4, size.y / 4);
            } else {
                line(size.x / 4, size.y / 4, size.x * 3 / 4, size.y * 3 / 4);
                line(size.x / 4, size.y * 3 / 4, size.x * 3 / 4, size.y / 4);
            }
            pop();
        }
    }

}