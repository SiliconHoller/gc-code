/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import java.util.Arrays;

/**
 *
 * @author david
 */
public class XYGrayKMeans implements KMeans {

    protected double[][] averages;
    protected double[][] features;
    protected int[] classes;
    protected int numFeatures;
    protected double epsilon = 0.003;
    protected int epoch = 10000;
    protected int count;

    /*
     * Basic 2-class un-initialized k-means classification
     *
     */
    public XYGrayKMeans(double[][] features, int numFeatures) {
        this.features = features;
        this.numFeatures = numFeatures;
        initKMeans(2);
    }

    public XYGrayKMeans(double[][] features, int numFeatures, double[] classHint) {
        this.features = features;
        this.numFeatures = numFeatures;
        initKMeans(2);
        averages[0] = classHint;
    }

    public XYGrayKMeans(double[][] features, int numFeatures, double[][] averages) {
        this.features = features;
        this.averages = averages;
        classes = new int[averages.length];
        Arrays.fill(classes,-1);
    }

    protected void initKMeans(int numClasses) {
        classes = new int[features.length];
        Arrays.fill(classes,-1);
        averages = new double[numClasses][numFeatures];
        initAverages();
    }

    protected void initAverages() {
        int avg = 0;
        for (int i=0;i<features.length;i++) {
            if (features[i].length != 0) {
                double[] f = features[i];
                for (int j=0;j<f.length;j++) {
                    averages[avg][j] = f[j];
                }
                avg++;
                if (avg == averages.length) i = features.length;//time saver
            }
        }
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public int getEpoch() {
        return epoch;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public int getCount() {
        return count;
    }

    public int[] getClassifications() {
        return classes;
    }

    public double[][] getFeatures() {
        return features;
    }

    public double[][] getAverages() {
        return averages;
    }

    public int getNumFeatures() {
        return numFeatures;
    }

    public boolean analyze() {
        boolean retVal = false;
        double diff = 1.0;
        count = 0;
        while (diff > epsilon && count < epoch) {
            classify();
            diff = adjust();
            count++;
        }
        if (count < epoch) retVal = true;
        return retVal;
    }

    protected void classify() {
        for (int i=0;i<classes.length;i++) {
            if (features[i].length != 0) {
                classes[i] = min(features[i]);
            }
        }
    }

    protected double adjust() {
        double diff = 0.0;
        for (int i=0;i<averages.length;i++) {
            double[] avg = new double[numFeatures];
            int acount = 0;
            for (int j=0;j<classes.length;j++) {
                if (classes[j] == i) {
                    double[] f = features[j];
                    for (int k=0;k<avg.length;k++) {
                        avg[k] = avg[k] + f[k];
                    }
                    acount++;
                }
            }
            //Calculate the averages
            if (acount != 0) {
                for (int m=0;m<avg.length;m++) {
                    avg[m] = avg[m]/(double)acount;
                }
            }
            double mvt = euclidianDist(averages[i],avg);
            if (mvt > diff) diff = mvt;
            for (int n=0;n<avg.length;n++) {
                averages[i][n] = avg[n];
            }
        }

        return diff;
    }

    protected int min(double[] f) {
        int c = 0;
        //start with class 0
        double min = euclidianDist(f,0);
        double curr;
        for (int i=1;i<averages.length;i++) {
            curr = euclidianDist(f,i);
            if (curr <= min) c = i;
        }
        return c;
    }

    protected double euclidianDist(double[] f, int clazz) {
        double dist = 0.0;
        double[] diffs = new double[f.length];
        //calculate the differences
        for (int i=0;i<diffs.length;i++) {
            diffs[i] = averages[clazz][i] - f[i];
        }

        for (int j=0;j<diffs.length;j++) {
            dist = dist + diffs[j]*diffs[j];
        }

        return Math.sqrt(dist);
    }

    protected double euclidianDist(double[] pre, double[] post) {
        double dist = 0.0;
        double[] diffs = new double[pre.length];
        for (int i=0;i<diffs.length;i++) {
            diffs[i] = pre[i] - post[i];
        }

        for (int j=0;j<diffs.length;j++) {
            dist = dist + diffs[j]*diffs[j];
        }

        return Math.sqrt(dist);
    }
}
