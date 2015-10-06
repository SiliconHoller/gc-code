/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.data;

import edu.ohio.graphcuts.analysis.features.Instance;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Vector;

/**
 * <p>Container for adding image samples which also performs the final
 * full sampling from the given image.</p>
 * @author David Days <david.c.days@gmail.com>
 */
public abstract class Sample {

    /**
     * Container for the raw sample data.
     */
    protected Vector<Instance> rawsample;

    /**
     * Creates a new and empty sampling container.
     */
    public Sample() {
        rawsample = new Vector<Instance>();
    }

    
    /**
     * Adds a single data instance to the raw sampling container.
     * @param i Instance to be added.
     */
    public void add(Instance i) {
        rawsample.add(i);
    }

    public abstract Sample newInstance();

    /**
     * Adds a Collection of data instances to the container.
     * @param s Instance collection to be added.
     */
    public void add(Collection<Instance> s) {
        rawsample.addAll(s);
    }

    public Collection<Instance> getRawSample() {
        return rawsample;
    }

    /**
     * <p>Given the image, this method uses the raw sample data, extracts the
     * appropriate gray pixel values, and returns a cleaned and full-featured sample
     * from the image.  Subclasses provide an implementation-specific set of features.</p>
     * @param img Image from which to draw the pixel intensities.
     * @return A feature-complete Collection of the data instances.
     */
    public abstract Collection<Instance> getCompleteSample(BufferedImage img);

    /**
     * <p>Given the 3D volume, this method uses the raw sample data and extracts the fully-featured
     * voxel data.</p>
     * @param volume 3D volume from which the raw data was taken.
     * @return A cleaned, fully-featured Collection of data instances.
     */
    public abstract Collection<Instance> getCompleteSample(double[][][] volume);

    @Override
    public String toString() {
        String cname = getClass().getName();
        String name = cname.substring(cname.lastIndexOf("."));
        return name+": "+Integer.toString(rawsample.size())+" samples.";
    }
}
