/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.event;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public interface CoordinateListener {

    public void setX(int x);
    public void setY(int y);
    public void setZ(int z);

    public void setOrigin(int x, int y, int z);

}
