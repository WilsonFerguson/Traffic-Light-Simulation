package library.core;

import java.awt.event.*;

import javax.swing.event.MouseInputListener;

public class MouseHandler implements MouseInputListener, MouseWheelListener {

    Applet applet;

    public MouseHandler(Applet applet) {
        this.applet = applet;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        applet.addEvent(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        applet.addEvent(e);
    }

}
