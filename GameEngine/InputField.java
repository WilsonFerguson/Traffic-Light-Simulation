package GameEngine;

import library.core.*;

public class InputField extends Interactable {

  private boolean selected = false;
  private String defaultText;

  private boolean numbersOnly = false;
  private boolean autoResize = false;

  private Runnable onEnter;

  // Defaults
  private static InputField defaultInputField =
      new InputField(0, 0, 300, 100, new color(200), new color(230),
                     new color(180), new color(0), "", 32, new color(0));

  public static PVector DEFAULT_SIZE = new PVector(300, 100);
  public static color DEFAULT_COLOR = new color(255);
  public static color HOVER_COLOR = new color(200);
  public static color ACTIVE_COLOR = new color(150);
  public static color STROKE_COLOR = new color(0);
  public static int TEXT_SIZE = 20;
  public static color TEXT_COLOR = new color(0);
  public static String DEFAULT_TEXT = "";

  public InputField(PVector pos, PVector size, color defaultColor,
                    color hoverColor, color activeColor, color strokeColor,
                    String defaultText, int textSize, color textColor) {
    this.pos = pos;
    this.size = size;

    this.defaultColor = defaultColor;
    this.hoverColor = hoverColor;
    this.activeColor = activeColor;
    this.strokeColor = strokeColor;

    this.defaultText = defaultText;
    this.text = "";
    this.textSize = textSize;
    this.textColor = textColor;

    currentColor = defaultColor;

    textAlignment = TextAlignment.LEFT;
  }

  public InputField(double x, double y, double w, double h, color defaultColor,
                    color hoverColor, color activeColor, color strokeColor,
                    String defaultText, int textSize, color textColor) {
    this(new PVector(x, y), new PVector(w, h), defaultColor, hoverColor,
         activeColor, strokeColor, defaultText, textSize, textColor);
  }

  public InputField(PVector pos, PVector size) {
    // this(pos, size, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
    // ACTIVE_COLOR.copy(), STROKE_COLOR.copy(),
    // DEFAULT_TEXT, TEXT_SIZE, TEXT_COLOR.copy());
    clone(defaultInputField);
    this.pos = pos;
    this.size = size;
  }

  public InputField(double x, double y, double w, double h) {
    // this(x, y, w, h, DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
    // ACTIVE_COLOR.copy(), STROKE_COLOR.copy(),
    // DEFAULT_TEXT, TEXT_SIZE, TEXT_COLOR.copy());
    this(new PVector(x, y), new PVector(w, h));
  }

  public InputField(PVector pos) {
    // this(pos, DEFAULT_SIZE.copy(), DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
    // ACTIVE_COLOR.copy(),
    // STROKE_COLOR.copy(),
    // DEFAULT_TEXT, TEXT_SIZE,
    // TEXT_COLOR.copy());
    this(pos, defaultInputField.size.copy());
  }

  public InputField(double x, double y) {
    // this(x, y, DEFAULT_SIZE.copy().x, DEFAULT_SIZE.copy().y,
    // DEFAULT_COLOR.copy(), HOVER_COLOR.copy(),
    // ACTIVE_COLOR.copy(), STROKE_COLOR.copy(), DEFAULT_TEXT,
    // TEXT_SIZE, TEXT_COLOR.copy());
    this(new PVector(x, y));
  }

  public static void setDefaults(InputField defaults) {
    defaultInputField = defaults;
  }

  public static InputField getDefaults() { return defaultInputField; }

  public InputField copy() {
    InputField copy = new InputField(pos.copy(), size.copy(),
                                     defaultColor.copy(), hoverColor.copy(),
                                     activeColor.copy(), strokeColor.copy(),
                                     defaultText, textSize, textColor.copy());
    copy.setCornerRadius(cornerRadius);
    copy.setNumbersOnly(numbersOnly);
    copy.onEnter(onEnter);
    copy.onHover(onHover);
    copy.onHoverExit(onHoverExit);
    copy.setActive(active);
    copy.setInteractive(interactive);
    copy.setText(text);
    copy.setTextAlignment(textAlignment);
    copy.setAutoResize(autoResize);
    return copy;
  }

