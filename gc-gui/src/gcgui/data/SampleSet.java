/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.data;

import edu.ohio.graphcuts.analysis.features.DataSet;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Vector;

/**
 * <p>Container class that creates new and empty sample sets, manages
 * their addition and removal, and converts the data into individual DataSet
 * instances.</p>
 * @author David Days <david.c.days@gmail.com>
 */
public class SampleSet{

    /**
     * Collection of Samples.
     */
    protected Vector<Sample> sset;


    /**
     * Constructor for an empty set of samples.
     */
    public SampleSet() {
        sset = new Vector<Sample>();
    }


    /**
     * Returns the current Sample set.  Any changes made here will
     * affect the underlying set of data.
     * @return The current Sample set.
     */
    public Vector<Sample> getSet() {
        return sset;
    }

    public void addSample(Sample sample) {
        sset.add(sample);
    }

    /**
     * Removes the given sample from the data set.
     * @param sample Sample container to be removed.
     */
    public void removeSample(Sample sample) {
        sset.remove(sample);
    }

    /**
     * Returns the number of samples currently herein.
     * @return Size of the current sample set.
     */
    public int numSamples() {
        return sset.size();
    }

    /**
     * Given the image, the Samples are converted to individual DataSet
     * instances with complete parameters.
     * @param img Image from which the samples were drawn.
     * @return Feature-complete DataSet instances.
     */
    public Collection<DataSet> getDataSets(BufferedImage img) {
        Vector<DataSet> dsets = new Vector<DataSet>();
        for (Sample s:sset) {
            DataSet dset = new DataSet(s.getCompleteSample(img));
            dsets.add(dset);
        }
        return dsets;
    }

    /**
     * Given the 3D volume, the Samples are converted to individual DataSet instances
     * with complete parameters.
     * @param volume 3D volume used for sampling.
     * @return Feature-complete DataSet instances.
     */
    public Collection<DataSet> getDataSets(double[][][] volume) {
        Vector<DataSet> dsets = new Vector<DataSet>();
        for (Sample s:sset) {
            DataSet dset = new DataSet(s.getCompleteSample(volume));
            dsets.add(dset);
        }
        return dsets;
    }

    @Override
    public String toString() {
        return "Sample set of "+Integer.toString(numSamples());
    }
}
