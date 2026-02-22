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
    public float textSize;
    public TextAlignment textAlignment = TextAlignment.CENTER;
    public color textColor;

    // Border
    public int cornerRadius = 15;
    public double strokeWeight = 2;

    // Can be interacted with
    public boolean interactive = true;

    public Interactable setText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

    public Interactable setTextSize(double textSize) {
        this.textSize = (float) textSize;
        return this;
    }

    public float getTextSize() {
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

    public Interactable setDefaultColor(color defaultColor) {
        this.defaultColor = defaultColor;
        return this;
    }

    public color getDefaultColor() {
        return defaultColor;
    }

    public Interactable setHoverColor(color hoverColor) {
        this.hoverColor = hoverColor;
        return this;
    }

    public color getHoverColor() {
        return hoverColor;
    }

    public Interactable setActiveColor(color activeColor) {
        this.activeColor = activeColor;
        return this;
    }

    public color getActiveColor() {
        return activeColor;
    }

    public Interactable setStrokeColor(color strokeColor) {
        this.strokeColor = strokeColor;
        return this;
    }

    public color getStrokeColor() {
        return strokeColor;
    }

    public Interactable setCornerRadius(double cornerRadius) {
        this.cornerRadius = (int) cornerRadius;
        return this;
    }

    public Interactable setStrokeWeight(double strokeWeight) {
        this.strokeWeight = strokeWeight;
        return this;
    }

    public Interactable setInteractive(boolean interactive) {
        this.interactive = interactive;
        return this;
    }

    public boolean isInteractive() {
        return interactive;
    }

    public abstract Interactable copy();

    public void delete() {
        super.delete();
        interactive = false;
    }

}
