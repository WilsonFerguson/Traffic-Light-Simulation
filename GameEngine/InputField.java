package GameEngine;

import library.core.*;

public class InputField extends Interactable {

    private boolean selected = false;
    private String defaultText;

    private boolean numbersOnly = false;
    private boolean autoResize = false;
    private boolean canShrink = false;

    private Runnable onEnter;
    private Runnable onInput;
    private Runnable onResize;

    // Defaults
    private static InputField defaultInputField = new InputField(0, 0, 300, 100, new color(200), new color(230),
            new color(180), new color(0), "", 32, new color(0));

    public InputField(PVector pos, PVector size, color defaultColor,
            color hoverColor, color activeColor, color strokeColor,
            String defaultText, double textSize, color textColor) {
        this.pos = pos;
        this.size = size;

        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.strokeColor = strokeColor;

        this.defaultText = defaultText;
        this.text = "";
        this.textSize = (float) textSize;
        this.textColor = textColor;

        currentColor = defaultColor;

        textAlignment = TextAlignment.LEFT;
    }

    public InputField(double x, double y, double w, double h, color defaultColor,
            color hoverColor, color activeColor, color strokeColor,
            String defaultText, double textSize, color textColor) {
        this(new PVector(x, y), new PVector(w, h), defaultColor, hoverColor,
                activeColor, strokeColor, defaultText, textSize, textColor);
    }

    public InputField(PVector pos, PVector size) {
        clone(defaultInputField);
        this.pos = pos;
        this.size = size;
    }

    public InputField(double x, double y, double w, double h) {
        this(new PVector(x, y), new PVector(w, h));
    }

    public InputField(PVector pos) {
        this(pos, defaultInputField.size.copy());
    }

    public InputField(double x, double y) {
        this(new PVector(x, y));
    }

    public static void setDefaults(InputField defaults) {
        defaultInputField = defaults;
    }

    public static void setDefaults(PVector defaultSize, color defaultColor,
            color hoverColor, color activeColor,
            color strokeColor, String defaultText,
            double textSize, color textColor) {
        defaultInputField = new InputField(PVector.zero(), defaultSize, defaultColor, hoverColor, activeColor,
                strokeColor, defaultText, textSize, textColor);
    }

    public static InputField getDefaults() {
        return defaultInputField;
    }

    public InputField copy() {
        InputField copy = new InputField(pos.copy(), size.copy(),
                defaultColor.copy(), hoverColor.copy(),
                activeColor.copy(), strokeColor.copy(),
                new String(defaultText), textSize, textColor.copy());
        copy.setCornerRadius(cornerRadius);
        copy.setStrokeWeight(strokeWeight);
        copy.setNumbersOnly(numbersOnly);
        copy.setAutoResize(autoResize);
        copy.setAllowShrinking(canShrink);
        copy.onEnter(onEnter);
        copy.onInput(onInput);
        copy.onResize(onResize);
        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setActive(active);
        copy.setInteractive(interactive);
        copy.setText(text);
        copy.setTextAlignment(textAlignment);
        copy.setAlpha(alpha);
        return copy;
    }

    public void clone(InputField inputField) {
        pos = inputField.pos.copy();
        size = inputField.size.copy();
        defaultColor = inputField.defaultColor.copy();
        hoverColor = inputField.hoverColor.copy();
        activeColor = inputField.activeColor.copy();
        strokeColor = inputField.strokeColor.copy();
        defaultText = inputField.defaultText;
        textSize = inputField.textSize;
        textColor = inputField.textColor.copy();
        cornerRadius = inputField.cornerRadius;
        numbersOnly = inputField.numbersOnly;
        onEnter = inputField.onEnter;
        onInput = inputField.onInput;
        onResize = inputField.onResize;
        onHover = inputField.onHover;
        onHoverExit = inputField.onHoverExit;
        active = inputField.active;
        interactive = inputField.interactive;
        text = inputField.text;
        textAlignment = inputField.textAlignment;

        currentColor = defaultColor;
    }

    public InputField setCornerRadius(double radius) {
        this.cornerRadius = (int) radius;
        return this;
    }

    public InputField setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public InputField setDefaultText(String defaultText) {
        this.defaultText = defaultText;
        return this;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public InputField setNumbersOnly(boolean numbersOnly) {
        this.numbersOnly = numbersOnly;
        return this;
    }

    public boolean getNumbersOnly() {
        return numbersOnly;
    }

    public InputField setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
        if (autoResize) {
            attemptAutoresize();
        }
        return this;
    }

