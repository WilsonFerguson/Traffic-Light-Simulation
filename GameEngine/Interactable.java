package GameEngine;

import library.core.*;

public abstract class Interactable extends UIElement {

    // Color
    public color currentColor;
    public color defaultColor;
    public color hoverColor;
    public color activeColor;
    public color strokeColor;

    // Text
    public String text;
    public int textSize;
    public TextAlignment textAlignment = TextAlignment.CENTER;
    public color textColor;

    // Rounded corners
    public int cornerRadius = 15;

    // Can be interacted with
    public boolean interactive = true;

    public Interactable setTextSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    public int getTextSize() {
        return textSize;
    }

    public Interactable setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public Interactable setTextColor(color textColor) {
        this.textColor = textColor;
        return this;
    }

    public color getTextColor() {
        return textColor;
    }

    public Interactable setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        return this;
    }

    public Interactable setInteractive(boolean interactive) {
        this.interactive = interactive;
        return this;
    }

}