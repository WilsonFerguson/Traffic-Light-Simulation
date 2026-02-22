package GameEngine;

import java.util.*;
import library.core.*;

public class Dropdown extends Interactable {

    public ArrayList<Button> options = new ArrayList<>();
    public Button selectedOption;

    String defaultText;

    color separatorColor; // color that separates the options

    public float animationDuration = 0.25f; // seconds
    private float animationDelay = animationDuration / 10;

    private float optionSize = 1;

    private boolean open = false;

    Runnable onSelectOption;
    Runnable onClose;
    Runnable onOpen;
    Runnable onStartClose;
    Runnable onStartOpen;

    private static Dropdown defaultDropdown = new Dropdown(0, 0, 400, 500, new color(255),
            new color(200), new color(150), new color(0), "Select", 20, new color(0), new color(230)).setTextAlignment(
                    TextAlignment.LEFT);

    private void dropdownSetup() {
        push();
        textSize(textSize);
        optionSize = textHeight("A") * 1.5f;
        pop();

        selectedOption = new Button(pos.x, pos.y, size.x, optionSize, defaultColor, hoverColor, activeColor,
                strokeColor,
                defaultText, textSize, textColor).onClick(() -> {
                    if (options.size() == 0)
                        return;
                    if (!interactive || !active)
                        return;
                    if (options.get(0).active)
                        close();
                    else
                        open();
                });
        selectedOption.textAlignment = textAlignment;
    }

    public Dropdown(PVector pos, PVector size, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, String defaultText, double textSize, color textColor, color separatorColor) {
        this.pos = pos;
        this.size = size;

        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.strokeColor = strokeColor;

        this.defaultText = defaultText;
        this.textSize = (float) textSize;
        this.textColor = textColor;

        this.separatorColor = separatorColor;
        if (defaultDropdown == null)
            this.textAlignment = TextAlignment.LEFT;
        else
            this.textAlignment = defaultDropdown.textAlignment;

        currentColor = defaultColor;

        dropdownSetup();
    }

    public Dropdown(double x, double y, double w, double h, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, String defaultText, double textSize, color textColor, color separatorColor) {
        this(new PVector(x, y), new PVector(w, h), defaultColor, hoverColor, activeColor, strokeColor, defaultText,
                textSize, textColor, separatorColor);
    }

    public Dropdown(PVector pos, PVector size) {
        clone(defaultDropdown);
        setPos(pos);
        setSize(size);
        dropdownSetup();
    }

    public Dropdown(double x, double y, double w, double h) {
        this(new PVector(x, y), new PVector(w, h));
    }

    public Dropdown(PVector pos) {
        clone(defaultDropdown);
        setPos(pos);
        dropdownSetup();
    }

    public Dropdown(double x, double y) {
        this(new PVector(x, y));
    }

    public Dropdown copy() {
        Dropdown copy = new Dropdown(pos.copy(), size.copy(), defaultColor.copy(), hoverColor.copy(),
                activeColor.copy(), strokeColor.copy(), new String(defaultText), textSize, textColor.copy(),
                separatorColor.copy());
        copy.onSelectOption(onSelectOption);
        copy.onClose(onClose);
        copy.onOpen(onOpen);
        copy.onStartClose(onStartClose);
        copy.onStartOpen(onStartOpen);
        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setCornerRadius(cornerRadius);
        copy.setStrokeWeight(strokeWeight);
        copy.setInteractive(interactive);
        copy.setActive(active);
        copy.setTextAlignment(textAlignment);
        copy.setAlpha(alpha);

        copy.options = new ArrayList<>(options.size());
        for (Button button : options) {
            copy.addOption(button.text);
        }

        copy.animationDuration = animationDuration;
        copy.animationDelay = animationDelay;
        copy.optionSize = optionSize;
        copy.open = open;
        if (open)
            copy.openNoAnimation();

        return copy;
    }

    public void clone(Dropdown dropdown) {
        pos = dropdown.pos.copy();
        size = dropdown.size.copy();
        defaultColor = dropdown.defaultColor.copy();
        hoverColor = dropdown.hoverColor.copy();
        activeColor = dropdown.activeColor.copy();
        strokeColor = dropdown.strokeColor.copy();
        defaultText = dropdown.defaultText;
        textSize = dropdown.textSize;
        textColor = dropdown.textColor.copy();
        separatorColor = dropdown.separatorColor.copy();
        textAlignment = dropdown.textAlignment;
        currentColor = defaultColor;
        options.clear();
        for (Button option : dropdown.options) {
            Button newOption = option.copy();
            options.add(newOption);
        }
        selectedOption = dropdown.selectedOption.copy();
    }

    public static void setDefaults(PVector defaultSize, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, String defaultText, double textSize, color textColor, color separatorColor,
            TextAlignment textAlignment) {
        defaultDropdown = new Dropdown(PVector.zero(), defaultSize, defaultColor, hoverColor, activeColor,
                strokeColor, defaultText, textSize, textColor, separatorColor).setTextAlignment(textAlignment);
    }

