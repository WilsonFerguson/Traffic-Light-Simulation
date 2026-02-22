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

    // Stroke Weight
    float startStrokeWeight = -1;
    float endStrokeWeight = -1;

    // Graph Percent Drawn
    float startGraphPercentDrawn = -1;
    float endGraphPercentDrawn = -1;

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

    // Alpha
    float startAlpha = -1;
    float endAlpha = -1;

    float duration = 1;
    float startTime = 0;

    float delayStartTime = 0; // How long to delay the animation before starting
    boolean shouldAnimate = true;

    UIElement element;
    Interactable interactable;

    Runnable onBegin;
    Runnable onUpdate;
    Runnable onComplete;

    LerpType lerpType = LerpType.SMOOTH;

    private boolean started = false;

    // Animation constants
    public static float colorLerpAmount = 0.2f;

    public Animator(PVector referencePos, PVector referenceSize, double seconds) {
        this.referencePos = referencePos;
        this.referenceSize = referenceSize;

        this.duration = (float) seconds * 1000;
        startTime = millis();

        animators.add(this);
    }

    public Animator(color referenceGenericColor, double seconds) {
        this.referenceGenericColor = referenceGenericColor;

        this.duration = (float) seconds * 1000;
        startTime = millis();

        animators.add(this);
    }

    public Animator(UIElement element, double seconds) {
        this(element.pos, element.size, seconds);

        this.element = element;
        if (element instanceof Interactable) {
            interactable = (Interactable) element;
        }
    }

    /**
     * Sets the lerp type for the animation. Note that {@code SUPER_SMOOTH} is a
     * more
     * exaggerated version of the ease-in-out {@code SMOOTH} lerp type.
     */
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
        if (element instanceof Panel)
            startCornerRadius = ((Panel) element).getCornerRadius();
        else if (element instanceof Interactable)
            startCornerRadius = ((Interactable) element).cornerRadius;
        else
            throw new RuntimeException(
                    "Attempted to animate corner radius on a non-panel or non-interactable element. The element must be an instance of Panel or Interactable, but it is an instance of "
                            + element.getClass().getName());

        this.endCornerRadius = (float) endCornerRadius;
        return this;
    }

    public Animator setStrokeWeight(double startStrokeWeight, double endStrokeWeight) {
        this.startStrokeWeight = (float) startStrokeWeight;
        this.endStrokeWeight = (float) endStrokeWeight;
        return this;
    }

    public Animator setStrokeWeight(double endStrokeWeight) {
        if (element instanceof Interactable)
            startStrokeWeight = (float) ((Interactable) element).strokeWeight;
        else
            throw new RuntimeException(
                    "Attempted to animate stroke weight on a non-interactable element. The element must be an instance of Interactable, but it is an instance of "
                            + element.getClass().getName());

        this.endStrokeWeight = (float) endStrokeWeight;
        return this;
    }

    public Animator setGraphPercentDrawn(double startGraphPercentDrawn, double endGraphPercentDrawn) {
        if (!(element instanceof Graph)) {
            throw new RuntimeException(
                    "Attempted to animate graph percent drawn on a non-graph element. The element must be an instance of Graph, but it is an instance of "
                            + element.getClass().getName());
        }
        this.startGraphPercentDrawn = (float) startGraphPercentDrawn;
        this.endGraphPercentDrawn = (float) endGraphPercentDrawn;
        return this;
    }

    public Animator setGraphPercentDrawn(double endGraphPercentDrawn) {
        if (!(element instanceof Graph)) {
            throw new RuntimeException(
                    "Attempted to animate graph percent drawn on a non-graph element. The element must be an instance of Graph, but it is an instance of "
                            + element.getClass().getName());
        }
        startGraphPercentDrawn = ((Graph) element).getPercentDrawn();
        this.endGraphPercentDrawn = (float) endGraphPercentDrawn;
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

    public Animator setAlpha(double startAlpha, double endAlpha) {
        this.startAlpha = (float) startAlpha;
        this.endAlpha = (float) endAlpha;
        return this;
    }

    public Animator setAlpha(double endAlpha) {
        if (element != null)
            startAlpha = element.getAlpha();
        else if (referenceGenericColor != null)
            startAlpha = referenceGenericColor.getAlpha();
        else
            throw new RuntimeException(
                    "The animator was created without a reference element or color, and so it could not automatically find a starting alpha value. Either use the other setAlpha method or provide a reference element or color.");

        this.endAlpha = (float) endAlpha;
        return this;
    }

    /**
     * Sets all possible parameters from a given element. This element can be of any
     * type, so long as it ultimately extends {@code UIElement}.
     * <br>
     * For example, if you pass a {@code Button}, it will copy all of the
     * {@code Button}'s properties as final values for the animation.
     * <br>
     * <br>
     * The initial values of the animation will be automatically set to the current
     * state of the animator's element (if provided). Otherwise they will be left as
     * null. Because of this, you will probably always call this function on an
     * {@code Animator} that has been assigned an element in its constructor.
     * <br>
     * <br>
     * More temporary states such as {@code isHovered} or {@code active} will not be
     * copied.
     */
    public Animator setFromElement(UIElement object) {
        // UIElement properties
        endPos = object.getPos().copy();
        startPos = element.pos.copy();

        endSize = object.getSize().copy();
        startSize = element.getSize().copy();

        endAlpha = object.getAlpha();
        if (element != null)
            startAlpha = element.getAlpha();
        else if (referenceGenericColor != null)
            startAlpha = referenceGenericColor.getAlpha();

        // Interactable properties
        if (object instanceof Interactable) {
            Interactable objectInteractable = (Interactable) object;

            endDefaultColor = objectInteractable.defaultColor.copy();
            endHoverColor = objectInteractable.hoverColor.copy();
            endActiveColor = objectInteractable.activeColor.copy();
            endStrokeColor = objectInteractable.strokeColor.copy();
            endTextColor = objectInteractable.textColor.copy();

            endTextSize = objectInteractable.textSize;
            endCornerRadius = objectInteractable.cornerRadius;
            endStrokeWeight = (float) objectInteractable.strokeWeight;

            if (interactable != null) {
                startDefaultColor = interactable.defaultColor.copy();
                startHoverColor = interactable.hoverColor.copy();
                startActiveColor = interactable.activeColor.copy();
                startStrokeColor = interactable.strokeColor.copy();
                startTextColor = interactable.textColor.copy();

                startTextSize = interactable.textSize;
                startCornerRadius = interactable.cornerRadius;
                startStrokeWeight = (float) interactable.strokeWeight;
            }
        }

        // Inidividual properties
        if (object instanceof Graph) {
            Graph objectGraph = (Graph) object;
            endGraphPercentDrawn = objectGraph.getPercentDrawn();

            if (element instanceof Graph) {
                startGraphPercentDrawn = ((Graph) element).getPercentDrawn();
            }
        }

        return this;
    }

    /**
     * Sets both the initial and final values of the animation from the given
     * objects.
     */
    public Animator setFromElement(UIElement start, UIElement end) {
        UIElement elementTemp = element;
        Interactable interactableTemp = interactable;

        element = start;
        interactable = start instanceof Interactable ? (Interactable) start : null;
        setFromElement(end);
        element = elementTemp;
        interactable = interactableTemp;

        return this;
    }

    /**
     * Sets whether the animation should animate or not. If you set this to
     * {@code false}, the animation will not play until you either set it to
     * {@code true} or call the {@link #start()} method (and then it will start
     * after the specified delay).
     *
     * By default, this is set to {@code true} and so the animation will start
     * immediately (or after the delay is up).
     *
     * If the animation is already running and you set this to {@code false}, it
     * will stop. Upon setting it to {@code true}, it will start from the beginning.
     */
    public Animator setShouldAnimate(boolean shouldAnimate) {
        this.shouldAnimate = shouldAnimate;
        if (shouldAnimate)
            startTime = millis() + delayStartTime;

        return this;
    }

    public boolean getShouldAnimate() {
        return shouldAnimate;
    }

    public Animator start() {
        this.shouldAnimate = true;
        startTime = millis() + delayStartTime;

        return this;
    }

    public Animator setDelay(double delay) {
        delayStartTime = (float) delay * 1000;
        startTime += delayStartTime;
        return this;
    }

    /**
     * @deprecated Use setDelay instead
     */
    @Deprecated
    public Animator delay(double delay) {
        return setDelay(delay);
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

    public Animator onUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    private void onUpdate() {
        if (onUpdate != null)
            onUpdate.run();
    }

    public float lerpFunction(double start, double stop, double amt) {
        if (amt == 1)
            return (float) stop;

        switch (lerpType) {
            case LINEAR:
                return lerp((float) start, (float) stop, (float) amt);
            case SMOOTH:
                return lerpSmooth(start, stop, amt);
            case SUPER_SMOOTH:
                return lerpSuperSmooth(start, stop, amt);
            case CONSTANT_ACCELERATION:
                return lerpConstantAcceleration(start, stop, amt);
            case CONSTANT_DECELERATION:
                return lerpConstantDeceleration(start, stop, amt);
            case OVERSHOOT:
                return lerpOvershoot(start, stop, amt);
            default:
                return lerp((float) start, (float) stop, (float) amt);
        }
    }

    public color lerpFunctionColor(color start, color stop, double amt) {
        if (amt == 1)
            return stop;

        switch (lerpType) {
            case LINEAR:
                return lerpColor(start, stop, (float) amt);
            case SMOOTH:
                return lerpColor(start, stop, lerpSmooth(0, 1, amt));
            case SUPER_SMOOTH:
                return lerpColor(start, stop, lerpSuperSmooth(0, 1, amt));
            case CONSTANT_ACCELERATION:
                return lerpColor(start, stop, lerpConstantAcceleration(0, 1, amt));
            case CONSTANT_DECELERATION:
                return lerpColor(start, stop, lerpConstantDeceleration(0, 1, amt));
            case OVERSHOOT:
                return lerpColor(start, stop, lerpOvershoot(0, 1, amt));
            default:
                return lerpColor(start, stop, (float) amt);
        }
    }

    private void animatePosSize(double amt) {
        // Position
        if (startPos != null && endPos != null) {
            // If we are animating a UI element, then use the setPos function
            if (element != null) {
                float x = lerpFunction(startPos.x, endPos.x, amt);
                float y = lerpFunction(startPos.y, endPos.y, amt);
                element.setPos(new PVector(x, y));
            } else {
                referencePos.x = lerpFunction(startPos.x, endPos.x, amt);
                referencePos.y = lerpFunction(startPos.y, endPos.y, amt);
            }

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
            // If we are animating a UI element, then use the setSize function
            if (element != null) {
                float w = lerpFunction(startSize.x, endSize.x, amt);
                float h = lerpFunction(startSize.y, endSize.y, amt);
                element.setSize(new PVector(w, h));
            } else {
                referenceSize.x = lerpFunction(startSize.x, endSize.x, amt);
                referenceSize.y = lerpFunction(startSize.y, endSize.y, amt);
            }

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
            interactable.setTextSize(lerpFunction(startTextSize, endTextSize, amt));
        }
    }

    public void animateCornerRadius(double amt) {
        if (startCornerRadius != -1 && interactable != null) {
            interactable.setCornerRadius(lerpFunction(startCornerRadius, endCornerRadius, amt));
        }
    }

    public void animateStrokeWeight(double amt) {
        if (startStrokeWeight != -1 && interactable != null) {
            interactable.setStrokeWeight(lerpFunction(startStrokeWeight, endStrokeWeight, amt));
        }
    }

    public void animateGraphPercentDrawn(double amt) {
        if (startGraphPercentDrawn != -1 && element instanceof Graph) {
            ((Graph) element).setPercentDrawn(lerpFunction(startGraphPercentDrawn, endGraphPercentDrawn, amt));
        }
    }

    public void animateAlpha(double amt) {
        if (startAlpha != -1 && endAlpha != -1) {
            float newAlpha = lerpFunction(startAlpha, endAlpha, amt);
            if (element != null) {
                element.setAlpha(newAlpha);
            } else if (referenceGenericColor != null) {
                referenceGenericColor.setAlpha(newAlpha);
            }
        }
    }

    public void animateColors(double amt) {
        // Colors
        if (interactable != null) {
            if (startDefaultColor != null)
                interactable.defaultColor.setColor(lerpFunctionColor(startDefaultColor, endDefaultColor, amt));
            if (startHoverColor != null)
                interactable.hoverColor.setColor(lerpFunctionColor(startHoverColor, endHoverColor, amt));
            if (startActiveColor != null)
                interactable.activeColor.setColor(lerpFunctionColor(startActiveColor, endActiveColor, amt));
            if (startStrokeColor != null)
                interactable.strokeColor.setColor(lerpFunctionColor(startStrokeColor, endStrokeColor, amt));
            if (startTextColor != null)
                interactable.textColor.setColor(lerpFunctionColor(startTextColor, endTextColor, amt));
        }

        // Generic color
        if (referenceGenericColor != null && referenceGenericStartColor != null
                && referenceGenericEndColor != null) {
            color newColor = lerpFunctionColor(referenceGenericStartColor, referenceGenericEndColor, amt);
            referenceGenericColor.setColor(newColor);
        }
    }

    public void animate() {
        // Delay
        if (!shouldAnimate)
            return;

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
        animateStrokeWeight(amt);
        animateGraphPercentDrawn(amt);
        animateAlpha(amt);
        animateColors(amt);

        onUpdate();

        if (amt == 1) {
            animators.remove(this);
            PComponent.delete(this);
            onEnd();
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
