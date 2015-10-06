/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.analysis.features.DataSet;
import edu.ohio.graphcuts.analysis.features.Instance;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class DataSetKMeans {


    protected int epoch = 10000;
    protected double epsilon = 0.003;
    protected int count = 0;
    protected int[] useFeatures;
    protected double[][] means = new double[0][0];
    protected DataSet dset;

    public DataSetKMeans(DataSet dset) {
        this.dset = dset;
    }

    public DataSetKMeans() {
        dset = null;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon= epsilon;
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


    public double[][] getMeans() {
        return means;
    }

    public void partition(double[][] means) {
        this.means = means;
        this.useFeatures = new int[0];
        partitionByAll();

    }

    public int getNumFeatures() {
        return dset.getNumFeatures();
    }

    public void partition(int clazzes, int[] useFeatures) {
        this.useFeatures = useFeatures;
        means = new double[clazzes][getNumFeatures()];
        partitionBySpecified();
    }

    public void partition(int clazzes) {
        this.useFeatures = new int[0];
        means = new double[clazzes][getNumFeatures()];
        partitionByAll();
    }

    protected void partitionByAll() {
        double diff = (double)epoch;
        count = 0;
        while (count < epoch && diff > epsilon) {
            classify();
            diff = adjust();
            count++;
        }
    }

    protected void partitionBySpecified() {
        double diff = 1.0;
        count = 0;
        while (count < epoch && diff > epsilon) {
            classifyBySpecified();
            diff = adjust();
            count++;
        }
    }

    protected void classify() {
        Instance[] is = dset.getData();
        for (Instance i: is) {
            i.setClazz(min(i));
        }
    }
    
    protected void classifyBySpecified() {
        Instance[] is = dset.getData();
        for (Instance i:is) {
            i.setClazz(minSpecified(i));
        }
    }

    protected double adjust() {
        double sum = 0.0;
        double[][] nmeans = new double[means.length][getNumFeatures()];
        //Set the new means
        for (int i=0;i<nmeans.length;i++) {
            nmeans[i] = dset.getClassAverage(i);
        }

        //Figure out the movement of the means
        for (int j=0;j<nmeans.length;j++) {
            sum += getEuclidean(means[j],nmeans[j]);
        }
        means = nmeans;
        return sum;
    }

    protected int min(Instance inst) {
        int clazz = -1;
        double mdist = Double.POSITIVE_INFINITY;
        double dist;
        for (int i=0;i<means.length;i++) {
            dist = getEuclidean(inst.getVals(),means[i]);
            if (dist < mdist) {
                clazz = i;
                mdist = dist;
            }
        }
        return clazz;
    }

    protected int minSpecified(Instance inst) {
        int clazz = -1;
        double mdist = Double.POSITIVE_INFINITY;
        double dist;
        for (int i=0;i<means.length;i++) {
            dist = getSpecifiedEuclidean(inst.getVals(), means[i]);
            if (dist < mdist) {
                clazz = i;
                mdist = dist;
            }
        }
        return clazz;
    }

    protected double getEuclidean(double[] fset, double[] avg) {
        double val = 0.0;
        double diff;
        for (int i=0;i<fset.length;i++) {
            diff = fset[i]-avg[i];
            val += diff*diff;
        }
        return Math.sqrt(val);
    }

    protected double getSpecifiedEuclidean(double[] fset, double[] avg) {
        double val = 0.0;
        double diff;
        for (int i=0;i<useFeatures.length;i++) {
            diff = fset[useFeatures[i]] - avg[useFeatures[i]];
            val += diff*diff;
        }
        return Math.sqrt(val);
    }


}
