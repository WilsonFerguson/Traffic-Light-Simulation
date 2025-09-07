package GameEngine;

import java.util.*;
import library.core.*;

public class Dropdown extends Interactable {

    ArrayList<Button> options = new ArrayList<Button>();
    Button selectedOption;

    String defaultText;

    color separatorColor; // color that separates the options

    public float animationDuration = 0.35f; // seconds
    private float animationDelay = animationDuration / 10;

    private float optionSize = 1;

    private boolean open = false;

    Runnable onSelectOption;

    // Defaults
    public static PVector DEFAULT_SIZE = new PVector(400, 500);
    public static color DEFAULT_COLOR = new color(255);
    public static color HOVER_COLOR = new color(200);
    public static color ACTIVE_COLOR = new color(150);
    public static color STROKE_COLOR = new color(0);
    public static String DEFAULT_TEXT = "Select";
    public static int TEXT_SIZE = 20;
    public static color TEXT_COLOR = new color(0);
    public static color SEPARATOR_COLOR = new color(230);

    public Dropdown(PVector pos, PVector size, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, String defaultText, int textSize, color textColor, color separatorColor) {
        this.pos = pos;
        this.size = size;

        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.strokeColor = strokeColor;

        this.defaultText = defaultText;
        this.textSize = textSize;
        this.textColor = textColor;

        this.separatorColor = separatorColor;

        currentColor = defaultColor;

        textSize(textSize);
        optionSize = textHeight("A") * 1.5f;

        selectedOption = new Button(pos.x, pos.y, size.x, optionSize, defaultColor, hoverColor, activeColor,
                strokeColor,
                defaultText, textSize, textColor).onClick(() -> {
                    if (options.size() == 0)
                        return;
                    if (options.get(0).active)
                        close();
                    else
                        open();
                });
        selectedOption.textAlignment = TextAlignment.LEFT;
    }

    public Dropdown(double x, double y, double w, double h, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, String defaultText, int textSize, color textColor, color separatorColor) {
        this(new PVector(x, y), new PVector(w, h), defaultColor, hoverColor, activeColor, strokeColor, defaultText,
                textSize, textColor, separatorColor);
    }

    public Dropdown(PVector pos, PVector size) {
        this(pos, size, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(), STROKE_COLOR.copy(),
                DEFAULT_TEXT, TEXT_SIZE, TEXT_COLOR.copy(),
                SEPARATOR_COLOR.copy());
    }

    public Dropdown(double x, double y, double w, double h) {
        this(x, y, w, h, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(), ACTIVE_COLOR.copy(), STROKE_COLOR.copy(),
                DEFAULT_TEXT, TEXT_SIZE, TEXT_COLOR.copy(),
                SEPARATOR_COLOR.copy());
    }

    public Dropdown(PVector pos) {
        this(pos, DEFAULT_SIZE.copy());
    }

    public Dropdown(double x, double y) {
        this(x, y, DEFAULT_SIZE.copy().x, DEFAULT_SIZE.copy().y);
    }

    public static void setDefaults(PVector defaultSize, color defaultColor, color hoverColor, color activeColor,
            color strokeColor, String defaultText, int textSize, color textColor, color separatorColor) {
        DEFAULT_SIZE = defaultSize;
        DEFAULT_COLOR = defaultColor;
        HOVER_COLOR = hoverColor;
        ACTIVE_COLOR = activeColor;
        STROKE_COLOR = strokeColor;
        DEFAULT_TEXT = defaultText;
        TEXT_SIZE = textSize;
        TEXT_COLOR = textColor;
        SEPARATOR_COLOR = separatorColor;
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
        Button button = new Button(pos.x, pos.y, size.x, optionSize, defaultColor, hoverColor, activeColor,
                color(0, 0),
                text, textSize, textColor).onClick(() -> {
                    selectedOption.text = text;
                    close();
                    onSelectOption();
                });
        button.setActive(false);
        button.textAlignment = TextAlignment.LEFT;
        options.add(button);
        return this;
    }

    public Dropdown addOptions(String... texts) {
        for (String text : texts) {
            addOption(text);
        }
        return this;
    }

    public Dropdown setAnimationDuration(float duration) {
        this.animationDuration = duration;
        animationDelay = animationDuration / 15;
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

    public Dropdown onSelectOption(Runnable onSelectOption) {
        this.onSelectOption = onSelectOption;
        return this;
    }

    public void onSelectOption() {
        if (onSelectOption != null)
            onSelectOption.run();
    }

    public Dropdown calculateTextSize() {
        // Set text size so that the longest option fits
        String longestText = defaultText;
        for (Button option : options) {
            longestText = option.text.length() > longestText.length() ? option.text : longestText;
        }

        textSize = (int) (size.x / longestText.length());
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
        if (!interactive || !active)
            return this;
        open = true;
        for (int i = 0; i < options.size(); i++) {
            Button option = options.get(i);

            option.setActive(true);
            option.interactive = false;
            new Animator(option, animationDuration).setPos(pos.x, pos.y + (i + 1) * optionSize)
                    .delay(i * animationDelay)
                    .onEnd(() -> {
                        option.interactive = true;
                    });

        }
        return this;
    }

    public Dropdown close() {
        if (!interactive || !active)
            return this;
        open = false;
        for (int i = options.size() - 1; i >= 0; i--) {
            Button option = options.get(i);
            option.interactive = false;
            new Animator(option, animationDuration).setPos(pos.x, pos.y)
                    .delay((options.size() - i) * animationDelay).onEnd(() -> {
                        option.setActive(false);
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
        if (open && !hover()) {
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
            option.draw();
        }

        // Separator lines
        stroke(separatorColor);
        strokeWeight(1.5);
        for (int i = 0; i < options.size() - 1; i++) {
            Button option = options.get(i);
            if (!option.active)
                continue;
            line(option.pos.x - option.size.x / 2, option.pos.y + option.size.y / 2,
                    option.pos.x + option.size.x / 2,
                    option.pos.y + option.size.y / 2);
        }

        selectedOption.draw();

        // Outline
        noFill();
        stroke(strokeColor);
        strokeWeight(2);
        rectMode(CORNER);
        float h = 0;
        if (options.size() > 0)
            h = PVector.sub(options.get(options.size() - 1).pos, pos).y + optionSize;
        else
            h = optionSize;
        rect(pos.x - size.x / 2, pos.y - optionSize / 2, size.x, h, cornerRadius);
    }

}
