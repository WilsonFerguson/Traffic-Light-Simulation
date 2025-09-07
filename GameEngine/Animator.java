package GameEngine;

import library.core.*;
import java.util.*;

public class Animator extends PComponent implements EventIgnorer {

    public static ArrayList<Animator> animators = new ArrayList<Animator>();

    // Reference position and size
    PVector referencePos;
    PVector referenceSize;

    // Reference colors
    color referenceGenericColor;

    // Pos
    PVector startPos;
    PVector endPos;

    // Size
    PVector startSize;
    PVector endSize;

    // Text Size
    float startTextSize = -1;
    float endTextSize = -1;

    // Corner Radius
    float startCornerRadius = -1;
    float endCornerRadius = -1;

    // Color
    color referenceGenericStartColor;
    color referenceGenericEndColor;
    color startDefaultColor;
    color endDefaultColor;
    color startHoverColor;
    color endHoverColor;
    color startActiveColor;
    color endActiveColor;
    color startStrokeColor;
    color endStrokeColor;
    color startTextColor;
    color endTextColor;

    float duration = 1;
    float startTime = 0;

    float delayStartTime = 0; // How long to delay the animation before starting

    UIElement element;
    Interactable interactable;

    Runnable onComplete;
    Runnable onBegin;

    LerpType lerpType = LerpType.SMOOTH;

    private boolean started = false;

    // Animation constants
    public static float colorLerpAmount = 0.2f;

    public Animator(PVector referencePos, PVector referenceSize, double duration) {
        this.referencePos = referencePos;
        this.referenceSize = referenceSize;

        this.duration = (float) duration * 1000;
        startTime = millis();

        animators.add(this);
    }

    public Animator(color referenceGenericColor, double duration) {
        this.referenceGenericColor = referenceGenericColor;

        this.duration = (float) duration * 1000;
        startTime = millis();

        animators.add(this);
    }

    public Animator(UIElement element, double duration) {
        this(element.pos, element.size, duration);

        this.element = element;
        if (element instanceof Interactable) {
            interactable = (Interactable) element;
        }
    }

    public Animator setLerpType(LerpType lerpType) {
        this.lerpType = lerpType;
        return this;
    }

    public Animator setPos(PVector startPos, PVector endPos) {
        this.startPos = startPos.copy();
        this.endPos = endPos.copy();
        return this;
    }

    public Animator setPos(double startX, double startY, double endX, double endY) {
        setPos(new PVector(startX, startY), new PVector(endX, endY));
        return this;
    }

    public Animator setPos(PVector endPos) {
        startPos = referencePos.copy();
        this.endPos = endPos.copy();
        return this;
    }

    public Animator setPos(double endX, double endY) {
        setPos(new PVector(endX, endY));
        return this;
    }

    public Animator setX(double startX, double endX) {
        float y = 0;
        if (element != null) {
            y = element.pos.y;
        }
        setPos(startX, y, endX, y);
        return this;
    }

    public Animator setX(double endX) {
        float startX = 0;
        if (element != null) {
            startX = element.pos.copy().x;
        }
        setX(startX, endX);
        return this;
    }

    public Animator setY(double startY, double endY) {
        float x = 0;
        if (element != null) {
            x = element.pos.copy().x;
        }
        setPos(x, startY, x, endY);
        return this;
    }

    public Animator setY(double endY) {
        float startY = 0;
        if (element != null) {
            startY = element.pos.y;
        }
        setY(startY, endY);
        return this;
    }

    public Animator setSize(PVector startSize, PVector endSize) {
        this.startSize = startSize.copy();
        this.endSize = endSize.copy();
        return this;
    }

    public Animator setSize(double startX, double startY, double endX, double endY) {
        setSize(new PVector(startX, startY), new PVector(endX, endY));
        return this;
    }

    public Animator setSize(PVector endSize) {
        startSize = referenceSize.copy();
        this.endSize = endSize.copy();
        return this;
    }

    public Animator setSize(double endX, double endY) {
        setSize(new PVector(endX, endY));
        return this;
    }

    public Animator setSize(double wh) {
        setSize(wh, wh);
        return this;
    }

    public Animator setWidth(double startWidth, double endWidth) {
        float h = 0;
        if (element != null) {
            h = element.size.y;
        }
        setSize(startWidth, h, endWidth, h);
        return this;
    }

    public Animator setWidth(double endWidth) {
        float startWidth = 0;
        if (element != null) {
            startWidth = element.size.x;
        }
        setWidth(startWidth, endWidth);
        return this;
    }

    public Animator setHeight(double startHeight, double endHeight) {
        float w = 0;
        if (element != null) {
            w = element.size.x;
        }
        setSize(w, startHeight, w, endHeight);
        return this;
    }

    public Animator setHeight(double endHeight) {
        float startHeight = 0;
        if (element != null) {
            startHeight = element.size.y;
        }
        setHeight(startHeight, endHeight);
        return this;
    }

