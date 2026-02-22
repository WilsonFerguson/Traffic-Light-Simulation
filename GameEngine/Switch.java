package GameEngine;

import library.core.*;

public class Switch extends Interactable {

    public boolean on = false;

    public boolean selected = false; // Is the user pressing the knob?

    private color currentBackgroundColor;
    public color unCheckedBackgroundColor;
    public color checkedBackgroundColor;

    private PVector knobPosition;

    private Runnable onToggle;

    // Defaults
    public static PVector DEFAULT_SIZE = new PVector(100, 50);
    public static color DEFAULT_COLOR = new color(255);
    public static color HOVER_COLOR = new color(200);
    public static color ACTIVE_COLOR = new color(150);
    public static color UNCHECKED_BACKGROUND_COLOR = new color(230);
    public static color CHECKED_BACKGROUND_COLOR = new color(0, 150, 0);
    public static color STROKE_COLOR = new color(0);

    /**
     * Note for size: a 2:1 ratio is recommended.
     */
    public Switch(PVector pos, PVector size, color defaultColor, color hoverColor, color activeColor,
            color unCheckedBackgroundColor, color checkedBackgroundColor, color strokeColor) {
        this.pos = pos;
        this.size = size;

        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.unCheckedBackgroundColor = unCheckedBackgroundColor;
        this.checkedBackgroundColor = checkedBackgroundColor;
        this.strokeColor = strokeColor;

        currentColor = defaultColor;
        currentBackgroundColor = unCheckedBackgroundColor;

        knobPosition = new PVector(pos.x + (on ? size.x / 4 : -size.x / 4), pos.y);
    }

    /**
     * Takes in just a width, sets height to width / 2.
     */
    public Switch(PVector pos, double w, color defaultColor, color hoverColor, color activeColor,
            color unCheckedBackgroundColor, color checkedBackgroundColor, color strokeColor) {
        this(pos, new PVector(w, w / 2), defaultColor, hoverColor, activeColor, unCheckedBackgroundColor,
                checkedBackgroundColor, strokeColor);
    }

    public Switch(double x, double y, double w, double h, color defaultColor, color hoverColor, color activeColor,
            color unCheckedBackgroundColor, color checkedBackgroundColor, color strokeColor) {
        this(new PVector(x, y), new PVector(w, h), defaultColor, hoverColor, activeColor, unCheckedBackgroundColor,
                checkedBackgroundColor, strokeColor);
    }

    public Switch(double x, double y, double w, color defaultColor, color hoverColor, color activeColor,
            color unCheckedBackgroundColor, color checkedBackgroundColor, color strokeColor) {
        this(new PVector(x, y), w, defaultColor, hoverColor, activeColor, unCheckedBackgroundColor,
                checkedBackgroundColor, strokeColor);
    }

    public Switch(PVector pos, PVector size) {
        this(pos, size, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(),
                UNCHECKED_BACKGROUND_COLOR.copy(),
                CHECKED_BACKGROUND_COLOR.copy(), STROKE_COLOR.copy());
    }

    public Switch(double x, double y, double w, double h) {
        this(new PVector(x, y), new PVector(w, h), DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(),
                UNCHECKED_BACKGROUND_COLOR.copy(), CHECKED_BACKGROUND_COLOR.copy(), STROKE_COLOR.copy());
    }

    public Switch(PVector pos, double w) {
        this(pos, w, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(), UNCHECKED_BACKGROUND_COLOR.copy(),
                CHECKED_BACKGROUND_COLOR.copy(), STROKE_COLOR.copy());
    }

    public Switch(double x, double y, double w) {
        this(new PVector(x, y), w, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(),
                UNCHECKED_BACKGROUND_COLOR.copy(),
                CHECKED_BACKGROUND_COLOR.copy(), STROKE_COLOR.copy());
    }

    public Switch(PVector pos) {
        this(pos, DEFAULT_SIZE.copy(), DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(),
                UNCHECKED_BACKGROUND_COLOR.copy(),
                CHECKED_BACKGROUND_COLOR.copy(), STROKE_COLOR.copy());
    }

    public Switch(double x, double y) {
        this(new PVector(x, y), DEFAULT_SIZE.copy(), DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(),
                UNCHECKED_BACKGROUND_COLOR.copy(), CHECKED_BACKGROUND_COLOR.copy(), STROKE_COLOR.copy());
    }

