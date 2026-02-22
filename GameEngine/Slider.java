package GameEngine;

import library.core.*;

public class Slider extends Interactable {

    public float value = 0;
    public float min = 0;
    public float max = 1;
    public float step = 0.2f;

    private boolean selected = false;

    private PVector knobPosition;

    public color filledBackgroundColor;
    public color emptyBackgroundColor;

    Runnable onValueChange;

    // Defaults
    public static PVector DEFAULT_SIZE = new PVector(200, 50);
    public static color DEFAULT_COLOR = new color(255);
    public static color HOVER_COLOR = new color(200);
    public static color ACTIVE_COLOR = new color(150);
    public static color STROKE_COLOR = new color(0);
    public static color FILLED_BACKGROUND_COLOR = new color(0, 230, 0);
    public static color EMPTY_BACKGROUND_COLOR = new color(200);

    public Slider(PVector pos, PVector size, double min, double max, double step, color defaultColor, color hoverColor,
            color activeColor, color strokeColor, color emptyBackgroundColor, color filledBackgroundColor) {
        this.pos = pos;
        this.size = size;

        this.min = (float) min;
        this.max = (float) max;
        this.step = (float) step;

        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.strokeColor = strokeColor;

        this.filledBackgroundColor = filledBackgroundColor;
        this.emptyBackgroundColor = emptyBackgroundColor;

        currentColor = defaultColor;

        knobPosition = getTargetKnobPosition();
    }

    public Slider(double x, double y, double w, double h, float min, float max, float step, color defaultColor,
            color hoverColor, color activeColor, color strokeColor, color emptyBackgroundColor,
            color filledBackgroundColor) {
        this(new PVector(x, y), new PVector(w, h), min, max, step, defaultColor, hoverColor, activeColor, strokeColor,
                emptyBackgroundColor, filledBackgroundColor);
    }

    public Slider(PVector pos, PVector size, float min, float max, float step) {
        this(pos, size, min, max, step, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(),
                STROKE_COLOR.copy(), EMPTY_BACKGROUND_COLOR.copy(),
                FILLED_BACKGROUND_COLOR.copy());
    }

    public Slider(double x, double y, double w, double h, float min, float max, float step) {
        this(new PVector(x, y), new PVector(w, h), min, max, step, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
                ACTIVE_COLOR.copy(),
                STROKE_COLOR.copy(), EMPTY_BACKGROUND_COLOR.copy(), FILLED_BACKGROUND_COLOR.copy());
    }

    /**
     * Makes a slider with a range of 0 to 1, and a step of 0.01.
     */
    public Slider(PVector pos, PVector size) {
        this(pos, size, 0, 1, 0.2, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(), STROKE_COLOR.copy(),
                EMPTY_BACKGROUND_COLOR.copy(),
                FILLED_BACKGROUND_COLOR.copy());
    }

    public Slider(double x, double y, double w, double h) {
        this(new PVector(x, y), new PVector(w, h), 0, 1, 0.2, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
                ACTIVE_COLOR.copy(), STROKE_COLOR.copy(),
                EMPTY_BACKGROUND_COLOR.copy(), FILLED_BACKGROUND_COLOR.copy());
    }

    public Slider(PVector pos) {
        this(pos, DEFAULT_SIZE.copy(), 0, 1, 0.2, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(),
                STROKE_COLOR.copy(),
                EMPTY_BACKGROUND_COLOR.copy(),
                FILLED_BACKGROUND_COLOR.copy());
    }

    public Slider(double x, double y) {
        this(new PVector(x, y), DEFAULT_SIZE.copy(), 0, 1, 0.2, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
                ACTIVE_COLOR.copy(), STROKE_COLOR.copy(),
                EMPTY_BACKGROUND_COLOR.copy(), FILLED_BACKGROUND_COLOR.copy());
    }

