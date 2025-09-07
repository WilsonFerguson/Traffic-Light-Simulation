package GameEngine;

import library.core.*;
import java.util.*;

public class Panel extends UIElement implements EventIgnorer {

    public color backgroundColor;
    public color strokeColor = new color(0, 0);

    private int cornerRadius = 50;

    ArrayList<UIElement> elements = new ArrayList<>();

    // Defaults
    public static PVector DEFAULT_SIZE = new PVector(400, 400);
    public static color BACKGROUND_COLOR = new color(150);
    public static color STROKE_COLOR = new color(0);

    public Panel(PVector pos, PVector size, color backgroundColor, color strokeColor) {
        this.pos = pos;
        this.size = size;
        this.backgroundColor = backgroundColor;
        this.strokeColor = strokeColor;
    }

    public Panel(PVector pos, PVector size, color backgroundColor) {
        this.pos = pos;
        this.size = size;
        this.backgroundColor = backgroundColor;
    }

    public Panel(double x, double y, double w, double h, color backgroundColor, color strokeColor) {
        this(new PVector(x, y), new PVector(w, h), backgroundColor, strokeColor);
    }

    public Panel(double x, double y, double w, double h, color backgroundColor) {
        this(new PVector(x, y), new PVector(w, h), backgroundColor);
    }

    public Panel(PVector pos, PVector size) {
        this(pos, size, BACKGROUND_COLOR.copy(), STROKE_COLOR.copy());
    }

    public Panel(double x, double y, double w, double h) {
        this(new PVector(x, y), new PVector(w, h), BACKGROUND_COLOR.copy(), STROKE_COLOR.copy());
    }

    public Panel(PVector pos) {
        this(pos, DEFAULT_SIZE.copy(), BACKGROUND_COLOR.copy(), STROKE_COLOR.copy());
    }

    public Panel(double x, double y) {
        this(new PVector(x, y), DEFAULT_SIZE.copy(), BACKGROUND_COLOR.copy(), STROKE_COLOR.copy());
    }

    public static void setDefaults(PVector defaultSize, color backgroundColor, color strokeColor) {
        DEFAULT_SIZE = defaultSize;
        BACKGROUND_COLOR = backgroundColor;
        STROKE_COLOR = strokeColor;
    }

    public void addElement(UIElement element) {
        element.pos = element.pos.add(pos);
        elements.add(element);
    }

    public void addElements(UIElement... elements) {
        for (UIElement element : elements) {
            addElement(element);
        }
    }

    public void removeElement(UIElement element) {
        elements.remove(element);
    }

    public void removeElements(UIElement... elements) {
        for (UIElement element : elements) {
            this.elements.remove(element);
        }
    }

    public void clearElements() {
        elements.clear();
    }

    public ArrayList<UIElement> getElements() {
        return elements;
    }

    public UIElement getElement(int index) {
        return elements.get(index);
    }

    public Panel setCornerRadius(double radius) {
        this.cornerRadius = (int) radius;
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

        if (strokeColor.equals(new color(0, 0)))
            noStroke();
        else
            stroke(strokeColor);

        strokeWeight(2);
        fill(backgroundColor);
        rectMode(CENTER);
        rect(pos.x, pos.y, size.x, size.y, cornerRadius);

        for (UIElement element : elements) {
            element.draw();
        }
    }

}