    public Switch copy() {
        Switch copy = new Switch(pos.copy(), size.copy(), defaultColor.copy(), hoverColor.copy(), activeColor.copy(),
                unCheckedBackgroundColor.copy(), checkedBackgroundColor.copy(), strokeColor.copy());
        copy.onToggle(onToggle);
        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setCornerRadius(cornerRadius);
        copy.setStrokeWeight(strokeWeight);
        copy.setInteractive(interactive);
        copy.setActive(active);
        copy.setTextAlignment(textAlignment);
        copy.setAlpha(alpha);
        copy.setOn(on);
        copy.currentBackgroundColor = currentBackgroundColor.copy();
        copy.knobPosition = knobPosition.copy();

        return copy;
    }

    public static void setDefaults(PVector defaultSize, color defaultColor, color hoverColor, color activeColor,
            color unCheckedBackgroundColor, color checkedBackgroundColor, color strokeColor) {
        DEFAULT_SIZE = defaultSize;
        DEFAULT_COLOR = defaultColor;
        HOVER_COLOR = hoverColor;
        ACTIVE_COLOR = activeColor;
        UNCHECKED_BACKGROUND_COLOR = unCheckedBackgroundColor;
        CHECKED_BACKGROUND_COLOR = checkedBackgroundColor;
        STROKE_COLOR = strokeColor;
    }

    /**
     * Returns true only if the knob is being hovered over.
     */
    public boolean hover() {
        return (distSq(mouseX, mouseY, knobPosition.x, knobPosition.y) < size.y * size.y / 4);
    }

    /*
     * Returns true if the knob or body is being hovered over.
     */
    public boolean hoverBody() {
        return (mouseX >= pos.x - size.x / 2 && mouseX <= pos.x + size.x / 2 &&
                mouseY >= pos.y - size.y / 2 && mouseY <= pos.y + size.y / 2);
    }

    public void mousePressed() {
        if (interactive && active && mouseButton == LEFT && hover()) {
            selected = true;
        }
    }

    public void mouseReleased() {
        if (selected && active) {
            selected = false;

            boolean previousOn = on;
            on = (mouseX > pos.x);
            if (previousOn != on) {
                onToggle();
            }
        }
    }

    public boolean isOn() {
        return on;
    }

    public Switch setOn(boolean on) {
        this.on = on;
        return this;
    }

    public Switch setPos(PVector pos) {
        this.pos = pos;
        snapKnobToTarget();
        return this;
    }

    public Switch setPos(double x, double y) {
        return setPos(new PVector(x, y));
    }

    public Switch onToggle(Runnable onToggle) {
        this.onToggle = onToggle;
        return this;
    }

    public void onToggle() {
        if (onToggle != null)
            onToggle.run();
    }

    public Switch snapKnobToTarget() {
        knobPosition.x = pos.x + (on ? size.x / 4 : -size.x / 4);
        knobPosition.y = pos.y;
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

        if (selected) {
            int targetX = (int) constrain(mouseX, pos.x - size.x / 4, pos.x + size.x / 4);
            knobPosition.x = lerp(knobPosition.x, targetX, 0.3);
        } else {
            // Lerp to the correct position
            knobPosition.x = lerp(knobPosition.x, pos.x + (on ? size.x / 4 : -size.x / 4), 0.3);
        }

        // Background
        if (!selected) {
            currentBackgroundColor = lerpColor(currentBackgroundColor,
                    on ? checkedBackgroundColor : unCheckedBackgroundColor, Animator.colorLerpAmount);
        } else {
            // Smoothly transition from unchecked to checked, % of knob position
            float percent = (knobPosition.x - pos.x + size.x / 4) / (size.x / 2);
            color targetColor = lerpColor(unCheckedBackgroundColor, checkedBackgroundColor, percent);
            currentBackgroundColor = lerpColor(currentBackgroundColor, targetColor, 0.5);
        }

        rectMode(CENTER);
        fill(currentBackgroundColor, currentBackgroundColor.a * (alpha / 255.0f));
        stroke(strokeColor, strokeColor.a * (alpha / 255.0f));
        strokeWeight(strokeWeight);
        rect(pos.x, pos.y, size.x, size.y, size.y);

        // Knob
        color targetColor = defaultColor;
        if (selected)
            targetColor = activeColor;
        else if (isHovered && interactive)
            if (mousePressed && mouseButton == LEFT)
                targetColor = activeColor;
            else
                targetColor = hoverColor;

        currentColor = lerpColor(currentColor, targetColor, Animator.colorLerpAmount);

        fill(currentColor, currentColor.a * (alpha / 255.0f));
        noStroke();
        ellipseMode(CENTER);
        circle(knobPosition.x, knobPosition.y, size.y * 0.95);
    }
}