    public static void setDefaults(Dropdown defaults) {
        defaultDropdown = defaults;
    }

    public static Dropdown getDefaults() {
        return defaultDropdown;
    }

    public boolean hover() {
        if (open) {
            for (Button option : options) {
                if (option.hover())
                    return true;
            }
        }
        return selectedOption.hover();
    }

    public Dropdown addOption(String text) {
        PVector buttonPos = pos;
        if (open && options.size() > 0) {
            buttonPos = new PVector(pos.x, pos.y + (options.size() + 1) * optionSize);
        }

        Button button = new Button(buttonPos.x, buttonPos.y, size.x, optionSize, defaultColor, hoverColor, activeColor,
                color(0, 0),
                text, textSize, textColor).onClick(() -> {
                    selectedOption.text = text;
                    close();
                    onSelectOption();
                });
        if (!open)
            button.setActive(false);
        else
            button.setActive(active);
        button.textAlignment = textAlignment;
        options.add(button);
        return this;
    }

    public Dropdown addOptions(String... texts) {
        for (String text : texts) {
            addOption(text);
        }
        return this;
    }

    public Dropdown setSelectedOption(String text) {
        selectedOption.text = text;
        return this;
    }

    public Dropdown setAnimationDuration(double duration) {
        this.animationDuration = (float) duration;
        animationDelay = animationDuration / 15;
        return this;
    }

    public double getAnimationDuration() {
        return animationDuration;
    }

    public Dropdown setSeparatorColor(color separatorColor) {
        this.separatorColor = separatorColor;
        return this;
    }

    public color getSeparatorColor() {
        return separatorColor;
    }

    public Dropdown setDefaultColor(color defaultColor) {
        this.defaultColor = defaultColor;
        selectedOption.defaultColor = defaultColor;
        for (Button option : options) {
            option.defaultColor = defaultColor;
        }
        return this;
    }

    public Dropdown setHoverColor(color hoverColor) {
        this.hoverColor = hoverColor;
        selectedOption.hoverColor = hoverColor;
        for (Button option : options) {
            option.hoverColor = hoverColor;
        }
        return this;
    }

    public Dropdown setActiveColor(color activeColor) {
        this.activeColor = activeColor;
        selectedOption.activeColor = activeColor;
        for (Button option : options) {
            option.activeColor = activeColor;
        }
        return this;
    }

    public Dropdown setInteractive(boolean interactive) {
        this.interactive = interactive;
        selectedOption.setInteractive(interactive);
        for (Button option : options) {
            option.setInteractive(interactive);
        }
        return this;
    }

    public Dropdown setAlpha(double alpha) {
        this.alpha = (float) alpha;
        selectedOption.alpha = this.alpha;
        for (Button option : options) {
            option.alpha = this.alpha;
        }
        return this;
    }

    public Dropdown setCornerRadius(double cornerRadius) {
        this.cornerRadius = (int) cornerRadius;
        for (Button option : options) {
            option.cornerRadius = this.cornerRadius;
        }
        selectedOption.cornerRadius = this.cornerRadius;
        return this;
    }

    public Dropdown setDefaultText(String text) {
        if (selectedOption.text.equals(defaultText))
            selectedOption.text = text;
        defaultText = text;
        return this;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public Dropdown setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        selectedOption.textAlignment = textAlignment;
        for (Button option : options) {
            option.textAlignment = textAlignment;
        }
        return this;
    }

    public Dropdown setPos(PVector pos) {
        this.pos = pos;
        selectedOption.setPos(pos);

        if (open) {
            for (Button option : options) {
                option.setPos(pos.x, pos.y + (options.indexOf(option) + 1) * optionSize);
            }
        } else {
            for (Button option : options) {
                option.setPos(pos.x, pos.y);
            }
        }

        return this;
    }

    public Dropdown setPos(float x, float y) {
        return setPos(new PVector(x, y));
    }

    public Dropdown setSize(PVector size) {
        this.size = size;
        selectedOption.setSize(size.x, optionSize);

        for (Button option : options) {
            option.setSize(size.x, optionSize);
        }

        return this;
    }

    public Dropdown setSize(float w, float h) {
        return setSize(new PVector(w, h));
    }

    /**
     * Returns the actual size the dropdown takes up (dependent on whether it is
     * open or not).
     */
    public PVector getSizeActual() {
        float h = optionSize;
        if (open && options.size() > 0) {
            h += (options.size() * optionSize);
        }
        return new PVector(size.x, h);
    }

    /**
     * Returns the center of the dropdown, dependent on whether it is open or not.
     */
    public PVector getPosActualCenter() {
        float h = 0;
        if (open && options.size() > 0) {
            h += (options.size() * optionSize) / 2;
        }
        return new PVector(pos.x, pos.y + h);
    }

