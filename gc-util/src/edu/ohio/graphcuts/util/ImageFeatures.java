/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util;

import edu.ohio.graphcuts.ImageGraph;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
/**
 * Produces a basic X-Y-gray val array of the features
 * @author david
 */
public class ImageFeatures {

    protected ImageGraph graph;

    public ImageFeatures(ImageGraph graph) {
        this.graph = graph;
    }


    /*
     * Returns an array of the features extracted into
     * the form f[i][0] = x, f[i][1] = y, f[i][2] = gray intensity
     */
    public double[][] getXYGrayArray() {
        int[] vertices = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();
        int src = graph.getSrc();
        double[][] features = new double[src][3];
        for (int i=0;i<src;i++) {
            if (edges[i] != null) {
                double[] f = new double[3];
                f[0] = graph.getX(i);
                f[1] = graph.getY(i);
                f[2] = vertices[i];
                features[i] = f;
            }
        }

        return features;
    }

    public double[][] getXYRGBArray() {
        int[] vertices = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();
        int src = graph.getSrc();
        PngOps pngops = new PngOps();
        double[][] features = new double[src][0];
        for (int i=0;i<src;i++) {
            if (edges[i] != null) {
                double[] f = new double[5];
                f[0] = graph.getX(i);
                f[1] = graph.getY(i);
                f[2] = pngops.getRedVal(vertices[i]);
                f[3] = pngops.getGreenVal(vertices[i]);
                f[4] = pngops.getBlueVal(vertices[i]);
                features[i] = f;
            }
        }

        return features;
    }

    public static double[][] getXYGrayFeatures(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int len = w*h;
        PngOps pngops = new PngOps();
        double[][] features = new double[len][3];
        int pos = 0;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                pos = w*j+i;
                features[pos][0] = i;
                features[pos][1] = j;
                features[pos][2] = pngops.getGrayVal(img.getRGB(i,j));
            }
        }
        return features;
    }

}
