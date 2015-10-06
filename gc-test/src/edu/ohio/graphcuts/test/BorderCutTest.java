/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import edu.ohio.graphcuts.ArrayOps;
import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.NLabelImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import edu.ohio.graphcuts.alg.NLabelBorderCut;
import edu.ohio.graphcuts.analysis.IntensityKMeans;
import edu.ohio.graphcuts.analysis.KMeans;
import edu.ohio.graphcuts.analysis.Statistics;
import edu.ohio.graphcuts.util.NLabelImageOps;
import edu.ohio.graphcuts.util.PngOps;
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
public class BorderCutTest {

    
    private static void fillEdges(NLabelImageGraph graph, Statistics stats, KMeans kmeans) {
        System.out.println("\tGetting groupings...");
        double[][] avgs = kmeans.getAverages();
        int[] labels = graph.getLabels();
        if (labels.length != avgs.length) throw new RuntimeException("Unequal # of classes...");

        int src = 0;
        int pt = 0;
        double val = 0.0;
        double dist2 = 0.0;
        double sig2 = 0.0;
        double[] avg = new double[1];
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
                double diff = avg[0] - verts[j];
                dist2 = diff*diff;
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
        //LinkedList<Integer>[] edges = graph.getEdges();
        for (int k=0;k<verts.length;k++) {
            if (!graph.isLabel(k)) {
                u[0] = graph.getX(k);
                u[1] = graph.getY(k);
                u[2] = verts[k];
                Iterator<Integer> it = graph.getEdgesFrom(k).iterator();
                while (it.hasNext()) {
                    pt = it.next();
                    if (!graph.isLabel(pt)) {
                        v[0] = graph.getX(pt);
                        v[1] = graph.getY(pt);
                        v[2] = verts[pt];
                        double diff = avg[0] - verts[pt];
                        dist2 = diff*diff;
                        val = Math.exp(-0.5*dist2/sig2);
                        graph.setCapacity(k, pt, val);
                    }
                }
            }
        }

        System.out.println("\tFinished with filling edge capacities...");

    }
    
    
    public static BufferedImage createClassImage(int clazz, KMeans kmeans, int w, int h, NLabelImageOps mops) {
        BufferedImage img = mops.blankImage(w, h);
        int[] classes = kmeans.getClassifications();
        double[][] features = kmeans.getFeatures();
        int pos = 0;
        int max = w*h;
        while (pos < max) {
            if (features[pos].length != 0) {
                if (classes[pos] == clazz) {
                    img.setRGB((pos%w), (pos/w), mops.makeGrayPixel((int)features[pos][0]));
                }
            }
            pos++;
        }
        return img;
    }

    public static void main(String[] args) {
        try {
            String filename = args[0];
            System.out.println("Using image file:  " + filename);

            BufferedImage img = ImageIO.read(new File(filename));
            int w = img.getWidth();
            int h = img.getHeight();
            System.out.println("Image width = "+img.getWidth());
            System.out.println("Image height = "+img.getHeight());
            NLabelImageOps mops = new NLabelImageOps();
            PngOps ops = new PngOps();
            ImageGraph agraph = ops.get4NGraph(img);
            System.out.println("Analyzing...");
            KMeans kmeans = new IntensityKMeans(agraph);
            kmeans.analyze();
            int count = kmeans.getCount();
            System.out.println("Done in "+count+" iterations.");
            double[][] avg = kmeans.getAverages();


            System.out.println("Averages found:");
            for (int i=0;i<avg.length;i++) {
                System.out.print("["+i+"]:  ");
                ArrayOps.printArray(avg[i]);
            }

            System.out.println("Creating KMeans resultant images...");
            for (int km=0;km<avg.length;km++) {
                BufferedImage kimg = createClassImage(km, kmeans, w, h, mops);
                String kname = filename+"-k"+Integer.toString(km)+".png";
                System.out.println("\tSaving "+kname);
                ImageIO.write(kimg,"png",new File(kname));
            }
            System.out.println("Creating Statistics object...");
            Statistics stats = new Statistics(kmeans);


            System.out.println("Creating MLabelImageGraph with 2 groups...");
            NLabelImageGraph graph = mops.get4NGraph(img, 2);
            System.out.println("Filling edges...");
            int[] labels = graph.getLabels();


            fillEdges(graph, stats, kmeans);

            System.out.println("Performing GraphCut...");
            GraphCut gc = new NLabelBorderCut(graph);
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
            Logger.getLogger(BorderCutTest.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private static void fillLabelEdges(NLabelImageGraph graph, int label, double avg, double sigma2) {

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

    private static void fillInternalEdges(NLabelImageGraph graph, double imgsig2) {

        System.out.println("\tFilling in internal edges...");
        
        double diff = 0.0;
        double val = 0.0;
        //double denom = 1.0/Math.sqrt(2.0*Math.PI*imgsig2);
        int[] verts = graph.getVertices();
        int pt = 0;
        for (int k=0;k<verts.length;k++) {
            if (!graph.isLabel(k)) {
                System.out.println("k = "+verts[k]);
                System.out.print("Neighbor values are ");
                Iterator<Integer> it = graph.getEdgesFrom(k).iterator();
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
