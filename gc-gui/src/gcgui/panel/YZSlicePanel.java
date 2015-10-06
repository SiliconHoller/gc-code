/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.panel;

import gcgui.event.CoordinateListener;
import java.awt.event.MouseEvent;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class YZSlicePanel extends SlicePanel {

    protected int x;
    protected int xmax;

    public YZSlicePanel() {
        super();
    }

    @Override
    protected void drawSlice() {
        if (volume != null) {

            double[][] slice = volume.readYZSlice(x);
            mr.render(slice,this);
        }
    }

    @Override
    public void drawSub() {
        if (svr != null) {
            if (svr.isDrawing()) {
                svr.drawSubYZ(img);
            }
        }
    }

    @Override
    protected void setMax() {
        xmax = volume.getWidth()-1;
    }

    @Override
    public void adjustX(int num) {
        int val = x+num;
        if (val < 0) {
            val = 0;
        } else if (val > xmax) {
            val = xmax;
        }
        if (x != val) {
            x = val;
            fireXChange();
            drawSlice();
        }
    }

    @Override
    public void adjustY(int num) {
    }

    @Override
    public void adjustZ(int num) {
    }

    @Override
    public void setXVal(int x) {
        this.x = x;
        fireYChange();
        drawSlice();
    }

    @Override
    public void setYVal(int y) {
    }

    @Override
    public void setZVal(int z) {
    }

    @Override
    public int getZVal() {
        return -1;
    }

    @Override
    public int getXVal() {
        return x;
    }

    @Override
    public int getYVal() {
        return -1;
    }

    @Override
    public void fireXChange() {
        for (CoordinateListener cl : cv) {
            cl.setX(x);
        }
    }

    @Override
    public void fireYChange() {
    }

    @Override
    public void fireZChange() {
    }


    @Override
    protected void wheelMoved(int numClicks) {
        adjustX(numClicks);
    }

    @Override
    protected void mouseClicked(MouseEvent evt) {
        if (svr != null) {
            if (svr.isDrawing()) {
                int my = (int)((double)evt.getX()/xscale);
                int mz = (int)((double)evt.getY()/yscale);
                int h = volume.getHeight();
                int zh = volume.getZHeight();
                for (CoordinateListener cl:cv) {
                    cl.setOrigin(x,h-my-1,zh-mz-1);
                }
            }
        }
    }

}
