/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.ImageGraph;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 *
 * @author david
 */
public class Histogram {
    
    protected int[] hist;

    public Histogram(BufferedImage img) {
        initHists();
        processImage(img);
    }

    public Histogram(ImageGraph graph) {
        initHists();
        processGraph(graph);
    }

    private void initHists() {
        hist = new int[256];
    }

    private void processGraph(ImageGraph graph) {
        LinkedList<Integer>[] edges = graph.getEdges();
        int[] vertices = graph.getVertices();
        int src = graph.getSrc();
        int sink = graph.getSink();
        for (int i=0;i<vertices.length;i++) {
            if (i != src && i != sink) {
                if (edges[i] != null) {
                    hist[vertices[i]] += 1;
                }
            }
        }
    }

    private void processImage(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int pixel = 0;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                pixel = img.getRGB(i,j);
                if (isVisible(pixel)) {
                    hist[getGrayVal(pixel)] += 1;
                }
            }
        }
    }

    private boolean isVisible(int pixel) {
        return ((pixel & 0xFF000000) >> 24) != 0;
    }

    private int getGrayVal(int pixel) {
        int red = (pixel & 0x00FF0000) >> 16;
        int green = (pixel & 0x0000FF00) >> 8;
        int blue = (pixel & 0x000000FF);
        return (red+green+blue)/3;
    }

    private int getTotalCount() {
        int count = 0;
        for (int i=0;i<hist.length;i++) {
            count = count + hist[i];
        }
        return count;
    }

    public int[] getHistogram() {
        return hist;
    }

    public double[] getProbHistogram() {
        double[] phist = new double[hist.length];
        double total = (double) getTotalCount();
        for (int i=0;i<phist.length;i++) {
            phist[i] = ((double)hist[i])/total;
        }
        return phist;
    }

    public int[] getCumulativeHistogram() {
        int[] chist = new int[hist.length];
        int sum = 0;
        for (int i=0;i<chist.length;i++) {
            sum = sum + hist[i];
            chist[i] = sum;
        }
        return chist;
    }

    public double[] getCumProbHistogram() {
        double[] phist = getProbHistogram();
        double[] cphist = new double[phist.length];
        double sum = 0.0;
        for (int i=0;i<cphist.length;i++) {
            sum = sum + phist[i];
            cphist[i] = sum;
        }
        return cphist;
    }
}
