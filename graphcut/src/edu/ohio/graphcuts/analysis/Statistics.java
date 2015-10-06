/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.ImageGraph;
import java.util.LinkedList;

/**
 *
 * @author david
 */
public class Statistics {

    protected int[] classes;
    protected double[][] features;
    protected int flength;
    protected boolean kmeansBased = false;
    protected KMeans kmeans = null;

    public Statistics(int[] classes, double[][] features, int flength) {
        this.classes = classes;
        this.features = features;
        this.flength = flength;
    }

    public Statistics(KMeans kmeans) {
        this.kmeans = kmeans;
        this.classes = kmeans.getClassifications();
        this.features = kmeans.getFeatures();
        this.flength = kmeans.getNumFeatures();
        kmeansBased = true;
    }

    public double[] getFeatureAverage() {
        double[] fav = new double[flength];
        int count = getFeatureCount();
        if (count == 0) return new double[0];
        for (int i=0;i<features.length;i++) {
            if (features[i].length != 0) {
                for (int j=0;j<flength;j++) {
                    fav[j] = fav[j]+features[i][j];
                }
            }
        }

        for (int k=0;k<fav.length;k++) {
            fav[k] = fav[k]/(double)count;
        }

        return fav;
    }
    
    public int getFeatureCount() {
        int count = 0;
        for (int i=0;i<features.length;i++) {
            if (features[i].length != 0) {
                count++;
            }
        }
        return count;
    }

    public double getFeatureSigmaSquared() {
        double sigma2 = 0.0;
        int count = getFeatureCount();
        if (count == 0) return -1.0;
        double[] avg = getFeatureAverage();
        double[] devs = new double[count];
        int i = 0;
        int j = 0;
        while (i<count) {
            if (features[j].length != 0) {
                devs[i] = euclidianDistSquared(features[j],avg);
                i++;
            }
            j++;
        }
        double sum = 0.0;
        for (int k=0;k<devs.length;k++) {
            sum = sum + devs[k];
        }
        sigma2 = sum/(double)count;
        return sigma2;

    }

    public double[] getAverageFeatures(int clazz) {
        double[] avg = new double[flength];
        if (kmeansBased) {
            double[][] km = kmeans.getAverages();
            avg = km[clazz];
        } else {
            double[] sums = new double[flength];
            for (int i=0;i<classes.length;i++) {
                if (classes[i] == clazz) {
                    double[] f = features[i];
                    for (int k=0;k<sums.length;k++) {
                        sums[k] = sums[k] + f[k];
                    }

                }
            }
            int count = getCount(clazz);
            if (count != 0) {
                double total = (double) count;
                for (int m = 0;m<sums.length;m++) {
                    avg[m] = sums[m]/total;
                }
            }
        }
        return avg;
    }

    protected int getCount(int clazz) {
        int count = 0;
        if (kmeansBased) {
            count = kmeans.getAverages().length;
        } else {
            for (int i=0;i<classes.length;i++) {
                if (classes[i] == clazz) count++;
            }
        }
        return count;
    }

    public double getSigmaSquared(int clazz) {
        double sigma2 = 0.0;
        int count = getCount(clazz);
        double[] avg = getAverageFeatures(clazz);
        double[] devs = new double[count];
        int i = 0;
        int j = 0;
        while (i<count) {
            if (classes[j] == clazz) {
                devs[i] = euclidianDistSquared(features[j],avg);
                i++;
            }
            j++;
        }
        double sum = 0.0;
        for (int k=0;k<devs.length;k++) {
            sum = sum + devs[k];
        }
        sigma2 = sum/(double)count;
        return sigma2;
    }

    public double euclidianDistSquared(double[] f, double[] avg) {
        double dist = 0.0;
        double[] diffs = new double[f.length];
        //calculate the differences
        for (int i=0;i<diffs.length;i++) {
            diffs[i] = avg[i] - f[i];
        }

        for (int j=0;j<diffs.length;j++) {
            dist = dist + diffs[j]*diffs[j];
        }

        return dist;
    }

    public static double getAverage(ImageGraph graph) {
        double avg = 0.0;
        int[] verts = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();
        int sum = 0;
        int count = 0;
        int len = graph.getWidth()*graph.getHeight();
        for (int i=0;i<len;i++) {
            if (edges[i] != null) {
                sum += verts[i];
                count++;
            }
        }
        avg = ((double)sum)/((double) count);

        return avg;
    }

    public static double getSigmaSquared(ImageGraph graph) {
        double avg = getAverage(graph);
        int[] verts = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();
        int n=0;
        double sig2 = 0.0;
        int len = graph.getWidth()*graph.getHeight();
        double[] devs = new double[len];
        for (int i=0;i<len;i++) {
            if (edges[i] != null) {
                devs[i] = avg - (double)verts[i];
                n++;
            }
        }

        //Sum up the squared differences
        double sum = 0.0;
        double diff = 0.0;
        for (int j=0;j<len;j++) {
            diff = devs[j];
            sum += diff*diff;
        }
        sig2 = sum/(double)n;
        return sig2;
    }

}
