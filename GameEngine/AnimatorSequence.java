package GameEngine;

import library.core.*;
import java.util.*;

public class AnimatorSequence {
    ArrayList<Animator> animators;

    boolean running = false;
    private int startTime = 0;

    Runnable onStart;
    Runnable onEnd;

    public AnimatorSequence() {
        animators = new ArrayList<>();
    }

    public AnimatorSequence(Animator... animatorsToAdd) {
        animators = new ArrayList<>();
        for (Animator animator : animatorsToAdd) {
            addAnimator(animator);
        }
    }

    /**
     * NOTE: Due to how the Animator is coded, if you use methods such as
     * {@code Animator.setSize() or Animator.setPos()} and only provide the final
     * value (and not the initial), the Animator will assume an initial value of
     * whatever the element's value is upon creation of the Animator. To fix this,
     * specify both the initial and final values of the size/position/etc.
     */
    public AnimatorSequence addAnimator(Animator animator) {
        if (running) {
            throw new IllegalStateException("Cannot add animator while animator sequence is running.");
        }

        animator.setShouldAnimate(false);
        animators.add(animator);

        if (animators.size() > 1) {
            final int lastIndex = animators.size() - 1;
            final int lastLastIndex = lastIndex - 1;
            animators.get(lastLastIndex).onEnd(() -> animators.get(lastIndex).start());
        }

        return this;
    }

    public AnimatorSequence removeAnimator(Animator animator) {
        if (running) {
            throw new IllegalStateException("Cannot remove animator while animator sequence is running.");
        }

        animators.remove(animator);

        if (animators.size() > 1) {
            for (int i = 0; i < animators.size() - 1; i++) {
                final int index = i;
                animators.get(index).onEnd(() -> animators.get(index + 1).start());
            }
        }

        return this;
    }

    public AnimatorSequence removeAnimator(int index) {
        if (running) {
            throw new IllegalStateException("Cannot remove animator while animator sequence is running.");
        }

        if (index < 0 || index >= animators.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        Animator removedAnimator = animators.remove(index);

        if (animators.size() > 1) {
            for (int i = 0; i < animators.size() - 1; i++) {
                final int ind = i;
                animators.get(ind).onEnd(() -> animators.get(ind + 1).start());
            }
        }

        return this;
    }

    public AnimatorSequence onStart(Runnable onStart) {
        this.onStart = onStart;
        return this;
    }

    private void onStart() {
        if (onStart != null) {
            onStart.run();
        }
    }

    public AnimatorSequence onEnd(Runnable onEnd) {
        this.onEnd = onEnd;
        return this;
    }

    private void onEnd() {
        if (onEnd != null) {
            onEnd.run();
        }
    }

    public void start() {
        if (animators.size() == 0) {
            throw new IllegalStateException("Cannot start an empty animator sequence.");
        }

        running = true;
        startTime = (int) System.currentTimeMillis();
        onStart();

        animators.get(animators.size() - 1).onEnd(() -> end());
        animators.get(0).start();
    }

    public void end() {
        for (Animator animator : animators) {
            animator.setShouldAnimate(false);
        }

        running = false;
        onEnd();
    }

    public boolean isRunning() {
        return running;
    }

    public float duration() {
        float totalDuration = 0;
        for (Animator animator : animators) {
            totalDuration += animator.duration + animator.delayStartTime;
        }
        return totalDuration;
    }

    /**
     * Returns the progress of the animator sequence as a float between 0 and 1 (0
     * if it's not running).
     */
    public float getProgress() {
        if (!running) {
            return 0;
        }
        int elapsedTime = (int) System.currentTimeMillis() - startTime;
        float totalDuration = duration();
        return elapsedTime / totalDuration;
    }
}
