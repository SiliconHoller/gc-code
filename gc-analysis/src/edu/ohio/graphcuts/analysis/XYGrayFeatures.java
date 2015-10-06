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
public class XYGrayFeatures implements FeatureExtraction {

    protected ImageGraph graph;
    protected int numFeatures = 3;

    public XYGrayFeatures(ImageGraph graph) {
        this.graph = graph;
    }

    public int numFeatures() {
        return numFeatures;
    }

    public double[][] getFeatures() {
        int w = graph.getWidth();
        int h = graph.getHeight();
        int len = w*h;
        double[][] features = new double[len][0];
        int[] v = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();

        int pos = 0;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                pos = w*j+i;
                if (edges[pos] != null) {
                    if (edges[pos].size() > 0) {
                        double[] f = new double[3];
                        f[0] = i;
                        f[1] = j;
                        f[2] = v[pos];
                        features[pos] = f;
                    }
                }
            }
        }
        return features;
    }

}
