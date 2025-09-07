package library.core;

import java.awt.event.*;

public class WindowHandler implements WindowListener {

    Applet applet;

    public WindowHandler(Applet applet) {
        this.applet = applet;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void windowActivated(WindowEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        applet.addEvent(e);
    }

}