    public boolean getAutoResize() {
        return autoResize;
    }

    /**
     * By default the input field will only grow (when autoresize is set to true).
     * But you can allow it to also shrink if the new text width is shorter than the
     * current size.
     */
    public InputField setAllowShrinking(boolean canShrink) {
        this.canShrink = canShrink;
        return this;
    }

    public boolean getAllowShrinking() {
        return canShrink;
    }

    public InputField onEnter(Runnable onEnter) {
        this.onEnter = onEnter;
        return this;
    }

    public void onEnter() {
        if (onEnter != null)
            onEnter.run();
    }

    /**
     * Triggered just after an input occurs (so the text is already updated).
     */
    public InputField onInput(Runnable onInput) {
        this.onInput = onInput;
        return this;
    }

    public void onInput() {
        if (onInput != null)
            onInput.run();
    }

    /**
     * Triggered when the input field is automatically resized (only triggers if
     * autoResize is true).
     */
    public InputField onResize(Runnable onResize) {
        this.onResize = onResize;
        return this;
    }

    public void onResize() {
        if (onResize != null)
            onResize.run();
    }

    public String getText() {
        return text;
    }

    public InputField setText(String text) {
        this.text = text;
        if (autoResize) {
            attemptAutoresize();
        }
        return this;
    }

    public InputField setTextSize(double textSize) {
        this.textSize = (float) textSize;
        if (autoResize) {
            attemptAutoresize();
        }
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public InputField setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    public void mousePressed() {
        if (!active || mouseButton != LEFT)
            return;

        boolean isHover = hover();
        if (isHover && interactive)
            selected = !selected;
        if (!isHover)
            selected = false;
    }

    public void keyTyped() {
        if (selected) {
            if (keyString.equals("Backspace")) {
                if (text.length() > 0) {
                    text = text.substring(0, text.length() - 1);
                }
                onInput();
            } else if (keyString.equals("Enter")) {
                onEnter();
                selected = false;
            } else if (keyString.equals("Escape")) {
                selected = false;
            } else if (key == ' ' && !numbersOnly) {
                text += " ";
            } else {
                if (numbersOnly) {
                    // numbers or decimal point
                    if ((key >= '0' && key <= '9') || key == '.' || key == ',') {
                        text += key;
                    }
                } else {
                    text += key;
                }
                onInput();
            }

            if (autoResize) {
                attemptAutoresize();
            }
        }
    }

    private void attemptAutoresize() {
        push();
        textSize(textSize);
        float textWidth = textWidth(text);

        if (!canShrink) {
            if (textWidth > size.x - 2 * 10) {
                size.x = textWidth + 2 * 10;
                onResize();
            }
        } else {
            float prevX = size.x;

            size.x = textWidth + 2 * 10;
            if (prevX != size.x)
                onResize();
        }
        pop();
    }

    public void draw() {
        if (!active)
            return;

        // Can't be selected if not interactive (so if we were selected but then were
        // set to not be interactive, this would then update selected)
        if (!interactive)
            selected = false;

        boolean previouslyHovered = isHovered;
        isHovered = hover();
        if (!previouslyHovered && isHovered)
            onHover();
        else if (previouslyHovered && !isHovered)
            onHoverExit();

        color targetColor = defaultColor;

        if (isHovered && !selected) {
            targetColor = hoverColor;
        } else if (selected) {
            targetColor = activeColor;
        }

        currentColor = lerpColor(currentColor, targetColor, Animator.colorLerpAmount);
        fill(currentColor, currentColor.a * (alpha / 255.0f));

        if (!interactive)
            fill(defaultColor, alpha);

        stroke(strokeColor, strokeColor.a * (alpha / 255.0f));
        strokeWeight(strokeWeight);
        rectMode(CENTER);
        rect(pos, size, cornerRadius);

        float textAlphaPercentage = text == "" ? 0.5f : 1.0f;
        fill(textColor, textAlphaPercentage * textColor.a * (alpha / 255.0f));
        textSize(textSize);
        textAlign(textAlignment);

        String shownText = text == "" ? defaultText : text;
        if (textAlignment.equals(TextAlignment.LEFT))
            text(shownText, pos.x - size.x / 2 + 10, pos.y);
        else if (textAlignment.equals(TextAlignment.CENTER))
            text(shownText, pos);
        else if (textAlignment.equals(TextAlignment.RIGHT))
            text(shownText, pos.x + size.x / 2 - 10, pos.y);
    }
}
