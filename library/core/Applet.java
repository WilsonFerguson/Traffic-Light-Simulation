package library.core;

import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.*;

import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;
import java.lang.reflect.Array;

public class Applet extends JPanel implements PConstants, Runnable {

    private JFrame frame;
    private BufferedImage img;
    private Graphics2D g2d;

    private String frameTitle = "Sketch";

    private AffineTransform oldTransform = new AffineTransform();

    public color[] pixels;
    private boolean hasDrawnSinceLoadPixels = true; // If false, loadPixels() will not do anything because it doesn't
                                                    // need to update anything new. Initially true to load the pixels

    // Alignment modes
    private int rectMode = CORNER;
    private int ellipseMode = CENTER;
    private int textAlign = LEFT;

    // Colors
    private color fillColor = color(0);
    private color strokeColor = color(0);

    // Text
    private float strokeWeight = 1;
    private float textSize = 12;
    private String textFont = "Arial";

    // Transformations
    private float rotation = 0;
    private PVector translation = PVector.zero();
    private float scale = 1;

    // Mouse
    public int mouseX = 0;
    public int mouseY = 0;
    public PVector mouse = PVector.zero();
    public int pmouseX = 0;
    public int pmouseY = 0;
    public PVector pmouse = PVector.zero();

    // Event calls
    private ArrayList<AWTEvent> eventQueue = new ArrayList<AWTEvent>();

    // Width/height settings
    public int width;
    public int height;
    public int displayWidth;
    public int displayHeight;
    private double universalScale = 1;
    private static double uiScale = 1;
    private PVector frameImageSizeDifference; // Difference between the frame size and the image size

    // Input States
    public boolean mousePressed = false;
    public boolean keyPressed = false;
    public int mouseButton = CENTER;
    public char key;
    public String keyString = "";
    public int keyCode = 0;
    public HashSet<String> keysPressed = new HashSet<String>();
    public AWTEvent awtEvent;

    // Shape
    private ArrayList<PVector> points = new ArrayList<PVector>();
    private int shapeMode = RIGID;

    // FPS
    private int targetFrameRate = 60;
    private ArrayList<Double> frameRates = new ArrayList<Double>();
    public float frameRate = 0;
    private double lastTime = 0;
    public int frameCount = 0;
    public float deltaTime = 0;
    private double startTime = 0;
    private boolean displayFrameRate = false;

    private boolean exitOnEscape = true;

    private boolean fullScreen = false;

    // Push/Pop
    private Stack<DrawSettings> drawSettingsStack = new Stack<>();

    private boolean shouldLoop = true; // Should draw be called
    private boolean shouldRedraw = false; // When redraw() is called, this is set to true

    // Game Loop
    private Thread gameLoopThread;
    private KeyHandler keyHandler = new KeyHandler(this);
    private MouseHandler mouseHandler = new MouseHandler(this);
    private WindowHandler windowHandler = new WindowHandler(this);

