/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.BKTreeCut;
import edu.ohio.graphcuts.alg.GraphCut;
import edu.ohio.graphcuts.analysis.Cuts;
import edu.ohio.graphcuts.analysis.IntensityKMeans;
import edu.ohio.graphcuts.analysis.KMeans;
import edu.ohio.graphcuts.analysis.MarkovCRF;
import edu.ohio.graphcuts.analysis.Statistics;
import edu.ohio.graphcuts.data.PngOps;
import edu.ohio.graphcuts.util.ArrayOps;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class KMeansMarkovCut {

    public static void fillSourceEdges(ImageGraph graph, double[] avg, double sigma2) {
        int src = graph.getSrc();
        LinkedList<Integer>[] edges = graph.getEdges();
        int[] verts = graph.getVertices();
        LinkedList<Integer> snodes = edges[src];
        double valdiff, diff2;
        if (snodes != null) {
            for (int pos: snodes) {


                valdiff = avg[0] - (double)verts[pos];
                diff2 = valdiff*valdiff;
                graph.setCapacity(src,pos,Math.exp(-0.5*diff2/sigma2));
            }
        }
    }

    public static void fillSinkEdges(ImageGraph graph, double[] avg, double sigma2) {
        int sink = graph.getSink();
        LinkedList<Integer>[] edges = graph.getEdges();
        int[] verts = graph.getVertices();
        double valdiff,diff2;
        for (int i=0;i<sink;i++) {
            LinkedList<Integer> oe = edges[i];
            if (oe != null) {
                for (int j: oe) {
                    if (j == sink) {
                        valdiff = avg[0] - (double) verts[i];
                        diff2 = valdiff*valdiff;
                        graph.setCapacity(i,j,Math.exp(-0.5*diff2/sigma2));
                    }
                }
            }
        }
    }

    public static void fillImageEdges(ImageGraph graph) {
       MarkovCRF.fillPosteriorCapacities(graph);
    }

    public static void cutAndResults(ImageGraph graph, GraphCut gc, String file, String type) throws IOException {

        gc.maxFlow();

        Cuts cuts = new Cuts(graph,gc);

        PngOps pngops = new PngOps();
        int w = graph.getWidth();
        int h = graph.getHeight();

        BufferedImage cutImg = pngops.cutImage(w, h, cuts.getCuts());
        BufferedImage srcImg = pngops.srcImage(w, h, gc, graph);
        BufferedImage sinkImg = pngops.sinkImage(w, h, gc, graph);

        File cutsFile = new File(file+"MC.png");
        File srcresultFile = new File(file+"MS.png");
        File sinkresultFile = new File(file+"MT.png");
        ImageIO.write(cutImg,"png",cutsFile);
        ImageIO.write(srcImg,"png",srcresultFile);
        ImageIO.write(sinkImg,"png",sinkresultFile);


    }


    public static void main(String[] args) {
        try {
            //load the image in quesitons
            String file = args[0];
            System.out.println("Original image is "+file);
            File imgFile = new File(file);
            System.out.println("Loading original image...");
            BufferedImage img = ImageIO.read(imgFile);
            
            //Create grayscale image for use in the program
            //System.out.println("Creating grayscale image...");
            //BufferedImage gimg = PngOps.makeGrayScaleImage(img);
            //Store the gray image
            //String grayFile = file + "-gray.png";
            //System.out.println("Storing grayscale image as "+grayFile);
            //ImageIO.write(gimg, "png", new File(grayFile));

            System.out.println("Image Dimensions:  "+img.getWidth()+" X "+img.getHeight()+" pixels.");

            //System.out.println("Extracting image features...");
            //double[][] features = ImageFeatures.getXYGrayFeatures(img);

            System.out.println("Creating ImageGraph object...");
            PngOps pops = new PngOps();
            ImageGraph graph = pops.get4NGraph(img);

            System.out.println("Performing k-means analysis..");
            KMeans kmeans = new IntensityKMeans(graph);
            boolean analyzed = kmeans.analyze();
            if (!analyzed) {
                System.out.println("Analysis did not reach convergence!");
            }
            System.out.println("K-Means epoch = "+kmeans.getEpoch());
            System.out.println("K-Means iteration count = "+kmeans.getCount());

            System.out.println("");
            System.out.println("K-Means averages:");
            double[][] kavgs = kmeans.getAverages();
            for (int k=0;k<kavgs.length;k++) {
                System.out.print("Grouping "+k+":  ");
                ArrayOps.printArray(kavgs[k]);
            }



            System.out.println("Getting image statistics...");
            Statistics stats = new Statistics(kmeans);
            double srcSigma2 = stats.getSigmaSquared(0);
            System.out.println("\tSrc sigma squared = "+srcSigma2);
            double sinkSigma2 = stats.getSigmaSquared(1);
            System.out.println("\tSink sigma squared = "+sinkSigma2);
            double imgavg = Statistics.getAverage(graph);
            System.out.println("\tImage average I = "+imgavg);
            double imgSigma2 = Statistics.getSigmaSquared(graph);
            System.out.println("\tIntensity sigma squared = "+imgSigma2);

            System.out.println("Filling capacities...");
            double[][] caps = graph.getNewFlows();
            graph.setCapacities(caps);
            System.out.println("\tFilling internal edges...");
            KMeansMarkovCut.fillImageEdges(graph);
            System.out.println("\tFilling Source edges...");
            KMeansMarkovCut.fillSourceEdges(graph,kavgs[0],srcSigma2);
            System.out.println("\tFilling Sink edges...");
            KMeansMarkovCut.fillSinkEdges(graph,kavgs[1],sinkSigma2);

            //System.out.println("Copying capacities...");
            //double[][] ffcaps = ArrayOps.cloneArray(caps);
            //double[][] ekcaps = ArrayOps.cloneArray(caps);
            //double[][] bkcaps = ArrayOps.cloneArray(caps);

            //System.out.println("Starting Ford-Fulkerson cut...");
            //graph.setCapacities(ffcaps);
            //FordFulkerson ff = new FordFulkerson(graph);
            //KMeansCut.cutAndResults(graph, ff, file, "ff");

            //System.out.println("Staring Edmonds-Karp cut...");
            //graph.setCapacities(ekcaps);
            //EdmondsKarp ek = new EdmondsKarp(graph);
            //KMeansCut.cutAndResults(graph,ek,file,"ek");

            System.out.println("Starting BK cut...");
            //graph.setCapacities(bkcaps);
            BKTreeCut bk = new BKTreeCut(graph);
            KMeansCut.cutAndResults(graph,bk,file,"bk");

        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(KMeansCut.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