    public Animator setTextSize(double startTextSize, double endTextSize) {
        this.startTextSize = (float) startTextSize;
        this.endTextSize = (float) endTextSize;
        return this;
    }

    public Animator setTextSize(double endTextSize) {
        startTextSize = ((Interactable) element).textSize;
        this.endTextSize = (float) endTextSize;
        return this;
    }

    public Animator setCornerRadius(double startCornerRadius, double endCornerRadius) {
        this.startCornerRadius = (float) startCornerRadius;
        this.endCornerRadius = (float) endCornerRadius;
        return this;
    }

    public Animator setCornerRadius(double endCornerRadius) {
        startCornerRadius = ((Interactable) element).cornerRadius;
        this.endCornerRadius = (float) endCornerRadius;
        return this;
    }

    public Animator setDefaultColor(color startColor, color endColor) {
        startDefaultColor = startColor;
        endDefaultColor = endColor;
        return this;
    }

    public Animator setDefaultColor(color endColor) {
        startDefaultColor = interactable.defaultColor.copy();
        endDefaultColor = endColor;
        return this;
    }

    public Animator setHoverColor(color startColor, color endColor) {
        startHoverColor = startColor;
        endHoverColor = endColor;
        return this;
    }

    public Animator setHoverColor(color endColor) {
        startHoverColor = interactable.hoverColor.copy();
        endHoverColor = endColor;
        return this;
    }

    public Animator setActiveColor(color startColor, color endColor) {
        startActiveColor = startColor;
        endActiveColor = endColor;
        return this;
    }

    public Animator setActiveColor(color endColor) {
        startActiveColor = interactable.activeColor.copy();
        endActiveColor = endColor;
        return this;
    }

    public Animator setStrokeColor(color startColor, color endColor) {
        startStrokeColor = startColor;
        endStrokeColor = endColor;
        return this;
    }

    public Animator setStrokeColor(color endColor) {
        startStrokeColor = interactable.strokeColor.copy();
        endStrokeColor = endColor;
        return this;
    }

    public Animator setTextColor(color startColor, color endColor) {
        startTextColor = startColor;
        endTextColor = endColor;
        return this;
    }

    public Animator setTextColor(color endColor) {
        startTextColor = interactable.textColor.copy();
        endTextColor = endColor;
        return this;
    }

    /**
     * Set all colors at once (including start and end colors, and text)
     */
    public Animator setColors(color startDefaultColor, color endDefaultColor, color startHoverColor,
            color endHoverColor, color startActiveColor, color endActiveColor, color startStrokeColor,
            color endStrokeColor, color startTextColor, color endTextColor) {
        this.startDefaultColor = startDefaultColor;
        this.endDefaultColor = endDefaultColor;
        this.startHoverColor = startHoverColor;
        this.endHoverColor = endHoverColor;
        this.startActiveColor = startActiveColor;
        this.endActiveColor = endActiveColor;
        this.startStrokeColor = startStrokeColor;
        this.endStrokeColor = endStrokeColor;
        this.startTextColor = startTextColor;
        this.endTextColor = endTextColor;
        return this;
    }

    /**
     * Set all colors at once (only end colors, and text)
     */
    public Animator setColors(color endDefaultColor, color endHoverColor, color endActiveColor, color endStrokeColor,
            color endTextColor) {
        startDefaultColor = interactable.defaultColor.copy();
        this.endDefaultColor = endDefaultColor;
        startHoverColor = interactable.hoverColor.copy();
        this.endHoverColor = endHoverColor;
        startActiveColor = interactable.activeColor.copy();
        this.endActiveColor = endActiveColor;
        startStrokeColor = interactable.strokeColor.copy();
        this.endStrokeColor = endStrokeColor;
        startTextColor = interactable.textColor.copy();
        this.endTextColor = endTextColor;
        return this;
    }

    /**
     * Set all colors at once (including start and end colors, no text)
     */
    public Animator setColors(color startDefaultColor, color endDefaultColor, color startHoverColor,
            color endHoverColor, color startActiveColor, color endActiveColor, color startStrokeColor,
            color endStrokeColor) {
        this.startDefaultColor = startDefaultColor;
        this.endDefaultColor = endDefaultColor;
        this.startHoverColor = startHoverColor;
        this.endHoverColor = endHoverColor;
        this.startActiveColor = startActiveColor;
        this.endActiveColor = endActiveColor;
        this.startStrokeColor = startStrokeColor;
        this.endStrokeColor = endStrokeColor;
        return this;
    }

    /**
     * Set all colors at once (only end colors, no text)
     */
    public Animator setColors(color endDefaultColor, color endHoverColor, color endActiveColor, color endStrokeColor) {
        startDefaultColor = interactable.defaultColor.copy();
        this.endDefaultColor = endDefaultColor;
        startHoverColor = interactable.hoverColor.copy();
        this.endHoverColor = endHoverColor;
        startActiveColor = interactable.activeColor.copy();
        this.endActiveColor = endActiveColor;
        startStrokeColor = interactable.strokeColor.copy();
        this.endStrokeColor = endStrokeColor;
        return this;
    }