    public Slider copy() {
        Slider copy = new Slider(pos.copy(), size.copy(), min, max, step, defaultColor.copy(), hoverColor.copy(),
                activeColor.copy(), strokeColor.copy(), emptyBackgroundColor.copy(), filledBackgroundColor.copy());
        copy.onValueChange(onValueChange);
        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setCornerRadius(cornerRadius);
        copy.setStrokeWeight(strokeWeight);
        copy.setInteractive(interactive);
        copy.setActive(active);
        copy.setTextAlignment(textAlignment);
        copy.setAlpha(alpha);
        copy.setValue(value);
        copy.knobPosition = knobPosition.copy();

        return copy;
    }

    public static void setDefaults(PVector defaultSize, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, color emptyBackgroundColor, color filledBackgroundColor) {
        DEFAULT_SIZE = defaultSize;
        DEFAULT_COLOR = defaultColor;
        HOVER_COLOR = hoverColor;
        ACTIVE_COLOR = activeColor;
        STROKE_COLOR = strokeColor;
        FILLED_BACKGROUND_COLOR = filledBackgroundColor;
        EMPTY_BACKGROUND_COLOR = emptyBackgroundColor;
    }

    private PVector getTargetKnobPosition() {
        float x = map(value, min, max, pos.x - size.x / 2 + size.y / 2, pos.x + size.x / 2 - size.y / 2);
        return new PVector(x, pos.y);
    }

    public void snapKnobToTarget() {
        knobPosition = getTargetKnobPosition();
    }

    public Slider onValueChange(Runnable onValueChange) {
        this.onValueChange = onValueChange;
        return this;
    }

    public void onValueChange() {
        if (onValueChange != null) {
            onValueChange.run();
        }
    }

    public Slider setValue(double value) {
        this.value = (float) value;
        knobPosition = getTargetKnobPosition();
        onValueChange();
        return this;
    }

    public Slider setMin(double min) {
        this.min = (float) min;
        return this;
    }

    public Slider setMax(double max) {
        this.max = (float) max;
        return this;
    }

    public Slider setStep(double step) {
        this.step = (float) step;
        return this;
    }

    public Slider setPos(PVector pos) {
        this.pos = pos;
        knobPosition = getTargetKnobPosition();
        return this;
    }

    public Slider setPos(double x, double y) {
        return setPos(new PVector(x, y));
    }

    public float getValue() {
        return value;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getStep() {
        return step;
    }

    /**
     * Returns true only if the knob is being hovered over.
     */
    public boolean hover() {
        float distSq = distSq(knobPosition, mouse);
        return distSq < size.y * size.y / 4;
    }

    /**
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
        selected = false;
    }

    private void calculateValue() {
        float previousValue = value;

        float x = selected ? constrain(mouseX, pos.x - size.x / 2 + size.y / 2, pos.x + size.x / 2 - size.y / 2)
                : knobPosition.x;
        value = map(x, pos.x - size.x / 2 + size.y / 2, pos.x + size.x / 2 - size.y / 2, min, max);
        value = round(value / step) * step;
        value = constrain(value, min, max);

        if (value != previousValue) {
            onValueChange();
        }
    }

    public void draw() {
        if (!active)
            return;

        calculateValue();

        boolean previouslyHovered = isHovered;
        isHovered = hover();
        if (!previouslyHovered && isHovered)
            onHover();
        else if (previouslyHovered && !isHovered)
            onHoverExit();

        // Move knob
        if (selected) {
            float targetX = map(value, min, max, pos.x - size.x / 2 + size.y / 2, pos.x + size.x / 2 - size.y / 2);
            knobPosition.x = lerp(knobPosition.x, targetX, 0.3);
        }

        // Background
        rectMode(CENTER);
        fill(emptyBackgroundColor, emptyBackgroundColor.a * (alpha / 255.0f));
        stroke(strokeColor, strokeColor.a * (alpha / 255.0f));
        strokeWeight(strokeWeight);
        rect(pos.x, pos.y, size.x, size.y, size.y);

        // Filled background
        rectMode(CORNER);
        fill(filledBackgroundColor, filledBackgroundColor.a * (alpha / 255.0f));
        noStroke();
        rect(pos.x - size.x / 2, pos.y - size.y / 2, knobPosition.x - pos.x + size.x / 2 + size.y / 2, size.y, size.y);

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
        circle(knobPosition.x, knobPosition.y, size.y);
    }
}