    public static void fixWindowsScaling() {
        // Note: have to do this in this order so that I can store the original scale
        // but still modify the scale before it's too late
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            System.setProperty("sun.java2d.uiScale", "1.0");
            uiScale = toolkit.getScreenResolution() / 96.0;
        }
    }

    public void startApplet() {
        PComponent.applet = this;

        // Setup some variables here so if they don't call size or fullScreen, they
        // still have functionality
        startTime = System.currentTimeMillis();
        lastTime = System.currentTimeMillis();
        NoiseHelper.noiseSeed = (long) MathHelper.random(100000);

        setup();

        gameLoopThread = new Thread(this);
        gameLoopThread.start();
    }

    @Override
    public void run() {
        while (gameLoopThread != null) {
            long start = System.currentTimeMillis();
            handleDrawStart();

            paintImmediately(0, 0, getWidth(), getHeight());

            handleDrawEnd(start);
        }
    }

    private void handleDrawStart() {
        PComponent.updateVariables();

        if (shouldLoop) // If we should loop, we should redraw
            shouldRedraw = true;

        if (shouldRedraw && frameCount > 0) { // If we should loop or manually redraw, we should redraw
            draw();
            shouldRedraw = false;

            if (displayFrameRate)
                renderFrameRate();
        }

        if (frameCount > 1 && frame.isVisible() == false)
            frame.setVisible(true);
    }

    private void handleDrawEnd(long start) {
        rotation = 0;
        translation = PVector.zero();
        scale = 1;
        drawSettingsStack.clear();

        // Update mouse
        pmouseX = mouseX;
        pmouseY = mouseY;
        pmouse = mouse.copy();
        PComponent.pmouseX = PComponent.mouseX;
        PComponent.pmouseY = PComponent.mouseY;
        PComponent.pmouse = PComponent.mouse.copy();

        handleEvents();

        handleFrameRate(start);
    }

    private void handleFrameRate(long startTime) {
        long end = System.currentTimeMillis();
        long time = end - startTime;
        if (time < 1000 / targetFrameRate) {
            Helper.delay(1000 / targetFrameRate - time);
        }

        // Update the frame rate
        double nextFrameRate = 1000.0 / (millis() - lastTime);
        if (frameRates.size() < 10) {
            frameRates.add(nextFrameRate);
        } else {
            frameRates.remove(0);
            frameRates.add(nextFrameRate);
        }
        frameRate = 0;
        for (double frameRate : frameRates) {
            this.frameRate += frameRate;
        }
        this.frameRate /= frameRates.size();
        lastTime = millis();

        deltaTime = (float) (millis() - startTime) / 1000f;

        frameCount++;
    }

    private void postSetup() {
        setFocusable(true);
        addKeyListener(keyHandler);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
        frame.addWindowListener(windowHandler);

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                eventQueue.add(componentEvent);
            }
        });

        // Set icon
        ArrayList<Image> icons = new ArrayList<Image>();
        icons.add(new ImageIcon("icon.png").getImage());
        icons.add(new ImageIcon("icon2.png").getImage());
        icons.add(new ImageIcon("icon3.png").getImage());
        frame.setIconImages(icons);

        frameImageSizeDifference = new PVector(frame.getWidth() - width, frame.getHeight() - height);
    }

    public void size(int width, int height) {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode displayMode = device.getDisplayMode();
        displayWidth = displayMode.getWidth();
        displayHeight = displayMode.getHeight();

        this.width = width;
        this.height = height;
        PComponent.width = width;
        PComponent.height = height;
        PComponent.displayWidth = displayWidth;
        PComponent.displayHeight = displayHeight;

        universalScale = 1;

        Dimension size = new Dimension(width, height);
        setPreferredSize(size);

        frame = new JFrame(frameTitle);
        frame.add(this);

        frame.setBackground(Color.BLACK);
        frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.setLocationRelativeTo(null);

        // Post setup:
        postSetup();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.scale(universalScale, universalScale);
        pixels = new color[width * height];
        smooth();

        // frame.setVisible(true);
    }

    /**
     * Note: Automatically sets width to 1920, and height to the same ratio as your
     * display.
     */
    public void fullScreen() {
        Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize();
        int displayWidthFake = (int) displaySize.getWidth();

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode displayMode = device.getDisplayMode();
        displayWidth = displayMode.getWidth();
        displayHeight = displayMode.getHeight();

        universalScale = displayWidth / 1920.0 * ((double) displayWidthFake / displayWidth);

        width = 1920;
        height = (int) (displayHeight * (1920.0 / displayWidth));

        PComponent.width = width;
        PComponent.height = height;
        PComponent.displayWidth = displayWidth;
        PComponent.displayHeight = displayHeight;

        Dimension size = new Dimension(displayWidth, displayHeight);
        setPreferredSize(size);

        frame = new JFrame(frameTitle);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);

        frame.add(this);

        // Set frame background to black
        frame.setBackground(Color.BLACK);

        frame.pack();
        frame.setResizable(false);

        // Post setup:
        postSetup();

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fullScreen = true;

        img = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.scale(universalScale, universalScale);
        pixels = new color[displayWidth * displayHeight];
        smooth();

        // frame.setVisible(true);
    }

    public double getWindowScale() {
        if (uiScale != 1)
            return uiScale;

        return Toolkit.getDefaultToolkit().getScreenResolution() / 96.0;
    }

    public double getUIScale() {
        return uiScale;
    }

    public PVector getTrueScreenSize() {
        return new PVector(displayWidth, displayHeight);
    }

    public PVector getScaledScreenSize() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        return new PVector(size.getWidth(), size.getHeight());
    }

    public void setResizable(boolean resizable) {
        frame.setResizable(resizable);
    }

    public JFrame getFrame() {
        return frame;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(width, height);
    }

    public Graphics2D getGraphics2D() {
        return g2d;
    }

    public double getUniversalScale() {
        return universalScale;
    }

    public void setTitle(String title) {
        frameTitle = title;
        if (frame != null)
            frame.setTitle(title);
    }

    // Events
    public void addEvent(AWTEvent evt) {
        eventQueue.add(evt);
    }

    private void handleEvents() {
        while (!eventQueue.isEmpty()) {
            awtEvent = eventQueue.remove(0);
            PComponent.awtEvent = awtEvent;
            simulateEvent(awtEvent);
        }
    }

    private void handleMouseEvent(MouseEvent evt) {
        if (evt.getID() == MouseEvent.MOUSE_PRESSED) {
            mousePress(evt);
        } else if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
            mouseRelease(evt);
        } else if (evt.getID() == MouseEvent.MOUSE_CLICKED) {
            mouseClick(evt);
        } else if (evt.getID() == MouseEvent.MOUSE_MOVED) {
            mouseMove(evt);
        } else if (evt.getID() == MouseEvent.MOUSE_DRAGGED) {
            mouseDrag(evt);
        } else if (evt.getID() == MouseEvent.MOUSE_WHEEL) {
            mouseScroll(((MouseWheelEvent) evt).getWheelRotation());
        } else if (evt.getID() == MouseEvent.MOUSE_ENTERED) {
            mouseEnter(evt);
        } else if (evt.getID() == MouseEvent.MOUSE_EXITED) {
            mouseExit(evt);
        }
    }

    private void handleKeyEvent(KeyEvent evt) {
        if (evt.getID() == KeyEvent.KEY_PRESSED) {
            keyPress(evt);
        } else if (evt.getID() == KeyEvent.KEY_RELEASED) {
            keyRelease(evt);
        } else if (evt.getID() == KeyEvent.KEY_TYPED) {
            keyType(evt);
        }
    }

    private void handleWindowEvent(WindowEvent evt) {
        if (evt.getID() == WindowEvent.WINDOW_OPENED) {
            windowOpened();
        } else if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
            windowClosing();
            onExit();
        } else if (evt.getID() == WindowEvent.WINDOW_CLOSED) {
            windowClosed();
        } else if (evt.getID() == WindowEvent.WINDOW_ICONIFIED) {
            windowIconified();
        } else if (evt.getID() == WindowEvent.WINDOW_DEICONIFIED) {
            windowDeiconified();
        } else if (evt.getID() == WindowEvent.WINDOW_ACTIVATED) {
            windowActivated();
        } else if (evt.getID() == WindowEvent.WINDOW_DEACTIVATED) {
            windowDeactivated();
        }
    }

    private void handleComponentEvent(ComponentEvent evt) {
        if (evt.getID() == ComponentEvent.COMPONENT_RESIZED) {
            windowResize();
        }
    }

    // Mouse inputs (protected so that they can be overridden)
    protected void mousePress(MouseEvent evt) {
        mousePressed = true;
        mouseButton = evt.getButton();

        PComponent.mousePressed = true;
        PComponent.mouseButton = evt.getButton();

        mousePressed();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).mousePressed();
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().mousePressed();
        // }
    }

    protected void mousePressed() {

    }

    protected void mouseRelease(MouseEvent evt) {
        mousePressed = false;
        mouseButton = CENTER;

        PComponent.mousePressed = false;
        PComponent.mouseButton = CENTER;

        mouseReleased();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).mouseReleased();
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().mouseReleased();
        // }
    }

    protected void mouseReleased() {

    }

    protected void mouseClick(MouseEvent evt) {
        mouseButton = evt.getButton();
        PComponent.mouseButton = evt.getButton();

        mouseClicked();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).mouseClicked();
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().mouseClicked();
        // }
    }

    protected void mouseClicked() {

    }

    private void updateMouse(MouseEvent evt) {
        mouseX = evt.getX();
        mouseY = evt.getY();
        mouseX /= universalScale;
        mouseY /= universalScale;
        // if (!fullScreen) {
        // mouseX -= 8 / universalScale;
        // mouseY -= 31 / universalScale;
        // }
        mouse = new PVector(mouseX, mouseY);

        PComponent.mouseX = mouseX;
        PComponent.mouseY = mouseY;
        PComponent.mouse = mouse;
    }

    protected void mouseMove(MouseEvent evt) {
        updateMouse(evt);

        mouseMoved();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).mouseMoved();
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().mouseMoved();
        // }
    }

    protected void mouseMoved() {

    }

    protected void mouseDrag(MouseEvent evt) {
        updateMouse(evt);

        mouseDragged();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).mouseDragged();
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().mouseDragged();
        // }
    }

    protected void mouseDragged() {

    }

    protected void mouseEnter(MouseEvent evt) {
        updateMouse(evt);

        mouseEntered();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).mouseEntered();
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().mouseEntered();
        // }
    }

    protected void mouseEntered() {

    }

    protected void mouseExit(MouseEvent evt) {
        updateMouse(evt);

        mouseExited();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).mouseExited();
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().mouseExited();
        // }
    }

    protected void mouseExited() {

    }

    // TODO makes this mouseWheelMoved()
    protected void mouseScroll(int amount) {
        mouseScrolled(amount);
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).mouseScrolled(amount);
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().mouseScrolled(amount);
        // }
    }

    protected void mouseScrolled(int amount) {

    }

    // Keyboard inputs
    protected void keyPress(KeyEvent evt) {
        keyCode = evt.getKeyCode();
        keyString = KeyEvent.getKeyText(evt.getKeyCode());
        key = evt.getKeyChar();

        keysPressed.add(keyString);

        keyPressed = true;

        PComponent.keyCode = keyCode;
        PComponent.key = key;
        PComponent.keyString = keyString;
        PComponent.keyPressed = true;

        if (keyCode == KeyEvent.VK_ESCAPE && exitOnEscape) {
            exit();
        }

        keyPressed();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).keyPressed();
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().keyPressed();
        // }
    }

    protected void keyPressed() {

    }

    protected void keyRelease(KeyEvent evt) {
        keyCode = evt.getKeyCode();
        keyString = KeyEvent.getKeyText(evt.getKeyCode());
        key = evt.getKeyChar();

        keysPressed.remove(keyString);

        keyPressed = false;

        PComponent.keyCode = keyCode;
        PComponent.key = key;
        PComponent.keyString = keyString;
        PComponent.keyPressed = true;

        keyReleased();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).keyReleased();
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().keyReleased();
        // }
    }

    protected void keyReleased() {

    }

    protected void keyType(KeyEvent evt) {
        keyTyped();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).keyTyped();
        }
        // Iterator<PComponent> itr = PComponent.components.iterator();
        // while (itr.hasNext()) {
        // itr.next().keyTyped();
        // }
    }

    /**
     * Warning: This method is not called when the user presses any modifier (e.g.
     * shift, ctrl, alt, backspace).
     * 
     * @param key
     */
    protected void keyTyped() {

    }

    protected void windowOpened() {

    }

    protected void windowClosing() {

    }

    protected void windowClosed() {

    }

    protected void windowIconified() {

    }

    protected void windowDeiconified() {

    }

    protected void windowActivated() {

    }

    protected void windowDeactivated() {

    }

    protected void windowResize() {
        if (!fullScreen) {
            width = frame.getWidth() - (int) frameImageSizeDifference.x;
            height = frame.getHeight() - (int) frameImageSizeDifference.y;
            PComponent.width = width;
            PComponent.height = height;
            setPreferredSize(new Dimension(width, height));

            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2dNew = newImage.createGraphics();
            g2dNew.scale(universalScale, universalScale);
            g2dNew.drawImage(img, 0, 0, null);
            g2dNew.dispose();

            img = newImage;
            g2d = img.createGraphics();
            g2d.scale(universalScale, universalScale);
            pixels = new color[width * height];
            smooth();
        }

        windowResized();
        for (int i = 0; i < PComponent.components.size(); i++) {
            PComponent.components.get(i).windowResized();
        }
    }

    protected void windowResized() {

    }

    public void simulateKeyPress(char key) {
        KeyEvent event = new KeyEvent(this, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                KeyEvent.getExtendedKeyCodeForChar(key), key);
        keyPress(event);
    }

    public void simulateEvent(AWTEvent event) {
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            handleMouseEvent(mouseEvent);
        } else if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            handleKeyEvent(keyEvent);
        } else if (event instanceof WindowEvent) {
            WindowEvent windowEvent = (WindowEvent) event;
            handleWindowEvent(windowEvent);
        } else if (event instanceof ComponentEvent) {
            ComponentEvent componentEvent = (ComponentEvent) event;
            handleComponentEvent(componentEvent);
        }
    }

    public void setup() {
        // Empty
    }

    public void draw() {
        exit(); // If the user doesn't override draw(), exit the program
    }

    public void noSmooth() {
        smooth(0);
    }

    public void smooth() {
        smooth(3);
    }

    // Stolen from Processing. Line:
    // https://github.com/processing/processing4/blob/587361dd4056c3e4f05b632c00c82747aa4e771d/core/src/processing/awt/PGraphicsJava2D.java#L394
    public void smooth(int smooth) {
        if (smooth == 0) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (smooth == 1 || smooth == 3) { // default is bicubic
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            } else if (smooth == 2) {
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }

            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        }
    }

    public void minimize() {
        frame.setExtendedState(Frame.ICONIFIED);
    }

    public void unMinimize() {
        frame.setExtendedState(Frame.NORMAL);
    }

    public boolean isMinimized() {
        return frame.getState() == Frame.ICONIFIED;
    }

    // PComponent garbage collection
    public void delete(PComponent component) {
        PComponent.delete(component);
    }

    // Loop and redrawing
    public void loop() {
        shouldLoop = true;
    }

    public void noLoop() {
        shouldLoop = false;
    }

    public void redraw() {
        shouldRedraw = true;
    }

    public boolean isLooping() {
        return shouldLoop;
    }

    public void backendUpdate() {

    }

    public void exitOnEscape(boolean exitOnEscape) {
        this.exitOnEscape = exitOnEscape;
    }

    public void frameRate(double targetFrameRate) {
        this.targetFrameRate = (int) constrain(targetFrameRate, 0, 1000);
    }

    public void displayFrameRate() {
        displayFrameRate = true;
    }

    private void renderFrameRate() {
        String fps = "FPS: " + Helper.roundString((float) frameRate, 0);

        PVector previousTranslation = translation.copy();
        float previousRotation = rotation;
        float previousScale = scale;
        push();
        translation = PVector.zero();
        rotation = 0;
        scale = 1;

        noStroke();

        rectMode(CENTER);
        fill(0, 0, 0, 100);
        rect(width - 50, 20, 100, 40);

        fill(255);
        textAlign(CENTER);
        textSize(20);
        textFont("Arial");
        text(fps, width - 50, 20);
        pop();

        translation = previousTranslation.copy();
        rotation = previousRotation;
        scale = previousScale;
    }

    public void background(color color) {
        g2d.setPaint(color.toColor());
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
    }

    public void background(color color, double a) {
        background(color.r, color.g, color.b, a);
    }

    public void background(double r, double g, double b, double a) {
        background(color(r, g, b, a));
    }

    public void background(double r, double g, double b) {
        background(r, g, b, 255);
    }

    public void background(double gray, double a) {
        background(gray, gray, gray, a);
    }

    public void background(double gray) {
        background(gray, 255);
    }

    // Drawing Modes
    public void rectMode(int mode) {
        rectMode = mode;
    }

    public void ellipseMode(int mode) {
        ellipseMode = mode;
    }

    // Pixels
    public void loadPixels() {
        if (!hasDrawnSinceLoadPixels) { // Haven't drawn anything new
            return;
        }

        // Pixels array with size width * height:
        BufferedImage readImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        if (universalScale != 1) {
            Graphics2D g2d = readImage.createGraphics();
            g2d.scale(universalScale, universalScale);
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();
        } else {
            readImage = img;
        }

        WritableRaster raster = readImage.getRaster();
        int[] pixelsInt = new int[width * height];
        raster.getDataElements(0, 0, width, height, pixelsInt);
        for (int i = 0; i < pixelsInt.length; i++) {
            pixels[i] = color.fromInt(pixelsInt[i]);
        }

        // Pixels array with size displayWidth * displayHeight:
        // WritableRaster raster = img.getRaster();
        // int[] pixelsInt = fullScreen ? new int[displayWidth * displayHeight] : new
        // int[width * height];
        // raster.getDataElements(0, 0, fullScreen ? displayWidth : width, fullScreen ?
        // displayHeight : height, pixelsInt);
        // for (int i = 0; i < pixelsInt.length; i++) {
        // pixels[i] = color.fromInt(pixelsInt[i]);
        // }

        hasDrawnSinceLoadPixels = false;
    }

    public void updatePixels() {
        // Pixels array with size width * height:
        int[] pixelsInt = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            pixelsInt[i] = pixels[i].getRGB();
        }

        // img.getRaster().setDataElements(0, 0, width, height, pixelsInt); // Much
        // faster
        if (universalScale != 1) {
            BufferedImage writeImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            writeImage.getRaster().setDataElements(0, 0, width, height, pixelsInt);
            Graphics2D g2d = img.createGraphics();
            g2d.scale(universalScale, universalScale);
            g2d.drawImage(writeImage, 0, 0, null);
            g2d.dispose();
        } else {
            img.getRaster().setDataElements(0, 0, width, height, pixelsInt);
        }

        // Pixels array with size displayWidth * displayHeight:
        // int[] pixelsInt = new int[pixels.length];
        // for (int i = 0; i < pixels.length; i++) {
        // pixelsInt[i] = pixels[i].getRGB();
        // }

        // if (fullScreen) {
        // img.getRaster().setDataElements(0, 0, displayWidth, displayHeight,
        // pixelsInt);
        // } else {
        // img.getRaster().setDataElements(0, 0, width, height, pixelsInt);
        // }
    }

    public color get(PVector pos) {
        return get(pos.x, pos.y);
    }

    public color get(int x, int y) {
        // if (!fullScreen) {
        // if (x < 0 || x >= width || y < 0 || y >= height) {
        // return new color(0);
        // }
        // } else {
        // if (x < 0 || x >= displayWidth || y < 0 || y >= displayHeight) {
        // return new color(0);
        // }
        // }

        // return color.fromInt(img.getRGB(x, y));

        if (x < 0 || x >= width || y < 0 || y >= height) {
            return new color(0);
        }

        if (fullScreen) {
            x = (int) map(x, 0, width, 0, displayWidth);
            y = (int) map(y, 0, height, 0, displayHeight);
        }

        return color.fromInt(img.getRGB(x, y));
    }

    public color get(double x, double y) {
        return get((int) x, (int) y);
    }

    public void set(PVector pos, color color) {
        set(pos.x, pos.y, color);
    }

    public void set(PVector pos, int c) {
        set(pos.x, pos.y, c);
    }

    public void set(int x, int y, color color) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        if (fullScreen) {
            x = (int) map(x, 0, width, 0, displayWidth);
            y = (int) map(y, 0, height, 0, displayHeight);
        }

        if (!fullScreen)
            img.setRGB(x, y, color.getRGB());
        else
            // setRGB region to set at x and x + 1, y and y + 1
            // img.setRGB(x, y, 2, 2, new int[] { color.getRGB(), color.getRGB(),
            // color.getRGB(), color.getRGB() }, 0, 2);
            for (int i = 0; i <= 1; i++) {
                for (int j = 0; j <= 1; j++) {
                    img.setRGB(x + i, y + j, color.getRGB());
                }
            }
    }

    public void set(double x, double y, color color) {
        set((int) x, (int) y, color);
    }

    public void set(int x, int y, int c) {
        set(x, y, color.fromInt(c));
    }

    public void set(double x, double y, int c) {
        set(x, y, color.fromInt(c));
    }

    // Push/Pop
    public void push() {
        DrawSettings drawSettings = new DrawSettings(rectMode, ellipseMode, textAlign, fillColor, strokeColor,
                strokeWeight, textSize, textFont, rotation, translation, scale);
        drawSettingsStack.push(drawSettings);
    }

    public void pop() {
        DrawSettings drawSettings = drawSettingsStack.pop();

        rectMode = drawSettings.rectMode;
        ellipseMode = drawSettings.ellipseMode;
        textAlign = drawSettings.textAlign;

        fillColor = drawSettings.fillColor.copy();
        strokeColor = drawSettings.strokeColor.copy();

        strokeWeight = drawSettings.strokeWeight;
        textSize = drawSettings.textSize;
        textFont = drawSettings.textFont;

        rotation = drawSettings.rotation;
        translation = drawSettings.translation.copy();
        scale = drawSettings.scale;
    }

    // Fill
    public void fill(color color, double a) {
        fillColor = color(color, a);
    }

    public void fill(color color) {
        fillColor = color;
    }

    public void fill(double r, double g, double b, double a) {
        fill(color(r, g, b, a));
    }

    public void fill(double r, double g, double b) {
        fill(r, g, b, 255);
    }

    public void fill(double gray, double a) {
        fill(gray, gray, gray, a);
    }

    public void fill(double gray) {
        fill(gray, gray, gray);
    }

    public void noFill() {
        fill(0, 0);
    }

    // Stroke
    public void stroke(color c, double a) {
        strokeColor = color(c, a);
    }

    public void stroke(color c) {
        strokeColor = c;
    }

    public void stroke(double r, double g, double b, double a) {
        stroke(color(r, g, b, a));
    }

    public void stroke(double r, double g, double b) {
        stroke(r, g, b, 255);
    }

    public void stroke(double gray, double a) {
        stroke(gray, gray, gray, a);
    }

    public void stroke(double gray) {
        stroke(gray, gray, gray);
    }

    public void noStroke() {
        stroke(0, 0);
    }

    // Stroke Weight
    public void strokeWeight(double weight) {
        weight *= uiScale;
        strokeWeight = MathHelper.constrain(weight, 0, weight);
    }

    // Text Size
    public void textSize(double size) {
        size *= uiScale;
        textSize = MathHelper.constrain(size, 0, size);
    }

    // Text Align
    public void textAlign(TextAlignment align) {
        textAlign = align.toInt();
    }

    public void textAlign(int align) {
        textAlign = align;
    }

    // Text Font
    public void textFont(String font) {
        textFont = font;
    }

    // Transformations
    public void translate(double x, double y) {
        translation.add(x, y);
    }

    public void translate(PVector p) {
        translation.add(p.copy());
    }

    public PVector getTranslation() {
        return translation.copy();
    }

    public void resetTranslation() {
        translation = PVector.zero();
    }

    public void rotate(double angle) {
        rotation += angle;
    }

    public void scale(double scalar) {
        scale += scalar;
    }

    // Returns the width of the text
    public float textWidth(String text) {
        oldTransform = g2d.getTransform();

        setFont();
        double wid = g2d.getFontMetrics().getStringBounds(text, g2d).getWidth();

        g2d.setTransform(oldTransform);
        return (float) wid;
    }

    // Returns the height of the text
    public float textHeight(String text) {
        // int hei = frame.getGraphics().getFontMetrics().getHeight();

        oldTransform = g2d.getTransform();

        setFont();
        double hei = g2d.getFontMetrics().getStringBounds(text, g2d).getHeight();

        g2d.setTransform(oldTransform);
        return (float) hei;
    }

    // Returns the ascent of the text
    public float textAscent() {
        oldTransform = g2d.getTransform();

        setFont();
        double asc = g2d.getFontMetrics().getAscent();

        g2d.setTransform(oldTransform);
        return (float) asc;
    }

    // Returns the descent of the text
    public float textDescent() {
        oldTransform = g2d.getTransform();

        setFont();
        double des = g2d.getFontMetrics().getDescent();

        g2d.setTransform(oldTransform);
        return (float) des;
    }

    private void drawGenericStart() {
        oldTransform = g2d.getTransform();

        g2d.scale(scale, scale);
        g2d.translate(translation.x, translation.y);
        g2d.rotate(rotation);

        hasDrawnSinceLoadPixels = true;
    }

    private void drawGenericShape(Shape shape) {
        drawGenericStart();

        // Fill
        if (!fillColor.equals(color(0, 0))) {
            g2d.setColor(fillColor.toColor());
            g2d.fill(shape);
        }

        // Stroke
        if (!strokeColor.equals(color(0, 0))) {
            g2d.setColor(strokeColor.toColor());
            g2d.setStroke(new BasicStroke((float) strokeWeight));
            g2d.draw(shape);
        }

        g2d.setTransform(oldTransform);
    }

    // Ellipse
    private void drawEllipse(double x, double y, double w, double h) {
        if (ellipseMode == CENTER) {
            x -= w / 2;
            y -= h / 2;
        }

        drawGenericShape(new Ellipse2D.Double(x, y, w, h));

    }

    public void ellipse(double x, double y, double w, double h) {
        drawEllipse(x, y, w, h);
    }

    public void ellipse(PVector p, double w, double h) {
        ellipse(p.x, p.y, w, h);
    }

    public void ellipse(PVector p, PVector s) {
        ellipse(p.x, p.y, s.x, s.y);
    }

    // Circle
    public void circle(double x, double y, double r) {
        ellipse(x, y, r, r);
    }

    public void circle(PVector p, double r) {
        ellipse(p.x, p.y, r, r);
    }

    // Line
    private void drawLine(double x1, double y1, double x2, double y2) {
        drawGenericStart();

        if (!strokeColor.equals(color(0, 0))) {
            g2d.setColor(strokeColor.toColor());
            g2d.setStroke(new BasicStroke(strokeWeight, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            g2d.draw(new Line2D.Double(x1, y1, x2, y2));
        }

        g2d.setTransform(oldTransform);
    }

    public void line(double x1, double y1, double x2, double y2) {
        drawLine(x1, y1, x2, y2);
    }

    public void line(PVector p1, PVector p2) {
        line(p1.x, p1.y, p2.x, p2.y);
    }

    // PVector
    public void point(double x, double y) {
        int previousEllipseMode = ellipseMode;
        ellipseMode = CENTER;

        circle(x, y, strokeWeight);

        ellipseMode = previousEllipseMode;
    }

    public void point(PVector p) {
        point(p.x, p.y);
    }

    // Quad
    private void drawQuad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        drawGenericShape(new Polygon(new int[] { (int) x1, (int) x2, (int) x3, (int) x4 },
                new int[] { (int) y1, (int) y2, (int) y3, (int) y4 }, 4));

    }

    public void quad(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        drawQuad(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public void quad(PVector p1, PVector p2, PVector p3, PVector p4) {
        quad(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y);
    }

    // Rect
    private void drawRect(double x, double y, double w, double h, double r) {
        if (rectMode == CENTER) {
            x -= w / 2;
            y -= h / 2;
        }

        if (r == 0) {
            drawGenericShape(new Rectangle2D.Double(x, y, w, h));
        } else {
            drawGenericShape(new RoundRectangle2D.Double(x, y, w, h, r, r));
        }
    }

    public void rect(double x, double y, double w, double h, double r) {
        drawRect(x, y, w, h, r);
    }

    public void rect(double x, double y, double w, double h) {
        rect(x, y, w, h, 0);
    }

    public void rect(PVector p, double w, double h, double r) {
        rect(p.x, p.y, w, h, r);
    }

    public void rect(PVector p, double w, double h) {
        rect(p.x, p.y, w, h);
    }

    public void rect(PVector p, PVector s, double r) {
        rect(p.x, p.y, s.x, s.y, r);
    }

    public void rect(PVector p, PVector s) {
        rect(p.x, p.y, s.x, s.y);
    }

    // Square
    public void square(double x, double y, double s) {
        rect(x, y, s, s);
    }

    public void square(PVector p, double s) {
        square(p.x, p.y, s);
    }

    // Triangle
    private void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        drawGenericShape(new Polygon(new int[] { (int) x1, (int) x2, (int) x3 },
                new int[] { (int) y1, (int) y2, (int) y3 }, 3));
    }

    public void triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        drawTriangle(x1, y1, x2, y2, x3, y3);
    }

    public void triangle(PVector p1, PVector p2, PVector p3) {
        triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
    }

    private void setFont() {
        File fontFile = new File(textFont);
        Font font = new Font(textFont, Font.PLAIN, (int) textSize);
        if (fontFile.exists()) {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
        }
        g2d.setFont(font.deriveFont(textSize));
    }

    // Text
    private void drawText(String text, double x, double y) {
        if (text.equals("") || fillColor.equals(color(0, 0)))
            return;

        drawGenericStart();

        setFont();

        int w = g2d.getFontMetrics().stringWidth(text);
        int h = g2d.getFontMetrics().getHeight();

        if (textAlign == CENTER) {
            x -= w / 2;
        } else if (textAlign == RIGHT) {
            x -= w;
        }
        y += h / 4;

        g2d.setColor(fillColor.toColor());
        g2d.drawString(text, (int) x, (int) y);

        g2d.setTransform(oldTransform);

    }

    public void text(String text, double x, double y) {
        drawText(text, x, y);
    }

    public void text(Object text, double x, double y) {
        text(str(text), x, y);
    }

    public void text(String text, PVector p) {
        text(text, p.x, p.y);
    }

    public void text(Object text, PVector p) {
        text(str(text), p.x, p.y);
    }

    // Shape
    private void drawShape() {
        if (points.size() < 2) {
            return;
        }

        drawGenericStart();

        if (shapeMode == RIGID) {
            int[] xPVectors = new int[points.size()];
            int[] yPVectors = new int[points.size()];

            g2d.setColor(strokeColor.toColor());
            g2d.setStroke(new BasicStroke(strokeWeight, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            for (int i = 0; i < points.size() - 1; i++) {
                PVector p1 = points.get(i);
                PVector p2 = points.get(i + 1);
                if (strokeColor != color(0, 0))
                    g2d.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);

                xPVectors[i] = (int) p1.x;
                yPVectors[i] = (int) p1.y;
            }
            xPVectors[points.size() - 1] = (int) points.get(points.size()
                    - 1).x;
            yPVectors[points.size() - 1] = (int) points.get(points.size()
                    - 1).y;

            // Draw the filled in shape
            if (fillColor != color(0, 0)) {
                g2d.setColor(fillColor.toColor());
                g2d.fillPolygon(xPVectors, yPVectors, points.size());
            }
        } else if (shapeMode == SMOOTH) {
            // Catmull-Rom splines smoothing algorithm

            ArrayList<Integer> xPVectors = new ArrayList<Integer>();
            ArrayList<Integer> yPVectors = new ArrayList<Integer>();

            for (int i = 0; i < points.size() - 3; i++) {
                PVector p1 = points.get(i);
                PVector p2 = points.get(i + 1);
                PVector p3 = points.get(i + 2);
                PVector p4 = points.get(i + 3);

                double x1 = p1.x;
                double y1 = p1.y;
                double x2 = p2.x;
                double y2 = p2.y;
                double x3 = p3.x;
                double y3 = p3.y;
                double x4 = p4.x;
                double y4 = p4.y;

                double t = 0;
                while (t <= 1) {
                    double x = 0.5 * ((2 * x2) + (-x1 + x3) * t + (2 * x1 - 5 * x2 + 4 * x3 - x4)
                            * t * t
                            + (-x1 + 3 * x2 - 3 * x3 + x4) * t * t * t);
                    double y = 0.5 * ((2 * y2) + (-y1 + y3) * t + (2 * y1 - 5 * y2 + 4 * y3 - y4)
                            * t * t
                            + (-y1 + 3 * y2 - 3 * y3 + y4) * t * t * t);
                    int xInt = (int) x;
                    int yInt = (int) y;
                    xPVectors.add(xInt);
                    yPVectors.add(yInt);
                    t += 0.01;
                }
            }

            int[] xPVectorsArray = Helper.toIntArray(xPVectors);
            int[] yPVectorsArray = Helper.toIntArray(yPVectors);

            // Draw the filled in shape
            if (fillColor != color(0, 0)) {
                g2d.setColor(fillColor.toColor());
                g2d.fillPolygon(xPVectorsArray, yPVectorsArray, xPVectorsArray.length);
            }

            // Draw lines
            if (!strokeColor.equals(color(0, 0))) {
                g2d.setColor(strokeColor.toColor());
                g2d.setStroke(new BasicStroke(strokeWeight));
                g2d.drawPolyline(xPVectorsArray, yPVectorsArray, xPVectorsArray.length);
            }

        }
        g2d.setTransform(oldTransform);
    }

    public void beginShape() {
        points = new ArrayList<PVector>();
    }

    public void beginShape(int mode) {
        shapeMode = mode;
        beginShape();
    }

    public void endShape() {
        drawShape();
        shapeMode = RIGID;
    }

    public void endShape(int endBehavior) {
        if (endBehavior == CLOSE && points.size() > 0) {
            points.add(points.get(0));
        }
        endShape();
    }

    public void vertex(double x, double y) {
        points.add(new PVector(x, y));
    }

    public void vertex(PVector p) {
        vertex(p.x, p.y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    public void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onExit() {
    }

    public void exit() {
        onExit();
        for (int i = 0; i < PComponent.components.size(); i++)
            PComponent.components.get(i).onExit();

        if (frame != null)
            frame.dispose();
        System.exit(0);
    }

    // Noise
    public void noiseSeed(long seed) {
        NoiseHelper.noiseSeed = seed;
    }

    /**
     * Sets the noise function to be used. The default is simplex noise. <br>
     * <br>
     * All noise functions are: {@code Simplex} {@code Voronoi} {@code Worley} and
     * {@code Ridged}.
     * 
     * @param function
     */
    public void noiseMode(int function) {
        NoiseHelper.noiseFunction = function;
    }

    /**
     * Sets the noise function and type. <br>
     * <br>
     * All noise types are: {@code STANDARD} and {@code FRACTAL}.
     * 
     * @param function
     * @param type
     */
    public void noiseMode(int function, int type) {

        NoiseHelper.noiseFunction = function;
        NoiseHelper.noiseType = type;
    }

    /**
     * If the noise type is set to {@code FRACTAL}, then this function sets the
     * octaves, lacunarity, and persistence.
     * 
     * @param octaves     How many layers of noise to add together.
     * @param lacunarity  How quickly the frequency increases for each octave.
     * @param persistence How quickly the amplitude decreases for each octave.
     */
    public void noiseDetail(int octaves, double lacunarity, double persistence) {
        NoiseHelper.noiseOctaves = octaves;
        NoiseHelper.noiseLacunarity = (float) lacunarity;
        NoiseHelper.noisePersistence = (float) persistence;
    }

    // ------------------------------------------------------------
    // ------------------------------------------------------------
    // ------------------------------------------------------------
    // PROCESSING FUNCTIONS

    public void println(Object... args) {
        Helper.println(args);
    }

    public void print(Object... args) {
        Helper.print(args);
    }

    public void printArray(Object[] array) {
        Helper.printArray(array);
    }

    public void printArray(String[] array) {
        Helper.printArray(array);
    }

    public void printArray(int[] array) {
        Helper.printArray(array);
    }

    public void printArray(float[] array) {
        Helper.printArray(array);
    }

    public void printArray(long[] array) {
        Helper.printArray(array);
    }

    public void printArray(double[] array) {
        Helper.printArray(array);
    }

    public void printArray(char[] array) {
        Helper.printArray(array);
    }

    public String input(String message) {
        return Helper.input(message);
    }

    public int inputInt(String message) {
        return Helper.inputInt(message);
    }

    public int inputInt(String message, boolean repeat) {
        return Helper.inputInt(message, repeat);
    }

    public boolean inputYesNo(String message) {
        return Helper.inputYesNo(message);
    }

    public boolean inputYesNo(String message, boolean repeat) {
        return Helper.inputYesNo(message, repeat);
    }

    public boolean inputBoolean(String message) {
        return Helper.inputBoolean(message);
    }

    public boolean inputBoolean(String message, boolean repeat) {
        return Helper.inputBoolean(message, repeat);
    }

    /**
     * Prints a given message + " (option 1, option 2, etc.): " and returns whatever
     * option the user enters. Throws an error if user types anything else.
     * 
     * @param message
     * @param options which options the user can choose from (any number of options)
     * 
     * @return String the option the user chose
     */
    public static String inputOption(String message, String... options) {
        return Helper.inputOption(message, options);
    }

    /**
     * Prints a given message + " (option 1, option 2, etc.): " and returns whatever
     * option the user enters. If set to repeat, it will continuously ask the user
     * for an integer until they give one.
     * 
     * @param message
     * @param options which options the user can choose from (any number of options)
     * 
     * @return String the option the user chose
     */
    public static String inputOption(String message, boolean repeat, String... options) {
        return Helper.inputOption(message, repeat, options);
    }

    public String[] loadStrings(String fileName) {
        return Helper.loadStrings(fileName);
    }

    /**
     * Opens a file with the default program. This can either be a directory (in
     * which case it will open the file explorer) or a file (in which case it will
     * open the default program for that file type
     * 
     * @param fileName
     */
    public void openFile(String fileName) {
        Helper.openFile(fileName);
    }

    public void openInBrowser(String url) {
        Helper.openInBrowser(url);
    }

    public boolean isValidURL(String url) {
        return Helper.isValidURL(url);
    }

    public String getClipboardContents() {
        return Helper.getClipboardContents();
    }

    public void copyToClipboard(String text) {
        Helper.copyToClipboard(text);
    }

    public LinkedHashMap<String, String> loadProperties(String filename) {
        String[] lines = loadStrings(filename);
        LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        for (String line : lines) {
            if (line.length() == 0 || line.startsWith("#") || !line.contains("="))
                continue;

            String[] split = line.split("=");
            int lastHash = split[1].lastIndexOf("#");
            if (lastHash != -1) {
                split[1] = split[1].substring(0, lastHash);
            }
            split[0] = split[0].trim();
            split[1] = split[1].trim();

            properties.put(split[0], split[1]);
        }

        return properties;
    }

    public void saveProperties(LinkedHashMap<String, String> properties, String filename) {
        ArrayList<String> lines = new ArrayList<>();
        for (String key : properties.keySet()) {
            lines.add(key + "=" + properties.get(key));
        }

        saveStrings(lines, filename);
    }

    public void append(byte[] array, byte value) {
        byte[] newArray = new byte[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    public void append(char[] array, char value) {
        char[] newArray = new char[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    // Array functions
    public void append(int[] array, int value) {
        int[] newArray = new int[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    public void append(float[] array, float value) {
        float[] newArray = new float[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    public void append(String[] array, String value) {
        String[] newArray = new String[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    public void append(Object[] array, Object value) {
        Object[] newArray = new Object[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    public void arrayCopy(Object src, int srcPos, Object dest, int destPos,
            int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    public void arrayCopy(Object src, Object dest, int length) {
        System.arraycopy(src, 0, dest, 0, length);
    }

    public void arrayCopy(Object src, Object dest) {
        System.arraycopy(src, 0, dest, 0, Array.getLength(src));
    }

    public boolean[] concat(boolean[] a, boolean[] b) {
        boolean[] c = new boolean[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public char[] concat(char[] a, char[] b) {
        char[] c = new char[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public int[] concat(int[] a, int[] b) {
        int[] c = new int[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public float[] concat(float[] a, float[] b) {
        float[] c = new float[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public Object[] concat(Object[] a, Object[] b) {
        Object[] c = new Object[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public boolean[] expand(boolean[] list) {
        boolean temp[] = new boolean[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public boolean[] expand(boolean[] list, int newSize) {
        boolean temp[] = new boolean[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public byte[] expand(byte[] list) {
        byte temp[] = new byte[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public byte[] expand(byte[] list, int newSize) {
        byte temp[] = new byte[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public char[] expand(char[] list) {
        char temp[] = new char[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public char[] expand(char[] list, int newSize) {
        char temp[] = new char[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public int[] expand(int[] list) {
        int temp[] = new int[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public int[] expand(int[] list, int newSize) {
        int temp[] = new int[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public long[] expand(long[] list) {
        long temp[] = new long[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public long[] expand(long[] list, int newSize) {
        long temp[] = new long[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public float[] expand(float[] list) {
        float temp[] = new float[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public float[] expand(float[] list, int newSize) {
        float temp[] = new float[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public double[] expand(double[] list) {
        double temp[] = new double[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public double[] expand(double[] list, int newSize) {
        double temp[] = new double[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public String[] expand(String[] list) {
        String temp[] = new String[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public String[] expand(String[] list, int newSize) {
        String temp[] = new String[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public Object[] expand(Object[] list) {
        Object temp[] = new Object[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public Object[] expand(Object[] list, int newSize) {
        Object temp[] = new Object[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public boolean[] shorten(boolean[] list) {
        boolean outgoing[] = new boolean[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public byte[] shorten(byte[] list) {
        byte outgoing[] = new byte[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public char[] shorten(char[] list) {
        char outgoing[] = new char[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public int[] shorten(int[] list) {
        int outgoing[] = new int[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public long[] shorten(long[] list) {
        long outgoing[] = new long[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public float[] shorten(float[] list) {
        float outgoing[] = new float[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public double[] shorten(double[] list) {
        double outgoing[] = new double[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public String[] shorten(String[] list) {
        String outgoing[] = new String[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public Object[] shorten(Object[] list) {
        Object outgoing[] = new Object[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public boolean[] splice(boolean[] list, boolean value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public byte[] splice(byte[] list, byte value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public char[] splice(char[] list, char value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public int[] splice(int[] list, int value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public long[] splice(long[] list, long value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public float[] splice(float[] list, float value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public double[] splice(double[] list, double value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public String[] splice(String[] list, String value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public Object[] splice(Object[] list, Object value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public boolean[] subset(boolean[] list, int start) {
        return subset(list, start, list.length);
    }

    public byte[] subset(byte[] list, int start) {
        return subset(list, start, list.length);
    }

    public char[] subset(char[] list, int start) {
        return subset(list, start, list.length);
    }

    public int[] subset(int[] list, int start) {
        return subset(list, start, list.length);
    }

    public long[] subset(long[] list, int start) {
        return subset(list, start, list.length);
    }

    public float[] subset(float[] list, int start) {
        return subset(list, start, list.length);
    }

    public double[] subset(double[] list, int start) {
        return subset(list, start, list.length);
    }

    public String[] subset(String[] list, int start) {
        return subset(list, start, list.length);
    }

    public Object[] subset(Object[] list, int start) {
        return subset(list, start, list.length);
    }

    public boolean[] subset(boolean[] list, int start, int count) {
        boolean outgoing[] = new boolean[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public byte[] subset(byte[] list, int start, int count) {
        byte outgoing[] = new byte[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public char[] subset(char[] list, int start, int count) {
        char outgoing[] = new char[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public int[] subset(int[] list, int start, int count) {
        int outgoing[] = new int[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public long[] subset(long[] list, int start, int count) {
        long outgoing[] = new long[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public float[] subset(float[] list, int start, int count) {
        float outgoing[] = new float[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public double[] subset(double[] list, int start, int count) {
        double outgoing[] = new double[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public String[] subset(String[] list, int start, int count) {
        String outgoing[] = new String[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public Object[] subset(Object[] list, int start, int count) {
        Object outgoing[] = new Object[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public boolean[] reverse(boolean[] list) {
        boolean outgoing[] = new boolean[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public byte[] reverse(byte[] list) {
        byte outgoing[] = new byte[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public char[] reverse(char[] list) {
        char outgoing[] = new char[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public int[] reverse(int[] list) {
        int outgoing[] = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public long[] reverse(long[] list) {
        long outgoing[] = new long[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public float[] reverse(float[] list) {
        float outgoing[] = new float[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public double[] reverse(double[] list) {
        double outgoing[] = new double[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public String[] reverse(String[] list) {
        String outgoing[] = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public Object[] reverse(Object[] list) {
        Object outgoing[] = new Object[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public String reverse(String what) {
        StringBuilder sb = new StringBuilder(what);
        return sb.reverse().toString();
    }

    public byte[] sort(byte[] list) {
        byte outgoing[] = new byte[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public char[] sort(char[] list) {
        char outgoing[] = new char[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public int[] sort(int[] list) {
        int outgoing[] = new int[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public long[] sort(long[] list) {
        long outgoing[] = new long[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public float[] sort(float[] list) {
        float outgoing[] = new float[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public double[] sort(double[] list) {
        double outgoing[] = new double[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public String[] sort(String[] list) {
        String outgoing[] = new String[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public Object[] sort(Object[] list) {
        Object outgoing[] = new Object[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public int indexOf(byte[] list, byte item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(char[] list, char item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(int[] list, int item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(long[] list, long item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(float[] list, float item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(double[] list, double item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(String[] list, String item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(Object[] list, Object item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    // Conversion Functions
    public String binary(int n) {
        return Integer.toBinaryString(n);
    }

    public String binary(byte n) {
        return Integer.toBinaryString(n);
    }

    public String binary(char n) {
        return Integer.toBinaryString(n);
    }

    public String binary(color c) {
        return Integer.toBinaryString(c.getRGB());
    }

    public boolean bool(int n) {
        return n != 0;
    }

    public boolean bool(String s) {
        return s.equals("true");
    }

    public byte parseByte(boolean b) {
        return (byte) (b ? 1 : 0);
    }

    public byte parseByte(int n) {
        return (byte) n;
    }

    public byte parseByte(char n) {
        return (byte) n;
    }

    public byte parseByte(double n) {
        return (byte) n;
    }

    public byte parseByte(String s) {
        return Byte.parseByte(s);
    }

    public byte parseByte(Object o) {
        return Byte.parseByte(o.toString());
    }

    public char parseChar(int n) {
        return (char) n;
    }

    public char parseChar(byte n) {
        return (char) n;
    }

    public char parseChar(double n) {
        return (char) n;
    }

    public char parseChar(String s) {
        return s.charAt(0);
    }

    public char parseChar(Object o) {
        return o.toString().charAt(0);
    }

    public float parseFloat(boolean b) {
        return b ? 1 : 0;
    }

    public float parseFloat(byte n) {
        return n;
    }

    public float parseFloat(char n) {
        return n;
    }

    public float parseFloat(int n) {
        return n;
    }

    public float parseFloat(String s) {
        return Float.parseFloat(s);
    }

    public float parseFloat(Object o) {
        return Float.parseFloat(o.toString());
    }

    public double parseDouble(String s) {
        return Double.parseDouble(s);
    }

    public double parseDouble(Object o) {
        return Double.parseDouble(o.toString());
    }

    public double parseDouble(boolean b) {
        return b ? 1 : 0;
    }

    public double parseDouble(byte n) {
        return n;
    }

    public double parseDouble(char n) {
        return n;
    }

    public double parseDouble(int n) {
        return n;
    }

    public String hex(byte n) {
        return Integer.toHexString(n);
    }

    public String hex(char n) {
        return Integer.toHexString(n);
    }

    public String hex(int n) {
        return Integer.toHexString(n);
    }

    public String hex(color c) {
        return Integer.toHexString(c.getRGB());
    }

    public int parseInt(boolean b) {
        return b ? 1 : 0;
    }

    public int parseInt(byte n) {
        return n;
    }

    public int parseInt(char n) {
        return n;
    }

    public int parseInt(double n) {
        return (int) n;
    }

    public int parseInt(String s) {
        return Integer.parseInt(s);
    }

    public int parseInt(Object o) {
        return Integer.parseInt(o.toString());
    }

    public String str(boolean b) {
        return Boolean.toString(b);
    }

    public String str(byte n) {
        return Byte.toString(n);
    }

    public String str(char n) {
        return Character.toString(n);
    }

    public String str(int n) {
        return Integer.toString(n);
    }

    public String str(double n) {
        return Float.toString((float) n);
    }

    public String str(color c) {
        return c.toString();
    }

    public String str(Object o) {
        return o.toString();
    }

    public int unbinary(String s) {
        return Integer.parseInt(s, 2);
    }

    public int unhex(String s) {
        return Integer.parseInt(s, 16);
    }

    // String Functions
    public String join(String[] list, String separator) {
        String result = "";
        for (int i = 0; i < list.length; i++) {
            result += list[i];
            if (i < list.length - 1) {
                result += separator;
            }
        }
        return result;
    }

    // TODO match all/match

    // nf
    public String nf(int num) {
        return str(num);
    }

    public String nf(double num) {
        return str(num);
    }

    public String[] nf(int[] nums) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            outgoing[i] = str(nums[i]);
        }
        return outgoing;
    }

    public String[] nf(float[] nums) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            outgoing[i] = str(nums[i]);
        }
        return outgoing;
    }

    public String nf(int num, int digits) {
        return String.format("%0" + digits + "d", num);
    }

    public String nf(double num, int digits) {
        return String.format("%0" + digits + "f", num);
    }

    public String[] nf(int[] nums, int digits) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            outgoing[i] = String.format("%0" + digits + "d", nums[i]);
        }
        return outgoing;
    }

    public String[] nf(float[] nums, int digits) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            outgoing[i] = String.format("%0" + digits + "f", nums[i]);
        }
        return outgoing;
    }

    public String nf(int num, int left, int right) {
        String first = left > 0 ? String.format("%0" + left + "d", num) : str(num);
        String second = String.format("%0" + right + "d", (int) (num % 1 * Math.pow(10, right)));
        return first + "." + second;
    }

    public String nf(double num, int left, int right) {
        String first = left > 0 ? String.format("%0" + left + "d", (int) num) : str((int) num);
        String second = String.format("%0" + right + "d", (int) (num % 1 * Math.pow(10, right)));
        return first + "." + second;
    }

    public String[] nf(int[] nums, int left, int right) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            String first = left > 0 ? String.format("%0" + left + "d", nums[i]) : str(nums[i]);
            String second = String.format("%0" + right + "d", (int) (nums[i] % 1 * Math.pow(10, right)));
            outgoing[i] = first + "." + second;
        }
        return outgoing;
    }

    public String[] nf(float[] nums, int left, int right) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            String first = left > 0 ? String.format("%0" + left + "d", (int) nums[i]) : str((int) nums[i]);
            String second = String.format("%0" + right + "d", (int) (nums[i] % 1 * Math.pow(10, right)));
            outgoing[i] = first + "." + second;
        }
        return outgoing;
    }

    // nfc
    public String nfc(int num) {
        String output = str(num);

        int length = output.length() - 1;
        if (output.charAt(0) == '-') {
            length--;
        }
        int commas = length / 3;
        for (int i = 0; i < commas; i++) {
            output = output.substring(0, output.length() - (i + 1) * 3 - i) + ","
                    + output.substring(output.length() - (i + 1) * 3 - i);
        }
        return output;
    }

    public String nfc(double num) {
        String first = nfc((int) num);
        String second = str(num).substring(str(num).indexOf("."));
        return first + second;
    }

    public String[] nfc(int[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfc(num[i]);
        }
        return output;
    }

    public String[] nfc(float[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfc(num[i]);
        }
        return output;
    }

    public String nfc(double num, int digits) {
        String first = nfc((int) num);
        String second = nf(num, 0, digits);
        second = second.substring(second.indexOf("."));
        return first + second;
    }

    public String[] nfc(int[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfc(num[i], digits);
        }
        return output;
    }

    public String[] nfc(float[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfc(num[i], digits);
        }
        return output;
    }

    // nfp
    public String nfp(int num) {
        return num > 0 ? "+" + nf(num) : nf(num);
    }

    public String nfp(double num) {
        return num > 0 ? "+" + nf(num) : nf(num);
    }

    public String[] nfp(int[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i]);
        }
        return output;
    }

    public String[] nfp(float[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i]);
        }
        return output;
    }

    public String nfp(int num, int digits) {
        return num > 0 ? "+" + nf(num, digits) : nf(num, digits);
    }

    public String nfp(double num, int digits) {
        return num > 0 ? "+" + nf(num, digits) : nf(num, digits);
    }

    public String[] nfp(int[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i], digits);
        }
        return output;
    }

    public String[] nfp(float[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i], digits);
        }
        return output;
    }

    public String nfp(int num, int left, int right) {
        return num > 0 ? "+" + nf(num, left, right) : nf(num, left, right);
    }

    public String nfp(double num, int left, int right) {
        return num > 0 ? "+" + nf(num, left, right) : nf(num, left, right);
    }

    public String[] nfp(int[] num, int left, int right) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i], left, right);
        }
        return output;
    }

    public String[] nfp(float[] num, int left, int right) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i], left, right);
        }
        return output;
    }

    // nfs
    public String nfs(int num) {
        return num > 0 ? " " + nf(num) : nf(num);
    }

    public String nfs(double num) {
        return num > 0 ? " " + nf(num) : nf(num);
    }

    public String[] nfs(int[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i]);
        }
        return output;
    }

    public String[] nfs(float[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i]);
        }
        return output;
    }

    public String nfs(int num, int digits) {
        return num > 0 ? " " + nf(num, digits) : nf(num, digits);
    }

    public String nfs(double num, int digits) {
        return num > 0 ? " " + nf(num, digits) : nf(num, digits);
    }

    public String[] nfs(int[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i], digits);
        }
        return output;
    }

    public String[] nfs(float[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i], digits);
        }
        return output;
    }

    public String nfs(int num, int left, int right) {
        return num > 0 ? " " + nf(num, left, right) : nf(num, left, right);
    }

    public String nfs(double num, int left, int right) {
        return num > 0 ? " " + nf(num, left, right) : nf(num, left, right);
    }

    public String[] nfs(int[] num, int left, int right) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i], left, right);
        }
        return output;
    }

    public String[] nfs(float[] num, int left, int right) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i], left, right);
        }
        return output;
    }

    // TODO splitTokens()

    public String[] split(String str, char separator) {
        return str.split(Character.toString(separator));
    }

    public String[] split(String str, String separator) {
        return str.split(separator);
    }

    public String trim(String str) {
        return str.trim();
    }

    // Time and Date Functions
    public int day() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public int hour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public int minute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public int month() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public int second() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    public int year() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public int millis() {
        return (int) (System.currentTimeMillis() - startTime);
    }

    // Math Functions
    public int abs(int n) {
        return Math.abs(n);
    }

    public float abs(double n) {
        return MathHelper.abs(n);
    }

    public int ceil(double n) {
        return (int) Math.ceil(n);
    }

    public int floor(double n) {
        return (int) Math.floor(n);
    }

    public float constrain(double value, double min, double max) {
        return MathHelper.constrain(value, min, max);
    }

    public float constrain(int value, int min, int max) {
        return MathHelper.constrain(value, min, max);
    }

    public float dist(double x1, double y1, double x2, double y2) {
        return PVector.dist(x1, y1, x2, y2);
    }

    public float dist(PVector p1, PVector p2) {
        return PVector.dist(p1, p2);
    }

    public float distSq(double x1, double y1, double x2, double y2) {
        return PVector.distSq(x1, y1, x2, y2);
    }

    public float distSq(PVector p1, PVector p2) {
        return PVector.distSq(p1, p2);
    }

    public float exp(double n) {
        return (float) Math.exp(n);
    }

    public float lerp(double start, double stop, double amt) {
        return Lerp.lerp(start, stop, amt);
    }

    public float lerpSmooth(double start, double stop, double amt) {
        return Lerp.lerpSmooth(start, stop, amt);
    }

    public float lerpOvershoot(double start, double stop, double amt) {
        return Lerp.lerpOvershoot(start, stop, amt);
    }

    public float ln(double n) {
        return (float) Math.log(n);
    }

    public float log(double n) {
        return (float) Math.log10(n);
    }

    public float mag(double a, double b) {
        return (float) Math.sqrt(a * a + b * b);
    }

    public float mag(double a, double b, double c) {
        return (float) Math.sqrt(a * a + b * b + c * c);
    }

    public float map(double value, double min1, double max1, double min2, double max2) {
        return MathHelper.map(value, min1, max1, min2, max2);
    }

    public float max(double... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("max() requires at least one value");
        }

        double max = values[0];
        for (int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }

        return (float) max;
    }

    public int max(int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("max() requires at least one value");
        }

        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }

        return max;
    }

    public float min(double... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("min() requires at least one value");
        }

        double min = values[0];
        for (int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }

        return (float) min;
    }

    public int min(int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("min() requires at least one value");
        }

        int min = values[0];
        for (int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }

        return min;
    }

    public float norm(double value, double min, double max) {
        return MathHelper.norm(value, min, max);
    }

    public float pow(double n, double e) {
        return MathHelper.pow(n, e);
    }

    public int round(double n) {
        return (int) Math.round(n);
    }

    public float round(double n, int place) {
        return (float) (Math.round(n * Math.pow(10, place)) / Math.pow(10, place));
    }

    public float sq(double n) {
        return (float) (n * n);
    }

    public float sqrt(double n) {
        return MathHelper.sqrt(n);
    }

    // Trigonometry Functions
    public float acos(double n) {
        return MathHelper.acos(n);
    }

    public float asin(double n) {
        return MathHelper.asin(n);
    }

    public float atan(double n) {
        return MathHelper.atan(n);
    }

    public float atan2(double y, double x) {
        return MathHelper.atan2(y, x);
    }

    public float cos(double angle) {
        return MathHelper.cos(angle);
    }

    public float sin(double angle) {
        return MathHelper.sin(angle);
    }

    public float tan(double angle) {
        return MathHelper.tan(angle);
    }

    public float csc(double angle) {
        return 1 / MathHelper.sin(angle);
    }

    public float sec(double angle) {
        return 1 / MathHelper.cos(angle);
    }

    public float cot(double angle) {
        return 1 / MathHelper.tan(angle);
    }

    public float acsc(double n) {
        return 1 / asin(n);
    }

    public float asec(double n) {
        return 1 / acos(n);
    }

    public float acot(double n) {
        return 1 / atan(n);
    }

    public float degrees(double radians) {
        return (float) Math.toDegrees(radians);
    }

    public float radians(double degrees) {
        return (float) Math.toRadians(degrees);
    }

    // Random Functions
    public float random(double high) {
        return MathHelper.random(high);
    }

    public float random(double low, double high) {
        return MathHelper.random(low, high);
    }

    public float random() {
        return MathHelper.random();
    }

    public char random(char[] array) {
        return MathHelper.random(array);
    }

    public byte random(byte[] array) {
        return MathHelper.random(array);
    }

    public int random(int[] array) {
        return MathHelper.random(array);
    }

    public long random(long[] array) {
        return MathHelper.random(array);
    }

    public float random(float[] array) {
        return MathHelper.random(array);
    }

    public double random(double[] array) {
        return MathHelper.random(array);
    }

    public boolean random(boolean[] array) {
        return MathHelper.random(array);
    }

    public String random(String[] array) {
        return MathHelper.random(array);
    }

    public PVector random(PVector[] array) {
        return MathHelper.random(array);
    }

    public Object random(Object[] array) {
        return MathHelper.random(array);
    }

    public float randomGaussian() {
        return (float) (new Random().nextGaussian());
    }

    // Shuffle
    /**
     * Returns a randomized copy of the array.
     */
    public char[] shuffle(char[] array) {
        char[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public byte[] shuffle(byte[] array) {
        byte[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            byte temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public int[] shuffle(int[] array) {
        int[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public long[] shuffle(long[] array) {
        long[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            long temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public float[] shuffle(float[] array) {
        float[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            float temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public double[] shuffle(double[] array) {
        double[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            double temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public boolean[] shuffle(boolean[] array) {
        boolean[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            boolean temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public String[] shuffle(String[] array) {
        String[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public PVector[] shuffle(PVector[] array) {
        PVector[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            PVector temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public Object[] shuffle(Object[] array) {
        Object[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Object temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    // Noise Functions
    // TODO Implement rest of noise functions
    public float noise(double x, double y, double z, double w) {
        // switch (noiseFunction) {
        // case SIMPLEX:
        // return NoiseHelper.simplexNoise(x, y, z, w);
        // case VORONOI:
        // return NoiseHelper.voronoiNoise(x, y, z, w);
        // case WORLEY:
        // return NoiseHelper.worleyNoise(x, y, z, w);
        // case RIDGED:
        // return NoiseHelper.ridgedNoise(x, y, z, w);
        // default:
        // return NoiseHelper.simplexNoise(x, y, z, w);
        // }
        return NoiseHelper.simplexNoise(x, y, z, w);
    }

    public float noise(double x, double y, double z) {
        // switch (noiseFunction) {
        // case SIMPLEX:
        // return NoiseHelper.simplexNoise(x, y, z);
        // case VORONOI:
        // return NoiseHelper.voronoiNoise(x, y, z);
        // case WORLEY:
        // return NoiseHelper.worleyNoise(x, y, z);
        // case RIDGED:
        // return NoiseHelper.ridgedNoise(x, y, z);
        // default:
        // return NoiseHelper.simplexNoise(x, y, z);
        // }
        return NoiseHelper.simplexNoise(x, y, z);
    }

    public float noise(double x, double y) {
        // switch (noiseFunction) {
        // case SIMPLEX:
        // return NoiseHelper.simplexNoise(x, y);
        // case VORONOI:
        // return NoiseHelper.voronoiNoise(x, y);
        // case WORLEY:
        // return NoiseHelper.worleyNoise(x, y);
        // case RIDGED:
        // return NoiseHelper.ridgedNoise(x, y);
        // default:
        // return NoiseHelper.simplexNoise(x, y);
        // }
        return NoiseHelper.simplexNoise(x, y);
    }

    public float noise(double x) {
        // switch (noiseFunction) {
        // case SIMPLEX:
        // return NoiseHelper.simplexNoise(x);
        // case VORONOI:
        // return NoiseHelper.voronoiNoise(x);
        // case WORLEY:
        // return NoiseHelper.worleyNoise(x);
        // case RIDGED:
        // return NoiseHelper.ridgedNoise(x);
        // default:
        // return NoiseHelper.simplexNoise(x);
        // }
        return NoiseHelper.simplexNoise(x);
    }

    /**
     * Returns noise value for specified 3D coordinate.
     * 
     * @param p
     */
    public float noise(PVector p) {
        // switch (noiseFunction) {
        // case SIMPLEX:
        // return NoiseHelper.simplexNoise(p.copy());
        // case VORONOI:
        // return NoiseHelper.voronoiNoise(p.copy());
        // case WORLEY:
        // return NoiseHelper.worleyNoise(p.copy());
        // case RIDGED:
        // return NoiseHelper.ridgedNoise(p.copy());
        // default:
        // return NoiseHelper.simplexNoise(p.copy());
        // }
        return NoiseHelper.simplexNoise(p.copy());
    }

    // color
    public color color(Color color) {
        return new color(color);
    }

    public color color(color c, double a) {
        return new color(c, a);
    }

    public color color(double gray, double alpha) {
        return new color(gray, alpha);
    }

    public color color(double gray) {
        return color(gray, 255);
    }

    public color color(double red, double green, double blue, double alpha) {
        return new color(red, green, blue, alpha);
    }

    public color color(double red, double green, double blue) {
        return color(red, green, blue, 255);
    }

    public color color(String c) {
        return new color(c);
    }

    public int alpha(color color) {
        return color.a;
    }

    public int blue(color color) {
        return color.b;
    }

    public int green(color color) {
        return color.g;
    }

    public int red(color color) {
        return color.r;
    }

    public float brightness(color color) {
        return color.getBrightness();
    }

    public color lerpColor(color c1, color c2, double amt) {
        return color(
                Lerp.lerp(c1.getRed(), c2.getRed(), amt),
                Lerp.lerp(c1.getGreen(), c2.getGreen(), amt),
                Lerp.lerp(c1.getBlue(), c2.getBlue(), amt),
                Lerp.lerp(c1.getAlpha(), c2.getAlpha(), amt));
    }

    public float saturation(color color) {
        float[] hsb = color.RGBtoHSB();
        return hsb[1];
    }

    public float hue(color color) {
        float[] hsb = color.RGBtoHSB();
        return hsb[0];
    }

    /**
     * Saves the frame to a .png file.
     * 
     * @param filename
     */
    public void save(String filename) {
        try {
            ImageIO.write(img, "png", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveStrings(String[] lines, String filename) {
        try {
            Files.write(Paths.get(filename), Arrays.asList(lines));
        } catch (IOException e) {
        }
    }

    public void saveStrings(java.util.List<String> lines, String filename) {
        try {
            Files.write(Paths.get(filename), lines);
        } catch (IOException e) {
        }
    }

    // Shaders
    /**
     * Runs a shader on the entire image. The Dispatch method of the shader should
     * be written for one pixel and it will be run for every pixel in the image in
     * parallel.
     * 
     * @param shader
     */
    public void shaderTexture(Shader shader) {
        int cores = Runtime.getRuntime().availableProcessors();
        // Create columns of chunks
        int threadWidth = width / cores;
        int threadWidthRemainder = width % cores;

        Thread[] threads = new Thread[cores];
        for (int i = 0; i < cores; i++) {
            final int xStart = i * threadWidth;
            int w = threadWidth;
            if (i == cores - 1) {
                w += threadWidthRemainder;
            }
            final int xEnd = xStart + w;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int x = xStart; x < xEnd; x++) {
                        for (int y = 0; y < height; y++) {
                            shader.Dispatch(new PVector(x, y));
                        }
                    }
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < cores; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Runs a shader on an array. The Dispatch method of the shader should be
     * written for one element in the array.
     * 
     * @param shader
     * @param arrayLength
     */
    public void shaderArray(Shader shader, int arrayLength) {
        int cores = Runtime.getRuntime().availableProcessors();
        // Sections of the array
        int threadWidth = arrayLength / cores;
        int threadWidthRemainder = arrayLength % cores;

        Thread[] threads = new Thread[cores];
        for (int i = 0; i < cores; i++) {
            final int xStart = i * threadWidth;
            int w = threadWidth;
            if (i == cores - 1) {
                w += threadWidthRemainder;
            }
            final int xEnd = xStart + w;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int x = xStart; x < xEnd; x++) {
                        shader.Dispatch(x);
                    }
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < cores; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Converting ArrayLists to arrays
    public char[] toCharArray(ArrayList<Character> array) {
        return Helper.toCharArray(array);
    }

    public byte[] toByteArray(ArrayList<Byte> array) {
        return Helper.toByteArray(array);
    }

    public int[] toIntArray(ArrayList<Integer> array) {
        return Helper.toIntArray(array);
    }

    public long[] toLongArray(ArrayList<Long> array) {
        return Helper.toLongArray(array);
    }

    public float[] toFloatArray(ArrayList<Float> array) {
        return Helper.toFloatArray(array);
    }

    public double[] toDoubleArray(ArrayList<Double> array) {
        return Helper.toDoubleArray(array);
    }

    public boolean[] toBooleanArray(ArrayList<Boolean> array) {
        return Helper.toBooleanArray(array);
    }

    public String[] toStringArray(ArrayList<String> array) {
        return Helper.toStringArray(array);
    }

    public PVector[] toPVectorArray(ArrayList<PVector> array) {
        return Helper.toPVectorArray(array);
    }

    public Object[] toObjectArray(ArrayList<Object> array) {
        return Helper.toObjectArray(array);
    }

    public color[] toColorArray(ArrayList<color> array) {
        return Helper.toColorArray(array);
    }
}
