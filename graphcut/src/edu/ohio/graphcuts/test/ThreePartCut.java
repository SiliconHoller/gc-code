/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import edu.ohio.graphcuts.alg.GraphCut;
import edu.ohio.graphcuts.analysis.KMeans;
import edu.ohio.graphcuts.analysis.Statistics;
import edu.ohio.graphcuts.analysis.XYGraySquaredKMeans;
import edu.ohio.graphcuts.multi.MLabelImageGraph;
import edu.ohio.graphcuts.multi.MLabelImageOps;
import edu.ohio.graphcuts.multi.MLabelMstCut;
import edu.ohio.graphcuts.util.ArrayOps;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class ThreePartCut {

    
    private static void fillEdges(MLabelImageGraph graph, Statistics stats, KMeans kmeans) {
        System.out.println("\tGetting groupings...");
        double[][] avgs = kmeans.getAverages();
        int[] labels = graph.getLabels();
        if (labels.length != avgs.length) throw new RuntimeException("Unequal # of classes...");

        int src = 0;
        int pt = 0;
        double val = 0.0;
        double dist2 = 0.0;
        double sig2 = 0.0;
        double[] avg = new double[3];
        double[] point = new double[3];
        double[] u = new double[3];
        double[] v = new double[3];
        int[] verts = graph.getVertices();
        //First, fill in the label values
        System.out.println("\tFilling in label values...");
        for (int i=0;i<labels.length;i++) {
            System.out.println("\t\tProcessing group "+i+"...");
            src = labels[i];
            avg = avgs[i];
            sig2 = stats.getSigmaSquared(i);
            System.out.print("\t\t["+i+"]:  ");
            ArrayOps.printArray(avg);
            System.out.println("\t\tSigma-2 = "+sig2);
            for (int j=0;j<verts.length;j++) {
                point[0] = graph.getX(j);
                point[1] = graph.getY(j);
                point[2] = verts[j];
                dist2 = stats.euclidianDistSquared(point, avg);
                val = Math.exp(-0.5*dist2/sig2);
                graph.setCapacity(src, j, val);
                graph.setCapacity(j,src,val);
            }
        }

        System.out.println("\tFilling in internal edges...");
        avg = stats.getFeatureAverage();
        sig2 = stats.getFeatureSigmaSquared();
        System.out.print("\t\tAvg Featues is ");
        ArrayOps.printArray(avg);
        System.out.println("\t\tSigma-2 is "+sig2);
        LinkedList<Integer>[] edges = graph.getEdges();
        for (int k=0;k<edges.length;k++) {
            if (edges[k] != null && !graph.isLabel(k)) {
                u[0] = graph.getX(k);
                u[1] = graph.getY(k);
                u[2] = verts[k];
                Iterator<Integer> it = edges[k].iterator();
                while (it.hasNext()) {
                    pt = it.next();
                    if (!graph.isLabel(pt)) {
                        v[0] = graph.getX(pt);
                        v[1] = graph.getY(pt);
                        v[2] = verts[pt];
                        dist2 = stats.euclidianDistSquared(u, v);
                        val = Math.exp(-0.5*dist2/sig2);
                        graph.setCapacity(k, pt, val);
                    }
                }
            }
        }

        System.out.println("\tFinished with filling edge capacities...");

    }
    
    
    public static BufferedImage createClassImage(int clazz, KMeans kmeans, int w, int h, MLabelImageOps mops) {
        BufferedImage img = mops.blankImage(w, h);
        int[] classes = kmeans.getClassifications();
        double[][] features = kmeans.getFeatures();
        int pos = 0;
        int max = w*h;
        while (pos < max) {
            if (features[pos].length != 0) {
                if (classes[pos] == clazz) {
                    double[] f = features[pos];
                    img.setRGB((int)f[0], (int)f[1], mops.makeGrayPixel((int)f[2]));
                }
            }
            pos++;
        }
        return img;
    }

    public static void main(String[] args) {
        try {
            String filename = "threefield.png";
            System.out.println("Using image file:  " + filename);

            BufferedImage img = ImageIO.read(new File(filename));
            int w = img.getWidth();
            int h = img.getHeight();
            System.out.println("Image width = "+img.getWidth());
            System.out.println("Image height = "+img.getHeight());
            /**
            System.out.println("Analyzing...");
            KMeans kmeans = new XYGraySquaredKMeans(img,3);
            kmeans.analyze();
            int count = kmeans.getCount();
            System.out.println("Done in "+count+" iterations.");
            double[][] avg = kmeans.getAverages();

            //Cheating here:
            avg[0][0] = 16.0;
            avg[0][1] = 10.0;
            avg[0][2] = 255;
            avg[1][0] = 10;
            avg[1][1] = 20;
            avg[1][2] = 127;
            avg[2][0] = 20;
            avg[2][1] = 20;
            avg[2][2] = 0.0;


            System.out.println("Averages found:");
            for (int i=0;i<avg.length;i++) {
                System.out.print("["+i+"]:  ");
                ArrayOps.printArray(avg[i]);
            }

            System.out.println("Creating KMeans resultant images...");
            MLabelImageOps mops = new MLabelImageOps();
            for (int km=0;km<avg.length;km++) {
                BufferedImage kimg = createClassImage(km, kmeans, w, h, mops);
                String kname = filename+"-k"+Integer.toString(km)+".png";
                System.out.println("\tSaving "+kname);
                ImageIO.write(kimg,"png",new File(kname));
            }
            System.out.println("Creating Statistics object...");
            Statistics stats = new Statistics(kmeans);
            ThreePartCut.fillEdges(graph, stats, kmeans);
             **/

            MLabelImageOps mops = new MLabelImageOps();
            System.out.println("Creating MLabelImageGraph with 3 groups...");
            MLabelImageGraph graph = mops.get4NGraph(img, 3);
            System.out.println("Filling edges...");
            int[] labels = graph.getLabels();

            
            
            
            double[] iavg = {0.0,127.0,255.0};
            double[] sig2 = {25.0,25.0,25.0};
            double imgavg = 127.0;
            double imgsig2 = 1000.0;

            //Cheat because we're having problems with the get pix value function.
            int[] verts = graph.getVertices();
            for (int p=0;p<verts.length;p++) {
                if (verts[p] >=100 && verts[p] <= 200) verts[p] = 127;
            }
            
            
            
            for (int i=0;i<labels.length;i++) {
                fillLabelEdges(graph, labels[i], iavg[i],sig2[i]);
            }

            fillInternalEdges(graph, imgsig2);

            

            System.out.println("Performing GraphCut...");
            GraphCut gc = new MLabelMstCut(graph);
            gc.maxFlow();

            System.out.println("Saving resultant images...");
            
            for (int m=0;m<labels.length;m++) {
                BufferedImage limg = mops.labelImage(labels[m], gc, graph);
                String sname = filename+"-"+Integer.toString(m)+".png";
                System.out.println("\tSaving "+sname+"...");
                ImageIO.write(limg, "png", new File(sname));
            }

            System.out.println("Done!");
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(ThreePartCut.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private static void fillLabelEdges(MLabelImageGraph graph, int label, double avg, double sigma2) {

        int[] verts = graph.getVertices();
        //First, fill in the label values
        //System.out.println("\tFilling in label values...");
        double i = 0.0;
        double diff = 0.0;
        double val = 0.0;
        for (int j=0;j<verts.length;j++) {
            i = verts[j];
            diff = avg - i;
            val = Math.exp(-0.5*diff*diff/sigma2);
            graph.setCapacity(label, j, val);
            graph.setCapacity(j,label,val);
        }

    }

    private static void fillInternalEdges(MLabelImageGraph graph, double imgsig2) {

        System.out.println("\tFilling in internal edges...");
        LinkedList<Integer>[] edges = graph.getEdges();
        double diff = 0.0;
        double val = 0.0;
        //double denom = 1.0/Math.sqrt(2.0*Math.PI*imgsig2);
        int[] verts = graph.getVertices();
        int pt = 0;
        for (int k=0;k<edges.length;k++) {
            if (edges[k] != null && !graph.isLabel(k)) {
                System.out.println("k = "+verts[k]);
                System.out.print("Neighbor values are ");
                Iterator<Integer> it = edges[k].iterator();
                while (it.hasNext()) {
                    pt = it.next();
                    System.out.print(" "+verts[pt]);
                    if (!graph.isLabel(pt)) {
                        diff = verts[k] - verts[pt];
                        val = (Math.exp(-0.5*diff*diff/imgsig2));
                        graph.setCapacity(k, pt, val);
                    }
                }
                System.out.println();
            }
        }

        System.out.println("\tFinished with filling edge capacities...");

    }

}
