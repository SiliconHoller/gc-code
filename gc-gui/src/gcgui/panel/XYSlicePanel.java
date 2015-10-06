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
public class XYSlicePanel extends SlicePanel {

    protected int z;
    protected int zmax;

    public XYSlicePanel() {
        super();
    }

    @Override
    protected void drawSlice() {
        if (volume != null) {
            double[][] slice = volume.readXYSlice( z);
            mr.render(slice,this);
        }
    }

    @Override
    public void drawSub() {
        if (svr != null) {
            if (svr.isDrawing()) {
                svr.drawSubXY(img);
            }
        }
    }

    @Override
    protected void setMax() {
        zmax = volume.getZHeight()-1;
    }

    @Override
    public void adjustX(int num) {
        
    }

    @Override
    public void adjustY(int num) {
        
    }

    @Override
    public void adjustZ(int num) {
        int val = z+num;
        if (val < 0) {
            val = 0;
        } else if (val > zmax) {
            val = zmax;
        }
        if (val != z) {
            z = val;
            fireZChange();
            drawSlice();
        }
    }

    @Override
    public void setXVal(int x) {
        //do nothing
    }

    @Override
    public void setYVal(int y) {
        //do nothing
    }

    @Override
    public void setZVal(int z) {
        this.z = z;
        fireZChange();
        drawSlice();
    }

    @Override
    public int getZVal() {
        return z;
    }

    @Override
    public int getXVal() {
        return -1;
    }

    @Override
    public int getYVal() {
        return -1;
    }

    @Override
    public void fireXChange() {
        
    }

    @Override
    public void fireYChange() {
        
    }

    @Override
    public void fireZChange() {
        for (CoordinateListener cl : cv) {
            cl.setZ(z);
        }
    }


    @Override
    protected void wheelMoved(int numClicks) {
        adjustZ(numClicks);
    }

    @Override
    protected void mouseClicked(MouseEvent evt) {
        if (svr != null) {
            if (svr.isDrawing()) {
                int mx = (int)((double)evt.getX()/xscale);
                int my = (int)((double)evt.getY()/yscale);
                int w = volume.getWidth();
                int h = volume.getHeight();
                for (CoordinateListener cl:cv) {
                    cl.setOrigin(w-mx-1,h-my-1,z);
                }
            }
        }
    }

}
