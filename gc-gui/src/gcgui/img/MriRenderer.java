/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.img;

import gcgui.event.RenderListener;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class MriRenderer {

    protected RenderListener rlistener;
    protected double[][] array;
    protected BufferedImage rimg;


    public MriRenderer() {
        
    }

    public void render(double[][] array, RenderListener rl) {

        rl.setImage(renderImage(array));
    }

    protected BufferedImage renderImage(double[][] array) {
        int w = array.length;
        int h = array[0].length;
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);

        int gval;
        Color c;
        double amax = arrayMax(array);
        //int xmax = w-1;
        //int ymax = h -1;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {

                gval = (int)(255*(array[i][j]/amax));
                c = new Color(gval,gval,gval);
                img.setRGB(i, j, c.getRGB());
            }
        }
        return img;
    }


    protected double arrayMax(double[][] arr) {
        double max = 0.0;
        for (int i=0;i<arr.length;i++) {
            for (int j=0;j<arr[0].length;j++) {
                if (arr[i][j] > max) max = arr[i][j];
            }
        }

        if (max == 0.0) {
            max = 1.0;
        }

        return max;
    }


}
