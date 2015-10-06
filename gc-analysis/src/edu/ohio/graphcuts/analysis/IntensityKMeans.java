/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.NLabelImageGraph;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author david
 */
public class IntensityKMeans implements KMeans {

    protected int epoch = 10000;
    protected double epsilon = 0.003;
    protected int count = 0;
    protected double[][] features;
    protected double[][] averages;
    protected int[] classes;

    public IntensityKMeans(ImageGraph graph) {
        processGraph(graph);
    }

    public IntensityKMeans(NLabelImageGraph graph) {
        processGraph(graph);
    }

    private void processGraph(NLabelImageGraph graph) {
        int[] verts = graph.getVertices();
        //LinkedList<Integer>[] edges = graph.getEdges();
        int w = graph.getWidth();
        int h = graph.getHeight();
        int[] labels = graph.getLabels();

        features = new double[w*h][0];
        for (int i=0;i<features.length;i++) {
            LinkedList<Integer> edges = graph.getEdgesFrom(i);
            
            if (edges.size() > 0) {
                double[] f = new double[1];
                f[0] = verts[i];
                features[i] = f;
            }
            
        }

        //Even split xy-I across range.
        int numLabels = graph.getLabels().length;
        averages = new double[numLabels][1];
        double maxpos = (double)(w*h-1);
        double maxI = 255.0;
        for (int k=0;k < numLabels;k++) {
            double proportion = ((double)k/(double)numLabels);
            double pos = proportion*maxpos;
            averages[k][0] = pos;
        }
        classes = new int[features.length];
        Arrays.fill(classes, -1);
    }

    private void processGraph(ImageGraph graph) {
        int[] verts = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();
        int w = graph.getWidth();
        int h = graph.getHeight();
        features = new double[w*h][0];
        for (int i=0;i<features.length;i++) {
            if (edges[i] != null) {
                if (edges[i].size() > 0) {
                    double[] f = new double[1];
                    f[0] = verts[i];
                    features[i] = f;
                }
            }
        }

        //Two-grouping split high and low
        averages = new double[2][1];
        averages[0][0] = 0;
        averages[1][0] = 255;

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
        return 1;
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
            if (features[i].length > 0) {
                classes[i] = min(features[i]);
            }
        }
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

    protected double adjust() {
        double diff = 0.0;
        for (int i=0;i<averages.length;i++) {
            double[] avg = new double[1];
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

}
