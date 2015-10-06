/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis.features;

import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class GrayImageDataSet extends ImageDataSet {

    public GrayImageDataSet(BufferedImage img) {
        super(img);
        extract();
    }

    @Override
    protected void extract() {
        Vector<Instance> vi = new Vector<Instance>();
        int gval;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                gval = getGrayVal(img.getRGB(i, j));
                Instance inst = new Instance.GrayPixel(i,j,gval);
                vi.add(inst);
            }
        }
        imap.add(vi);
        dset = new DataSet(vi);
    }

    /**
     * Given the RGB pixel, this method returns the corresponding gray
     * value based on the mean of the R, G, and B components.
     * @param pixel Pixel data to be evaluated.
     * @return The mean of the red, green, and blue intensities.
     */
    protected int getGrayVal(int pixel) {
        int val = 0;

        int red = (pixel & 0x00FF0000) >> 16;
        int green = (pixel & 0x0000FF00) >> 8;
        int blue = pixel & 0x000000FF;
        val = (red+green+blue)/3;
        //System.out.println("pixel value = "+val);
        return val;
    }

}
