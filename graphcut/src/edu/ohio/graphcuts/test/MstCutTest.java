/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import edu.ohio.graphcuts.alg.MstCut;
import edu.ohio.graphcuts.analysis.Cuts;
import edu.ohio.graphcuts.analysis.IntensityKMeans;
import edu.ohio.graphcuts.analysis.KMeans;
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
public class MstCutTest {

    public static void fillSourceEdges(ImageGraph graph, double[] avg, double sigma2) {
        int src = graph.getSrc();
        LinkedList<Integer>[] edges = graph.getEdges();
        int[] verts = graph.getVertices();
        //LinkedList<Integer> snodes = edges[src];
        if (edges[src] != null) {
            double valdiff, diff2;
            int[][] index = graph.getEdgeIndex();
            double[][] caps = graph.getCapacities();
            int[] snodes = index[src];
            for (int i=0;i<snodes.length;i++) {


                valdiff = avg[0] - (double)verts[snodes[i]];
                diff2 = valdiff*valdiff;
                caps[src][i] = Math.exp(-0.5*diff2/sigma2);
            }
        }
    }

    public static void fillSinkEdges(ImageGraph graph, double[] avg, double sigma2) {
        int sink = graph.getSink();
        LinkedList<Integer>[] edges = graph.getEdges();
        int[] verts = graph.getVertices();

        double valdiff,diff2;
        for (int i=0;i<verts.length;i++) {
            LinkedList<Integer> oe = edges[i];
            if (oe != null) {
                for (int j: oe) {
                    if (j == sink) {
                        valdiff = avg[0] - (double) verts[i];
                        diff2 = valdiff*valdiff;
                        graph.setCapacity(i,j, Math.exp(-0.5*diff2/sigma2));
                    }
                }
            }
        }
    }

    public static void fillImageEdges(ImageGraph graph, double sigma2) {
       int src = graph.getSrc();
       int sink = graph.getSink();
       LinkedList<Integer>[] edges = graph.getEdges();
       int[] verts = graph.getVertices();
       int w = graph.getWidth();
       int h = graph.getHeight();

       double xdiff,ydiff,valdiff,diff2;
       for (int i=0;i<verts.length;i++) {
           System.out.println("Point value:  "+verts[i]);
           LinkedList<Integer> oe = edges[i];
           if (oe != null) {
               System.out.print("Neighbor values:  ");
               for (int j: oe) {
                   System.out.print(" "+verts[j]);
                   if (i != src && i != sink && j != src && j != sink) {
                       xdiff = (double)(graph.getX(i) - graph.getX(j));
                       ydiff = (double)(graph.getY(i) - graph.getY(j));
                       valdiff = (double)(verts[i] - verts[j]);
                       diff2 = xdiff*xdiff + ydiff*ydiff + valdiff*valdiff;
                       graph.setCapacity(i,j, Math.exp(-0.5*diff2/sigma2));
                   }
               }
               System.out.println();
           }
       }
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

        File cutsFile = new File(file+"C.png");
        File srcresultFile = new File(file+"S.png");
        File sinkresultFile = new File(file+"T.png");
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

            System.out.println("\tFilling internal edges...");
            MstCutTest.fillImageEdges(graph, imgSigma2);
            System.out.println("\tFilling Source edges...");
            MstCutTest.fillSourceEdges(graph,kavgs[0],srcSigma2);
            System.out.println("\tFilling Sink edges...");
            MstCutTest.fillSinkEdges(graph,kavgs[1],sinkSigma2);



            System.out.println("Starting normal BK cut...");
            MstCut mst = new MstCut(graph);
            MstCutTest.cutAndResults(graph,mst,file,"bk");

        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(MstCutTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
