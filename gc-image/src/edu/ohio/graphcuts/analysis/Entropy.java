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
public class Entropy {

    public Entropy() {
        //empty constructor for convenience
    }

    public double getValueEntropy(Histogram hist) {
        return getEntropy(hist.getProbHistogram());
    }

    public double getPositionEntropy(ImageGraph graph) {
        int w = graph.getWidth();
        int h = graph.getHeight();
        LinkedList<Integer>[] edges = graph.getEdges();
        int pos;
        int[][] hist = new int[w][h];
        double[] xsums = new double[w];
        double[] ysums = new double[h];
        int count = 0;
        //Get 2-D histogram
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                LinkedList<Integer> elist = edges[graph.getPosition(i, j)];
                if (elist != null) {
                    xsums[i] += 1.0;
                    ysums[j] += 1.0;
                    count++;
                }
            }
        }
        double total = (double) count;
        //Turn into probability histograms
        for (int m=0;m<xsums.length;m++) {
            xsums[m] = xsums[m]/count;
        }
        for (int n=0;n<ysums.length;n++) {
            ysums[n] = ysums[n]/count;
        }
        double xentropy = getEntropy(xsums);
        double yentropy = getEntropy(ysums);
        return xentropy + yentropy;
    }

    public double getEntropy(double[] phist) {
        double entropy = 0.0;
        double log2 = Math.log(2.0);
        double p;
        for (int i=0;i<phist.length;i++) {
            p = phist[i];
            if (p != 0.0) {
                entropy = entropy + (p*Math.log(p)/log2);
            }
        }
        //Change the sign
        entropy *= -1.0;
        return entropy;
    }
}
