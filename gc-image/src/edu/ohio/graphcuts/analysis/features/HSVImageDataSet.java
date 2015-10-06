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
public class HSVImageDataSet extends ImageDataSet {

    public HSVImageDataSet(BufferedImage img) {
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
        float[] hsv = new float[3];
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                pixel = img.getRGB(i, j);
                r = getRed(pixel);
                g = getGreen(pixel);
                b = getBlue(pixel);
                Color.RGBtoHSB(r, g, b, hsv);
                Instance inst = new Instance.HSVPixel(i,j,hsv[0],hsv[1],hsv[2]);
                vi.add(inst);
            }
        }
        imap.add(vi);
        dset = new DataSet(vi);
    }

    protected int getRed(int pixel) {
        return (pixel & 0x00FF0000) >> 16;
    }

    protected int getGreen(int pixel) {
        return (pixel & 0x0000FF00) >> 8;
    }

    protected int getBlue(int pixel) {
        return pixel & 0x000000FF;
    }

}
