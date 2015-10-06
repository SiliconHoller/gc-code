/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.util.PngOps;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author david
 */
public class XYRGBKMeans implements KMeans {

    protected double[][] averages;
    protected double[][] features;
    protected int[] classes;
    protected int numFeatures;
    protected double epsilon = 0.003;
    protected int epoch = 10000;
    protected int count;

    public XYRGBKMeans(ImageGraph graph) {
        processGraph(graph);
    }

    /*
     * Basic 2-class un-initialized k-means classification
     *
     */
    protected void processGraph(ImageGraph graph) {
        numFeatures = 5;
        int[] verts = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();
        int w = graph.getWidth();
        int h = graph.getHeight();
        features = new double[w*h][0];
        PngOps ops = new PngOps();
        for (int i=0;i<features.length;i++) {
            if (edges[i] != null) {
                double[] f = new double[numFeatures];
                f[0] = graph.getX(i);
                f[1] = graph.getY(i);
                f[2] = ops.getRedVal(verts[i]);
                f[3] = ops.getGreenVal(verts[i]);
                f[4] = ops.getBlueVal(verts[i]);
                features[i] = f;
            }
        }

        //Two-grouping split high and low
        averages = new double[2][5];
        averages[0][0] = 0;
        averages[0][1] = 0;
        averages[0][2] = 0;
        averages[0][3] = 0;
        averages[0][4]= 0;
        averages[1][0] = w*h-1;
        averages[1][1] = w*h-1;
        averages[1][2] = 255;
        averages[1][3] = 255;
        averages[1][4] = 255;

        classes = new int[features.length];
        Arrays.fill(classes, -1);
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
        squareVals();
        while (diff > epsilon && count < epoch) {
            classify();
            diff = adjust();
            count++;
        }
        unSquareVals();
        if (count < epoch) retVal = true;
        return retVal;
    }

    protected void squareVals() {
        double tmp = 0.0;
        //Square the intensity values of the features
        
        for (int i=0;i<features.length;i++) {
            if (features[i].length != 0) {
                tmp = features[i][2];
                features[i][2] = tmp*tmp;
                tmp = features[i][3];
                features[i][3] = tmp*tmp;
                tmp = features[i][4];
                features[i][4] = tmp*tmp;
            }
        }
        //square the averages intensity value
        //Should have no effect if no hints were passed
        for (int j=0;j<averages.length;j++) {
            tmp = averages[j][2];
            averages[j][2] = tmp*tmp;
            tmp = averages[j][3];
            averages[j][3] = tmp*tmp;
            tmp = averages[j][4];
            averages[j][4] = tmp*tmp;
        }
    }

    protected void unSquareVals() {
        double tmp = 0.0;
        //unsquare the feature values
        
        for (int i=0;i<features.length;i++) {
            tmp = features[i][2];
            features[i][2] = Math.sqrt(tmp);
            tmp = features[i][3];
            features[i][3] = Math.sqrt(tmp);
            tmp = features[i][4];
            features[i][4] = Math.sqrt(tmp);
        }
        //unsqaure the averages intesity values
        for (int j=0;j<averages.length;j++) {
            tmp = averages[j][2];
            averages[j][2] = Math.sqrt(tmp);
            tmp = averages[j][3];
            averages[j][3] = Math.sqrt(tmp);
            tmp = averages[j][4];
            averages[j][4] = Math.sqrt(tmp);
        }
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
