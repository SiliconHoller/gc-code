/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis.features;

import java.util.Arrays;

/**
 * <p>Abstract class to encapsulate features of a particular instance
 * of a data type.</p>
 * <p>This class is primarily for use in the DataSet class to enable simple
 * calculations and groupings over particular instances.  The concrete subclasses define
 * the number and position of features encapsulated.</p>
 * @author David Days <david.c.days@gmail.com>
 */
public abstract class Instance {

    /**
     * Classification value for k-means and kernel methods.
     */
    protected int clazz = -1;
    /**
     * Array containing the feature values
     */
    protected double[] vals;


    /**
     * Subclasses should call super(int numFeatures) to establish
     * how many features will be stored.
     * @param numFeatures Number of features for which to build the array.
     */
    protected Instance(int numFeatures) {
        vals = new double[numFeatures];
    }

    /**
     * Abstract method to find out how many features are stored in this instance.
     * @return
     */
    public abstract int getNumFeatures();

    /**
     * Returns the array holding the feature values.
     * @return Array containing the feature values.
     */
    public double[] getVals() {
        return vals;
    }

    /**
     * Returns the value of the requested feature.  Note:  No
     * checks are made to ensure that the request is within the limits
     * of the feature array.
     * @param fnum Feature number (0 to vals.length) requested.
     * @return Value within the feature array.
     */
    public double getVal(int fnum) {
        return vals[fnum];
    }

    /**
     * Integer classification value.  Default (unset) value is -1.
     * @return Classification of this instance; -1 if unset.
     */
    public int getClazz() {
        return clazz;
    }

    /**
     * Sets the classification of this instance.
     * @param clazz Classification to be set for this particular instance.
     */
    public void setClazz(int clazz) {
        this.clazz = clazz;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Arrays.hashCode(this.vals);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Instance)) return false;
        return Arrays.equals(vals, ((Instance)o).vals);
    }
    /**
     * Instance class to encapsulate gray-valued pixels in a 2D image.  Three
     * features (x, y, gray value) are held in that order.
     */
    public static class GrayPixel extends Instance {

        /**
         * Number of features (3) in this instance.
         */
        protected static final int NUMFEATURES = 3;

        /**
         * Default constructor with 3 features all set to 0.
         */
        public GrayPixel() {
            super(NUMFEATURES);
        }

        /**
         * Constructor assigning x, y, and gray values.
         * @param x X position
         * @param y Y position
         * @param gray Gray-scale intensity value
         */
        public GrayPixel(int x, int y, int gray) {
            this();
            vals[0] = x;
            vals[1] = y;
            vals[2] = gray;
        }

        /**
         * Overridden constructor for double/floats.
         * @param x X Position
         * @param y Y position
         * @param gray Gray-scale intensity.
         */
        public GrayPixel(double x, double y, double gray) {
            this();
            vals[0] = x;
            vals[1] = y;
            vals[2] = gray;
        }

        @Override
        public int getNumFeatures() {
            return NUMFEATURES;
        }
    }

    /**
     * Instance class to encapsulate position and RGB color values.
     * This class has 5 features (x, y, R, G, B).
     */
    public static class RGBPixel extends Instance {

        /**
         * Number of features encapsulation:  5.
         */
        protected static final int NUMFEATURES = 5;

        /**
         * Default constructor with all values set to 0.
         */
        public RGBPixel() {
            super(NUMFEATURES);
        }

        /**
         * Constructor setting the 5 values describing this pixel
         * @param x X position.
         * @param y Y position.
         * @param rval Red pixel value
         * @param gval Green pixel value
         * @param bval Blue pixel value.
         */
        public RGBPixel(int x, int y, int rval, int gval, int bval) {
            this();
            vals[0] = x;
            vals[1] = y;
            vals[2] = rval;
            vals[3] = gval;
            vals[4] = bval;
        }

        /**
         * Overridden constructor for double/float values.
         * @param x X Position
         * @param y Y Position
         * @param rval Red value
         * @param gval Green value
         * @param bval Blue value
         */
        public RGBPixel(double x, double y, double rval, double gval, double bval) {
            this();
            vals[0] = x;
            vals[1] = y;
            vals[2] = rval;
            vals[3] = gval;
            vals[4] = bval;
        }

        @Override
        public int getNumFeatures() {
            return NUMFEATURES;
        }
    }

    /**
     * Subclass of Instance for encapsulating pixel HSV values.  This class
     * has 5 features (x,y,h,s,v).
     */
    public static class HSVPixel extends Instance {

        /**
         * Number of features:  5.
         */
        protected static final int NUMFEATURES = 5;

        /**
         * Default constructor with 5 features, all valued at 0.
         */
        public HSVPixel() {
            super(NUMFEATURES);
        }

        /**
         * Constructor storing the given position and HSV parameters.
         * @param x X position.
         * @param y Y position.
         * @param h Hue value.
         * @param s Saturation value.
         * @param v Value value.
         */
        public HSVPixel(int x, int y, double h, double s, double v) {
            this();
            vals[0] = x;
            vals[1] = y;
            vals[2] = h;
            vals[3] = s;
            vals[4] = v;
        }

        public HSVPixel(double x, double y, double h, double s, double v) {
            this();
            vals[0] = x;
            vals[1] = y;
            vals[2] = h;
            vals[3] = s;
            vals[4] = v;
        }

        @Override
        public int getNumFeatures() {
            return NUMFEATURES;
        }
        
    }

    /**
     * Instance class to encapsulate 3D voxel data.  This class
     * has 4 features (x, y, z, gray scale);
     */
    public static class GrayVoxel extends Instance {

        /**
         * Number of features: 4
         */
        protected static final int NUMFEATURES = 4;

        /**
         * Default constructor with zero values.
         */
        public GrayVoxel() {
            super(NUMFEATURES);
        }

        /**
         * Integer constructor.
         * @param x X position
         * @param y Y position
         * @param z Z position
         * @param gray Gray-scale value.
         */
        public GrayVoxel(int x, int y, int z, int gray) {
            this();
            vals[0] = x;
            vals[1] = y;
            vals[2] = z;
            vals[3] = gray;
        }

        /**
         * Constructor for double/float values.
         * @param x X position
         * @param y Y position
         * @param z Z position
         * @param gray Gray-scale value.
         */
        public GrayVoxel(double x, double y, double z, double gray) {
            this();
            vals[0] = x;
            vals[1] = y;
            vals[2] = z;
            vals[3] = gray;
        }

        @Override
        public int getNumFeatures() {
            return NUMFEATURES;
        }

    }


}
