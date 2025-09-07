package library.core;

import java.awt.event.*;

public class KeyHandler implements KeyListener {

    Applet applet;

    public KeyHandler(Applet applet) {
        this.applet = applet;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // applet.keyTpe(e);
        applet.addEvent(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        applet.addEvent(e);
    }

}
