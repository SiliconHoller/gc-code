/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.NLabelImageGraph;
import java.util.LinkedList;

/**
 *  Fills the edge capacities of the given graph
 *  using a Radial Basis Function (normal curve) and
 *  the initialized Statistics and KMeans data.
 *
 * @author david
 */
public class RBFCapacityFill {

    ImageGraph graph = null;
    NLabelImageGraph ngraph = null;
    KMeans kmeans;
    Statistics stats;


    public RBFCapacityFill(ImageGraph graph, KMeans kmeans, Statistics stats) {
        this.graph = graph;
        this.kmeans = kmeans;
        this.stats = stats;
    }

    public RBFCapacityFill(NLabelImageGraph ngraph, KMeans kmeans, Statistics stats) {
        this.ngraph = ngraph;
        this.kmeans = kmeans;
        this.stats = stats;
    }

    public void fillCapacities() {
        if (graph != null) {
            fillSTCapacities();
        } else {
            fillNCapacities();
        }
    }

    private void fillNCapacities() {
        System.out.println("Filling NLabelImageGraph capacities...");
        int[] labels = ngraph.getLabels();
        double[] lsig2 = new double[labels.length];
        for (int j=0;j<lsig2.length;j++) {
            lsig2[j] = stats.getSigmaSquared(j);
            System.out.println("Label "+j+" sigma2 = "+lsig2[j]);
        }
        double imgsig2 = stats.getFeatureSigmaSquared();
        double[][] kavgs = kmeans.getAverages();
        fillNImageEdges(imgsig2);
        for (int i=0;i<labels.length;i++) {
            fillLabelEdges(labels[i],kavgs[i],lsig2[i]);
        }
    }

    private void fillLabelEdges(int lab, double[] avg, double lsig2) {
        System.out.println("Filling Label "+lab+" t-links...");
        System.out.println("lsigma = "+lsig2);
        LinkedList<Integer> le = ngraph.getEdgesFrom(lab);
        
        
        double diff2,fval;

        for (int i: le) {

            diff2 = stats.euclidianDistSquared(i, avg);
            //System.out.println("\tdiff2 = "+diff2);
            if (diff2 > 0) {
                fval = Math.exp(-0.5*diff2/lsig2);
                //System.out.println("\tfval = "+fval);
                ngraph.setCapacity(lab, i, fval);
                ngraph.setCapacity(i,lab, fval);
            }

        }
        
    }

    private void fillSTCapacities() {

        double srcSigma2 = stats.getSigmaSquared(0);
        double sinkSigma2 = stats.getSigmaSquared(1);

        double imgSigma2 = stats.getFeatureSigmaSquared();

        
        double[][] kavgs = kmeans.getAverages();

            
        fillImageEdges(imgSigma2);
        fillSourceEdges(kavgs[0],srcSigma2);
        fillSinkEdges(kavgs[1],sinkSigma2);

    }

    protected void fillSourceEdges(double[] avg, double sigma2) {
        int src = graph.getSrc();
        LinkedList<Integer>[] edges = graph.getEdges();
        if (edges[src] != null) {
            double diff2;
            LinkedList<Integer> se = edges[src];
            for (int i : se) {


                diff2 = stats.euclidianDistSquared(i, avg);
                if (diff2 > 0) {
                    graph.setCapacity(src,i, Math.exp(-0.5*diff2/sigma2));
                }
            }
        }
    }

    protected void fillSinkEdges(double[] avg, double sigma2) {
        int sink = graph.getSink();
        LinkedList<Integer>[] edges = graph.getEdges();
        int[] verts = graph.getVertices();

        double diff2;
        for (int i=0;i<verts.length;i++) {
            LinkedList<Integer> oe = edges[i];
            if (oe != null) {
                for (int j: oe) {
                    if (j == sink) {
                        diff2 = stats.euclidianDistSquared(i, avg);
                        if (diff2 > 0) {
                            graph.setCapacity(i,j, Math.exp(-0.5*diff2/sigma2));
                        }
                    }
                }
            }
        }
    }

    protected void fillImageEdges(double sigma2) {
       int src = graph.getSrc();
       int sink = graph.getSink();
       LinkedList<Integer>[] edges = graph.getEdges();

       double diff2;
       for (int i=0;i<edges.length;i++) {
           LinkedList<Integer> oe = edges[i];
           if (oe != null) {
               for (int j: oe) {
                   if (i != src && i != sink && j != src && j != sink) {
                       
                       diff2 = stats.euclidianDistSquared(i, j);
                       if (diff2 > 0) {
                            graph.setCapacity(i,j, Math.exp(-0.5*diff2/sigma2));
                       }
                   }
               }
           }
       }
    }

    protected void fillNImageEdges(double sigma2) {
        System.out.println("Filling NLabelImageGraph edges...");
       int[] verts = ngraph.getVertices();

       double diff2;
       double val;
       for (int i=0;i<verts.length;i++) {
           LinkedList<Integer> oe = ngraph.getEdgesFrom(i);
           if (oe != null) {
               for (int j: oe) {
                   if (!ngraph.isLabel(i) && !ngraph.isLabel(j)) {

                       diff2 = stats.euclidianDistSquared(i, j);
                       if (diff2 > 0) {
                           val = Math.exp(-0.5*diff2/sigma2);
                           //System.out.println("\tval = "+val);
                           ngraph.setCapacity(i,j, val);
                       }
                   }
               }
           }
       }
    }

}
