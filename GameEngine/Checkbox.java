package GameEngine;

import library.core.*;

public class Checkbox extends Interactable {

    public boolean checked = false;

    private double checkThickness = 3;
    private boolean checkMark = false;

    private Runnable onToggle;

    private static Checkbox defaultCheckbox = new Checkbox(0, 0, 100, new color(255), new color(200),
            new color(150), new color(0), new color(0));

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
        clone(defaultCheckbox);
        this.pos = pos;
        this.size = new PVector(w, w);
    }

    public Checkbox(double x, double y, double w) {
        this(new PVector(x, y), w);
    }

    public Checkbox(PVector pos) {
        this(pos, defaultCheckbox.size.copy().x);
    }

    public Checkbox(double x, double y) {
        this(new PVector(x, y));
    }

    public Checkbox copy() {
        Checkbox copy = new Checkbox(pos.copy(), size.copy().x, defaultColor.copy(), hoverColor.copy(),
                activeColor.copy(),
                strokeColor.copy(), textColor.copy());
        copy.onToggle(onToggle);
        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setCornerRadius(cornerRadius);
        copy.setStrokeWeight(strokeWeight);
        copy.setInteractive(interactive);
        copy.setActive(active);
        copy.setAlpha(alpha);
        copy.setTextAlignment(textAlignment);
        copy.setCheckThickness(checkThickness);
        copy.setCheckMark(checkMark);
        copy.setChecked(checked);

        return copy;
    }

    public void clone(Checkbox checkbox) {
        pos = checkbox.pos.copy();
        size = checkbox.size.copy();
        defaultColor = checkbox.defaultColor.copy();
        hoverColor = checkbox.hoverColor.copy();
        activeColor = checkbox.activeColor.copy();
        strokeColor = checkbox.strokeColor.copy();
        textColor = checkbox.textColor.copy();
        currentColor = checkbox.currentColor.copy();
        alpha = checkbox.alpha;
        interactive = checkbox.interactive;
        active = checkbox.active;
        cornerRadius = checkbox.cornerRadius;
        checkThickness = checkbox.checkThickness;
        checkMark = checkbox.checkMark;
        onToggle = checkbox.onToggle;
        onHover = checkbox.onHover;
        onHoverExit = checkbox.onHoverExit;
    }

    public static void setDefaults(Checkbox defaults) {
        defaultCheckbox = defaults;
    }

    public static void setDefaults(double defaultW, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, color checkColor) {
        defaultCheckbox = new Checkbox(0, 0, defaultW, defaultColor, hoverColor, activeColor, strokeColor,
                checkColor);
    }

    public static Checkbox getDefaults() {
        return defaultCheckbox;
    }

    public Checkbox setCornerRadius(double radius) {
        this.cornerRadius = (int) radius;
        return this;
    }

    public void mousePressed() {
        if (interactive && active && mouseButton == LEFT && hover()) {
            checked = !checked;
            onToggle();
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

    public Checkbox setCheckThickness(double checkThickness) {
        this.checkThickness = checkThickness;
        return this;
    }

    public double getCheckThickness() {
        return checkThickness;
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

    public boolean getCheckMark() {
        return checkMark;
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

        strokeWeight(checkThickness);
        stroke(textColor, textColor.a * (alpha / 255.0f));
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
