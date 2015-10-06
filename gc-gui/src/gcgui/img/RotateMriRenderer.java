/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.img;

import gcgui.event.RenderListener;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class RotateMriRenderer extends MriRenderer {


    public RotateMriRenderer() {
    }

    @Override
    public void render(double[][] array, RenderListener rl) {
        BufferedImage oimg = renderImage(array);
        //Now rotate 180-degrees
        BufferedImage img = new BufferedImage(oimg.getWidth(), oimg.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.rotate(Math.PI, img.getWidth()/2, img.getHeight()/2);
        g.drawRenderedImage(oimg, null);
        rl.setImage(img);
    }

}
