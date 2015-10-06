/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.panel;

import gcgui.event.CoordinateListener;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class CoordToolBar extends JToolBar implements CoordinateListener {

    private JLabel xlabel;
    private JLabel xpos;
    private JLabel ylabel;
    private JLabel ypos;
    private JLabel zlabel;
    private JLabel zpos;
    private JLabel tlabel;
    private JLabel tpos;

    public CoordToolBar() {
        super();
        initComponents();
    }

    private void initComponents() {
        xlabel = new JLabel("X = ");
        ylabel = new JLabel("Y = ");
        zlabel = new JLabel("Z = ");
        tlabel = new JLabel("T = ");

        xpos = new JLabel();
        ypos = new JLabel();
        zpos = new JLabel();
        tpos = new JLabel();

        add(xlabel);
        add(xpos);
        add(new JToolBar.Separator());
        add(ylabel);
        add(ypos);
        add(new JToolBar.Separator());
        add(zlabel);
        add(zpos);
        add(new JToolBar.Separator());
        add(tlabel);
        add(tpos);

    }

    public void setX(int x) {
        xpos.setText(Integer.toString(x));
    }

    public void setY(int y) {
        ypos.setText(Integer.toString(y));
    }

    public void setZ(int z) {
        zpos.setText(Integer.toString(z));
    }

    public void setOrigin(int x, int y, int z) {
        //do nothing--CoordToolBar doesn't track the origin of a volume.
    }

}
