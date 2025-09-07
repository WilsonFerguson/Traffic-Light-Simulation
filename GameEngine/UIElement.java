package GameEngine;

import library.core.*;

public abstract class UIElement extends PComponent {

    public PVector pos;
    public PVector size;

    public boolean isHovered = false;

    Runnable onHover;
    Runnable onHoverExit;

    protected boolean active = true;

    public void draw() {
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

}