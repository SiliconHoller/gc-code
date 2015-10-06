/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.data;

import edu.ohio.graphcuts.analysis.features.Instance;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;

/**
 * <p>Container for adding image samples which also performs the final
 * full sampling from the given image.</p>
 * @author David Days <david.c.days@gmail.com>
 */
public class Gray2DSample extends Sample {

    /**
     * Creates a new and empty sampling container.
     */
    public Gray2DSample() {
        super();
    }

    /**
     * Creates and returns a new, empty sample of the same type.
     * @return New Gray2DSample instance.
     */
    public Gray2DSample newInstance() {
        return new Gray2DSample();
    }

    
    /**
     * <p>Given the image, this method uses the raw sample data, extracts the
     * appropriate gray pixel values, and returns a cleaned and full-featured sample
     * from the image, composed of Instance.GrayPixel data with x,y,gray values.</p>
     * <p>The Collection returned is a HashSet.  This object was used for
     * its ability to reject duplicate entries.</p>
     * @param img Image from which to draw the pixel intensities.
     * @return A Collection
     */
    public Collection<Instance> getCompleteSample(BufferedImage img) {
        HashSet<Instance> sample = new HashSet<Instance>();
        int gray;
        int x;
        int y;
        for (Instance ri: rawsample) {
            x = (int)ri.getVal(0);
            y = (int) ri.getVal(1);
            gray = getGrayVal(img.getRGB(x,y));
            Instance i = new Instance.GrayPixel(x,y,gray);
            sample.add(i);
        }
        return sample;
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

    /**
     * Not implemented for 2D images.
     * @param volume
     * @return Null in all cases.
     */
    @Override
    public Collection<Instance> getCompleteSample(double[][][] volume) {
        return null;
    }

}
