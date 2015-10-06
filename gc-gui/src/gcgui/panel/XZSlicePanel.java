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
public class XZSlicePanel extends SlicePanel {

    protected int ymax;
    protected int y;

    public XZSlicePanel() {
        super();
    }
    
    @Override
    protected void drawSlice() {
        if (volume != null) {

            double[][] slice = volume.readXZSlice(y);
            mr.render(slice,this);
        }
    }

    @Override
    public void drawSub() {
        if (svr != null) {
            if (svr.isDrawing()) {
                svr.drawSubXZ(img);
            }
        }
    }

    @Override
    protected void setMax() {
        ymax = volume.getWidth() -1;
    }

    @Override
    public void adjustX(int num) {
    }

    @Override
    public void adjustY(int num) {
        int val = y+num;
        if (val < 0) {
            val = 0;
        } else if (val > ymax) {
            val = ymax;
        }
        if (y != val) {
            y = val;
            fireYChange();
            drawSlice();
        }
    }

    @Override
    public void adjustZ(int num) {
    }

    @Override
    public void setXVal(int x) {
    }

    @Override
    public void setYVal(int y) {
        this.y = y;
        fireYChange();
        drawSlice();
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
        return -1;
    }

    @Override
    public int getYVal() {
        return y;
    }

    @Override
    public void fireXChange() {
        
    }

    @Override
    public void fireYChange() {
        for (CoordinateListener cl : cv) {
            cl.setY(y);
        }
    }

    @Override
    public void fireZChange() {
        
    }


    @Override
    protected void wheelMoved(int numClicks) {
        adjustY(numClicks);
    }

    @Override
    protected void mouseClicked(MouseEvent evt) {
        if (svr != null) {
            if (svr.isDrawing()) {
                int mx = (int)((double)evt.getX()/xscale);
                int mz = (int)((double)evt.getY()/yscale);
                int w = volume.getWidth();
                int zh = volume.getZHeight();

                for (CoordinateListener cl:cv) {
                    cl.setOrigin(w-mx-1,y,zh-mz-1);
                }
            }
        }
    }

}