  public void clone(InputField inputField) {
    pos = inputField.pos.copy();
    size = inputField.size.copy();
    defaultColor = inputField.defaultColor.copy();
    hoverColor = inputField.hoverColor.copy();
    activeColor = inputField.activeColor.copy();
    strokeColor = inputField.strokeColor.copy();
    defaultText = inputField.defaultText;
    textSize = inputField.textSize;
    textColor = inputField.textColor.copy();
    cornerRadius = inputField.cornerRadius;
    numbersOnly = inputField.numbersOnly;
    onEnter = inputField.onEnter;
    onHover = inputField.onHover;
    onHoverExit = inputField.onHoverExit;
    active = inputField.active;
    interactive = inputField.interactive;
    text = inputField.text;
    textAlignment = inputField.textAlignment;

    currentColor = defaultColor;
  }

  public static void setDefaults(PVector defaultSize, color defaultColor,
                                 color hoverColor, color activeColor,
                                 color strokeColor, String defaultText,
                                 int textSize, color textColor) {
    DEFAULT_SIZE = defaultSize;
    DEFAULT_COLOR = defaultColor;
    HOVER_COLOR = hoverColor;
    ACTIVE_COLOR = activeColor;
    STROKE_COLOR = strokeColor;
    DEFAULT_TEXT = defaultText;
    TEXT_SIZE = textSize;
    TEXT_COLOR = textColor;
  }

  public InputField setCornerRadius(double radius) {
    this.cornerRadius = (int)radius;
    return this;
  }

  public InputField setDefaultText(String defaultText) {
    this.defaultText = defaultText;
    return this;
  }

  public InputField setNumbersOnly(boolean numbersOnly) {
    this.numbersOnly = numbersOnly;
    return this;
  }

  public InputField setAutoResize(boolean autoResize) {
    this.autoResize = autoResize;
    return this;
  }

  public InputField onEnter(Runnable onEnter) {
    this.onEnter = onEnter;
    return this;
  }

  public void onEnter() {
    if (onEnter != null)
      onEnter.run();
  }

  public String getText() {
    if (numbersOnly)
      return text.replace(",", "");
    return text;
  }

  public InputField setText(String text) {
    this.text = text;
    return this;
  }

  public boolean isSelected() { return selected; }

  public InputField setSelected(boolean selected) {
    this.selected = selected;
    return this;
  }

  public void mousePressed() {
    if (!active)
      return;
    if (hover() && interactive)
      selected = !selected;
    if (!hover())
      selected = false;
  }

  public void keyTyped() {
    if (selected) {
      if (keyString.equals("Backspace")) {
        if (text.length() > 0) {
          text = text.substring(0, text.length() - 1);
        }
      } else if (keyString.equals("Enter")) {
        onEnter();
        selected = false;
      } else if (keyString.equals("Escape")) {
        selected = false;
      } else if (key == ' ' && !numbersOnly) {
        text += " ";
      } else {
        if (numbersOnly) {
          // numbers or decimal point
          if (key >= '0' && key <= '9') {
            text += key;
          }
        } else {
          text += key;
        }
      }

      if (autoResize) {
        push();
        textSize(textSize);
        float textWidth = textWidth(text);
        if (textWidth > size.x - 2 * 10) {
          size.x = textWidth + 2 * 10;
        }
        pop();
      }
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

    color targetColor = defaultColor;

    if (isHovered && !selected) {
      targetColor = hoverColor;
    } else if (selected) {
      targetColor = activeColor;
    }

    currentColor =
        lerpColor(currentColor, targetColor, Animator.colorLerpAmount);
    fill(currentColor);

    if (!interactive)
      fill(defaultColor);

    stroke(strokeColor);
    strokeWeight(2);
    rectMode(CENTER);
    rect(pos, size, cornerRadius);

    fill(textColor);
    textSize(textSize);
    textAlign(textAlignment);

    String shownText = text == "" ? defaultText : text;
    if (textAlignment.equals(TextAlignment.LEFT))
      text(shownText, pos.x - size.x / 2 + 10, pos.y + textSize / 2 - 5);
    else if (textAlignment.equals(TextAlignment.CENTER))
      text(shownText, pos);
    else if (textAlignment.equals(TextAlignment.RIGHT))
      text(shownText, pos.x + size.x / 2 - 10, pos.y + textSize / 2 - 5);
  }
}