    public Dropdown onSelectOption(Runnable onSelectOption) {
        this.onSelectOption = onSelectOption;
        return this;
    }

    public void onSelectOption() {
        if (onSelectOption != null)
            onSelectOption.run();
    }

    public Dropdown onClose(Runnable onClose) {
        this.onClose = onClose;
        return this;
    }

    public void onClose() {
        if (onClose != null)
            onClose.run();
    }

    public Dropdown onOpen(Runnable onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    public void onOpen() {
        if (onOpen != null)
            onOpen.run();
    }

    /**
     * Runs when the dropdown starts closing.
     */
    public Dropdown onStartClose(Runnable onStartClose) {
        this.onStartClose = onStartClose;
        return this;
    }

    public void onStartClose() {
        if (onStartClose != null)
            onStartClose.run();
    }

    /**
     * Runs when the dropdown starts opening.
     */
    public Dropdown onStartOpen(Runnable onStartOpen) {
        this.onStartOpen = onStartOpen;
        return this;
    }

    public void onStartOpen() {
        if (onStartOpen != null)
            onStartOpen.run();
        onOpen();
    }

    public Dropdown calculateTextSize() {
        // Set text size so that the longest option fits
        String longestText = defaultText;
        for (Button option : options) {
            longestText = option.text.length() > longestText.length() ? option.text : longestText;
        }

        textSize = (size.x / longestText.length());
        textSize(textSize);
        optionSize = textHeight("A") * 1.5f;
        scaleOptions();
        return this;
    }

    public Dropdown scaleOptions() {
        for (Button option : options) {
            option.size.y = optionSize;
            option.textSize = textSize;
        }
        selectedOption.size.y = optionSize;
        selectedOption.textSize = textSize;
        return this;
    }

    public Dropdown open() {
        open = true;
        onStartOpen();

        for (int i = 0; i < options.size(); i++) {
            Button option = options.get(i);

            option.setActive(active); // Set active to be whatever I am (so if I'm not active, don't activate options,
                                      // but also vise-versa)
            option.interactive = false;
            final int iFinal = i;
            new Animator(option, animationDuration).setPos(pos.x, pos.y + (i + 1) * optionSize)
                    .setDelay(i * animationDelay)
                    .onEnd(() -> {
                        option.interactive = interactive; // Same logic as active
                        if (iFinal == 0)
                            onOpen();
                    });

        }
        return this;
    }

    public Dropdown openNoAnimation() {
        open = true;
        onStartOpen();

        for (int i = 0; i < options.size(); i++) {
            Button option = options.get(i);

            option.setActive(active);
            option.setInteractive(interactive);
            option.setPos(pos.x, pos.y + (i + 1) * optionSize);

        }
        onOpen();

        return this;
    }

    public Dropdown close() {
        if (!interactive || !active)
            return this;

        open = false;
        onStartClose();

        for (int i = options.size() - 1; i >= 0; i--) {
            Button option = options.get(i);
            option.interactive = false;
            new Animator(option, animationDuration).setPos(pos.x, pos.y)
                    .setDelay((options.size() - i) * animationDelay).onEnd(() -> {
                        option.setActive(false);
                        onClose();
                    });
        }
        return this;
    }

    public String getSelectedOption() {
        return selectedOption.text;
    }

    public boolean isOpen() {
        return open;
    }

    public void mousePressed() {
        if (interactive && active && open && mouseButton == LEFT && !hover()) {
            close();
        }
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

        for (Button option : options) {
            float prevAlpha = option.getAlpha();
            option.setAlpha(prevAlpha * (alpha / 255.0f));
            option.draw();
            option.setAlpha(prevAlpha);
        }

        // Separator lines
        stroke(separatorColor, separatorColor.a * (alpha / 255.0f));
        strokeWeight(0.75 * strokeWeight);
        for (int i = 0; i < options.size() - 1; i++) {
            Button option = options.get(i);
            if (!option.active)
                continue;

            // Because of rounded edges, we stop a little short
            float padding = cornerRadius / 2.0f;
            line(option.pos.x - option.size.x / 2 + padding, option.pos.y + option.size.y / 2,
                    option.pos.x + option.size.x / 2 - padding,
                    option.pos.y + option.size.y / 2);
        }

        selectedOption.draw();

        // Outline
        noFill();
        stroke(strokeColor, strokeColor.a * (alpha / 255.0f));
        strokeWeight(strokeWeight);
        rectMode(CORNER);
        float h = 0;
        if (options.size() > 0)
            h = PVector.sub(options.get(options.size() - 1).pos, pos).y + optionSize;
        else
            h = optionSize;
        rect(pos.x - size.x / 2, pos.y - optionSize / 2, size.x, h, cornerRadius);
    }

    public void delete() {
        super.delete();
        for (Button option : options) {
            option.delete();
        }
        options.clear();
        selectedOption.delete();
    }
}
