/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.img;

import gcgui.event.CoordinateListener;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class SubVolRenderer implements CoordinateListener {

    private boolean drawing = false;
    private int ox;
    private int oy;
    private int oz;
    private int dx;
    private int dy;
    private int dz;
    private Color ocolor = Color.yellow;
    private Color dcolor = Color.blue;

    private Vector<ChangeListener> clists;

    public SubVolRenderer() {
        ox = 0;
        oy = 0;
        oz = 0;
        dx = 0;
        dy = 0;
        dz = 0;
    }

    public void addChangeListener(ChangeListener cl) {
        if (clists == null) {
            clists = new Vector<ChangeListener>();
        }
        clists.add(cl);
    }

    public void fireChangeEvent() {
        if (clists != null) {
            for (ChangeListener cl :clists) {
                cl.stateChanged(new ChangeEvent(this));
            }
        }
    }

    public void setDrawing(boolean draw) {
        this.drawing = draw;
        fireChangeEvent();
    }

    public boolean isDrawing() {
        return drawing;
    }

    public void setOColor(Color o) {
        ocolor = o;
        fireChangeEvent();
    }

    public void setDColor(Color d) {
        dcolor = d;
        fireChangeEvent();
    }

    public void setOrigin(int ox, int oy, int oz) {
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
        fireChangeEvent();
    }

    public void setOx(int ox) {
        this.ox = ox;
        fireChangeEvent();
    }

    public int getOx() {
        return ox;
    }

    public void setOy(int oy) {
        this.oy = oy;
        fireChangeEvent();
    }

    public int getOy() {
        return oy;
    }

    public void setOz(int oz) {
        this.oz = oz;
        fireChangeEvent();
    }

    public int getOz() {
        return oz;
    }

    public void setDx(int dx) {
        this.dx = dx;
        fireChangeEvent();
    }

    public int getDx() {
        return dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
        fireChangeEvent();
    }

    public int getDy() {
        return dy;
    }

    public void setDz(int dz) {
        this.dz = dz;
        fireChangeEvent();
    }

    public int getDz() {
        return dz;
    }

    public void drawSubXY(BufferedImage img) {
        if (drawing) {

            Graphics2D g = (Graphics2D) img.getGraphics();
            g.setColor(dcolor);
            g.drawLine(ox, oy, (ox+dx), oy);
            g.drawLine(ox,oy,ox, (oy+dy));
            drawOrigin(ox, oy, img);
        }
    }

    public void drawSubXZ(BufferedImage img) {
        if (drawing) {

            Graphics2D g = (Graphics2D) img.getGraphics();
            g.setColor(dcolor);
            g.drawLine(ox, oz, (ox+dx), oz);
            g.drawLine(ox,oz,ox, (oz+dz));
            drawOrigin(ox, oz, img);
        }
    }

    public void drawSubYZ(BufferedImage img) {
        if (drawing) {

            Graphics2D g = (Graphics2D) img.getGraphics();
            g.setColor(dcolor);
            g.drawLine(oy, oz, (oy+dy), oz);
            g.drawLine(oy,oz,oy, (oz+dz));
            drawOrigin(oy, oz, img);
        }
    }

    public void drawOrigin(int x, int y, BufferedImage img) {
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(ocolor);
        g.drawLine(x, y, x, y);
    }

    public void setX(int x) {
        ox = x;
    }

    public void setY(int y) {
        oy = y;
    }

    public void setZ(int z) {
        oz = z;
    }
}
