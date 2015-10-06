/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import edu.ohio.graphcuts.ArrayOps;
import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.analysis.KMeans;
import edu.ohio.graphcuts.analysis.Statistics;
import edu.ohio.graphcuts.analysis.XYRGBKMeans;
import edu.ohio.graphcuts.util.PngOps;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class KMeansColorTest {

    private static BufferedImage getImageFor(KMeans kmeans, int clazz, ImageGraph graph) {
        PngOps ops = new PngOps();
        BufferedImage img = ops.blankImage(graph.getWidth(), graph.getHeight());
        int[] verts = graph.getVertices();
        int[] clazzes = kmeans.getClassifications();
        for (int i=0;i<clazzes.length;i++) {
            if (clazzes[i] == clazz) {
                img.setRGB(graph.getX(i), graph.getY(i), verts[i]);
            }
        }
        return img;
    }

    public static void main(String[] args) {
        try {
            //load the image in quesitons
            String file = args[0];
            System.out.println("Original image is "+file);
            File imgFile = new File(file);
            System.out.println("Loading original image...");
            BufferedImage img = ImageIO.read(imgFile);

            System.out.println("Image Dimensions:  "+img.getWidth()+" X "+img.getHeight()+" pixels.");

            System.out.println("Creating ImageGraph object...");
            PngOps pops = new PngOps();
            ImageGraph graph = pops.getColor4NGraph(img);

            System.out.println("Performing k-means analysis..");
            KMeans kmeans = new XYRGBKMeans(graph);
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

            System.out.println("Creating split images...");
            System.out.println("\tCreating 0-group image...");
            BufferedImage imgzero = getImageFor(kmeans,0,graph);
            System.out.println("\tCreating 1-group image...");
            BufferedImage imgone = getImageFor(kmeans,1,graph);
            System.out.println("\tsaving...");
            String zerofile = file +"-0.png";
            String onefile = file + "-1.png";
            ImageIO.write(imgzero, "png", new File(zerofile));
            ImageIO.write(imgone,"png",new File(onefile));
            System.out.println("...done.");

        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(KMeansColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
