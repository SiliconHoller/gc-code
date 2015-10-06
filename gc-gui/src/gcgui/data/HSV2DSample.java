/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.data;

import edu.ohio.graphcuts.analysis.features.Instance;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class HSV2DSample extends Sample {

    public HSV2DSample() {
        super();
    }

    @Override
    public Sample newInstance() {
        return new HSV2DSample();
    }

    @Override
    public Collection<Instance> getCompleteSample(BufferedImage img) {
        HashSet<Instance> sample = new HashSet<Instance>();
        int pixel;
        int x;
        int y;
        int r;
        int g;
        int b;
        float[] hsv = new float[3];
        ColorModel cm = img.getColorModel();
        for (Instance ri: rawsample) {
            x = (int)ri.getVal(0);
            y = (int) ri.getVal(1);
            pixel = img.getRGB(x,y);
            r = getRed(pixel);
            g = getGreen(pixel);
            b = getBlue(pixel);
            Color.RGBtoHSB(r, g, b, hsv);
            Instance i = new Instance.HSVPixel(x,y,hsv[0],hsv[1],hsv[2]);
            sample.add(i);
        }
        return sample;
    }

    @Override
    public Collection<Instance> getCompleteSample(double[][][] volume) {
        return null;
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
