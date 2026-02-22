package GameEngine;

import library.core.*;
import java.util.*;

public class Panel extends UIElement implements EventIgnorer {

    public color backgroundColor;
    public color strokeColor = new color(0, 0);

    private int cornerRadius = 50;
    private double strokeWeight = 2;
    private float elementHeightCounter = -1;

    ArrayList<UIElement> elements = new ArrayList<>();
    private HashSet<UIElement> drawInSecondPass = new HashSet<>();

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

    public Panel copy() {
        Panel copy = new Panel(pos.copy(), size.copy(), backgroundColor.copy(), strokeColor.copy());
        copy.setCornerRadius(cornerRadius);
        copy.setStrokeWeight(strokeWeight);
        copy.elementHeightCounter = elementHeightCounter;

        copy.elements = new ArrayList<>(elements.size());
        for (UIElement element : elements) {
            copy.elements.add(element.copy());
        }
        copy.drawInSecondPass = new HashSet<>(drawInSecondPass.size());
        for (UIElement element : drawInSecondPass) {
            copy.drawInSecondPass.add(element);
        }

        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setActive(active);
        copy.setAlpha(alpha);

        return copy;
    }

    public static void setDefaults(PVector defaultSize, color backgroundColor, color strokeColor) {
        DEFAULT_SIZE = defaultSize;
        BACKGROUND_COLOR = backgroundColor;
        STROKE_COLOR = strokeColor;
    }

    public Panel addElement(UIElement element) {
        element.setPos(PVector.add(element.pos, pos));
        elements.add(element);

        return this;
    }

    public Panel addElements(UIElement... elements) {
        for (UIElement element : elements) {
            addElement(element);
        }

        return this;
    }

    /**
     * Beginning at the top of the panel, it adds the element, then moves the
     * counter down that element's height so that the next element is added below
     * it.
     */
    public Panel addElementFromTop(UIElement element) {
        return addElementFromTop(element, true);
    }

    /**
     * Adds an element from the top. If {@code moveCounter == false}, then it will
     * not move the counter down. Useful if you want multiple elements in the same
     * row.
     */
    public Panel addElementFromTop(UIElement element, boolean moveCounter) {
        if (elementHeightCounter == -1)
            elementHeightCounter = element.size.y / 2;

        element.setPos(PVector.add(element.pos, new PVector(0, -size.y / 2 + elementHeightCounter)));
        addElement(element);

        if (moveCounter)
            elementHeightCounter += element.size.y;

        return this;
    }

    /**
     * Adds an element from the top and then also adds padding below it (increases
     * the counter by the element's height and padding).
     */
    public Panel addElementFromTop(UIElement element, double paddingBelow) {
        addElementFromTop(element);
        elementHeightCounter += paddingBelow;

        return this;
    }

    public Panel drawInSecondPass(UIElement element) {
        drawInSecondPass.add(element);
        return this;
    }

    public Panel removeFromSecondPass(UIElement element) {
        drawInSecondPass.remove(element);
        return this;
    }

    public Panel incrementElementHeight(double height) {
        elementHeightCounter += height;
        return this;
    }

    public Panel removeElement(UIElement element) {
        elements.remove(element);
        if (drawInSecondPass.contains(element))
            drawInSecondPass.remove(element);

        return this;
    }

    public Panel removeElements(UIElement... elements) {
        for (UIElement element : elements) {
            this.elements.remove(element);
            if (drawInSecondPass.contains(element))
                drawInSecondPass.remove(element);
        }

        return this;
    }

    public Panel clearElements() {
        elements.clear();
        drawInSecondPass.clear();

        return this;
    }

    public ArrayList<UIElement> getElements() {
        return elements;
    }

    public UIElement getElement(int index) {
        return elements.get(index);
    }

    public HashSet<UIElement> getElementsDrawnInSecondPass() {
        return drawInSecondPass;
    }

    public Panel setBackgroundColor(color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public color getBackgroundColor() {
        return backgroundColor;
    }

    public Panel setStrokeColor(color strokeColor) {
        this.strokeColor = strokeColor;
        return this;
    }

    public color getStrokeColor() {
        return strokeColor;
    }

    public Panel setCornerRadius(double radius) {
        this.cornerRadius = (int) radius;
        return this;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public Panel setStrokeWeight(double weight) {
        this.strokeWeight = weight;
        return this;
    }

    public double getStrokeWeight() {
        return strokeWeight;
    }

    public Panel setPos(PVector pos) {
        PVector diff = PVector.sub(pos, this.pos.copy());
        this.pos = pos;
        for (UIElement element : elements) {
            element.setPos(PVector.add(element.pos, diff));
        }

        return this;
    }

    public Panel setPos(double x, double y) {
        return setPos(new PVector(x, y));
    }

    public Panel setPos(float x, float y) {
        return setPos(new PVector(x, y));
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
            stroke(strokeColor, strokeColor.a * (alpha / 255.0f));

        strokeWeight(strokeWeight);
        fill(backgroundColor, backgroundColor.a * (alpha / 255.0f));
        rectMode(CENTER);
        rect(pos.x, pos.y, size.x, size.y, cornerRadius);

        for (UIElement element : elements) {
            if (!drawInSecondPass.contains(element)) {
                float prevAlpha = element.getAlpha();
                element.setAlpha(prevAlpha * (alpha / 255.0));
                element.draw();
                element.setAlpha(prevAlpha);
            }
        }
        for (UIElement element : drawInSecondPass) {
            float prevAlpha = element.getAlpha();
            element.setAlpha(prevAlpha * (alpha / 255.0));
            element.draw();
            element.setAlpha(prevAlpha);
        }
    }

    public void delete() {
        super.delete();

        for (UIElement element : elements) {
            element.delete();
        }
        elements.clear();
        drawInSecondPass.clear();
    }
}