    public Animator setGenericColor(color startColor, color endColor) {
        referenceGenericStartColor = startColor;
        referenceGenericEndColor = endColor;
        return this;
    }

    public Animator setGenericColor(color endColor) {
        referenceGenericStartColor = referenceGenericColor.copy();
        referenceGenericEndColor = endColor;
        return this;
    }

    public Animator delay(double delay) {
        delayStartTime = (float) delay * 1000;
        startTime += delayStartTime;
        return this;
    }

    /**
     * Sets the onEnd function given a lambda expression
     */
    public Animator onEnd(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    private void onEnd() {
        if (onComplete != null)
            onComplete.run();
    }

    public boolean hasStarted() {
        return started;
    }

    /**
     * Sets the onStart function given a lambda expression
     */
    public Animator onStart(Runnable onBegin) {
        this.onBegin = onBegin;
        return this;
    }

    private void onStart() {
        if (onBegin != null)
            onBegin.run();
    }

    public float lerpFunction(double start, double stop, double amt) {
        if (lerpType == LerpType.LINEAR)
            return lerp(start, stop, amt);
        else if (lerpType == LerpType.SMOOTH)
            return lerpSmooth(start, stop, amt);
        else if (lerpType == LerpType.OVERSHOOT)
            return lerpOvershoot(start, stop, amt);
        else
            return lerp(start, stop, amt);
    }

    private void animatePosSize(double amt) {
        // Position
        if (startPos != null && endPos != null) {
            referencePos.x = lerpFunction(startPos.x, endPos.x, amt);
            referencePos.y = lerpFunction(startPos.y, endPos.y, amt);

            // TODO Refactor this code:
            if (element instanceof Switch) {
                ((Switch) element).snapKnobToTarget(); // Have it immediately go to target position instead of lerping
            }
            if (element instanceof Slider) {
                ((Slider) element).snapKnobToTarget(); // Have it immediately go to target position instead of lerping
            }
        }
        // Size
        if (startSize != null && endSize != null) {
            referenceSize.x = lerpFunction(startSize.x, endSize.x, amt);
            referenceSize.y = lerpFunction(startSize.y, endSize.y, amt);

            // TODO Refactor this code:
            if (element instanceof Switch) {
                ((Switch) element).snapKnobToTarget(); // Have it immediately go to target position instead of lerping
            }
            if (element instanceof Slider) {
                ((Slider) element).snapKnobToTarget(); // Have it immediately go to target position instead of lerping
            }
        }

    }

    public void animateTextSize(double amt) {
        if (startTextSize != -1 && interactable != null) {
            interactable.setTextSize((int) lerpFunction(startTextSize, endTextSize, amt));
        }
    }

    public void animateCornerRadius(double amt) {
        if (startCornerRadius != -1 && interactable != null) {
            interactable.setCornerRadius((int) lerpFunction(startCornerRadius, endCornerRadius, amt));
        }
    }

    public void animateColors(double amt) {
        // Colors
        if (interactable != null) {
            if (startDefaultColor != null)
                interactable.defaultColor.setColor(lerpColor(startDefaultColor, endDefaultColor, amt));
            if (startHoverColor != null)
                interactable.hoverColor.setColor(lerpColor(startHoverColor, endHoverColor, amt));
            if (startActiveColor != null)
                interactable.activeColor.setColor(lerpColor(startActiveColor, endActiveColor, amt));
            if (startStrokeColor != null)
                interactable.strokeColor.setColor(lerpColor(startStrokeColor, endStrokeColor, amt));
            if (startTextColor != null)
                interactable.textColor.setColor(lerpColor(startTextColor, endTextColor, amt));
        }

        // Generic color
        if (referenceGenericColor != null) {
            color newColor = lerpColor(referenceGenericStartColor, referenceGenericEndColor, amt);
            referenceGenericColor.setColor(newColor);
        }
    }

    public void animate() {
        // Delay
        if (millis() - (startTime - delayStartTime) < delayStartTime)
            return;

        if (!started) {
            onStart();
            started = true;
        }

        double amt = (millis() - startTime) / duration;
        amt = constrain(amt, 0, 1);

        animatePosSize(amt);
        animateTextSize(amt);
        animateCornerRadius(amt);
        animateColors(amt);

        if (amt == 1) {
            onEnd();
            animators.remove(this);
        }
    }

    // Animation constants
    public static void setColorLerpAmount(double amount) {
        colorLerpAmount = (float) amount;
    }

    public static void Run() {
        for (int i = animators.size() - 1; i >= 0; i--) {
            animators.get(i).animate();
        }
    }

}