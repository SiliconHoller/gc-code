/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis.features;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Vector;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class RGBImageDataSet extends ImageDataSet {

    public RGBImageDataSet(BufferedImage img) {
        super(img);
        extract();
    }

    @Override
    protected void extract() {
        Vector<Instance> vi = new Vector<Instance>();
        int r;
        int g;
        int b;
        int pixel;
        ColorModel cm = img.getColorModel();
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                pixel = img.getRGB(i, j);
                r = cm.getRed(pixel);
                g = cm.getGreen(pixel);
                b = cm.getBlue(pixel);
                Instance inst = new Instance.RGBPixel(i,j,r,g,b);
                vi.add(inst);
            }
        }
        imap.add(vi);
        dset = new DataSet(vi);
    }



}
