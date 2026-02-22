package GameEngine;

import library.core.*;

public abstract class UIElement extends PComponent {

    public PVector pos;
    public PVector size;
    public float alpha = 255;

    public boolean isHovered = false;

    Runnable onHover;
    Runnable onHoverExit;

    protected boolean active = true;

    public void draw() {
    }

    public UIElement setAlpha(double alpha) {
        this.alpha = (float) alpha;
        return this;
    }

    public float getAlpha() {
        return alpha;
    }

    public boolean hover() {
        return mouseX > pos.x - size.x / 2 && mouseX < pos.x + size.x / 2 && mouseY > pos.y - size.y / 2
                && mouseY < pos.y + size.y / 2;
    }

    public UIElement onHover(Runnable onHover) {
        this.onHover = onHover;
        return this;
    }

    public void onHover() {
        if (onHover != null)
            onHover.run();
    }

    public UIElement onHoverExit(Runnable onHoverExit) {
        this.onHoverExit = onHoverExit;
        return this;
    }

    public void onHoverExit() {
        if (onHoverExit != null)
            onHoverExit.run();
    }

    public UIElement setActive(boolean active) {
        this.active = active;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public PVector getPos() {
        return pos;
    }

    public PVector getSize() {
        return size;
    }

    public UIElement setPos(PVector pos) {
        this.pos = pos;
        return this;
    }

    public UIElement setPos(double x, double y) {
        this.pos = new PVector(x, y);
        return this;
    }

    public UIElement setPos(float x, float y) {
        this.pos = new PVector(x, y);
        return this;
    }

    public UIElement setSize(PVector size) {
        this.size = size;
        return this;
    }

    public UIElement setSize(double width, double height) {
        this.size = new PVector(width, height);
        return this;
    }

    public abstract UIElement copy();

    public void delete() {
        PComponent.delete(this);
        active = false;
    }
}
