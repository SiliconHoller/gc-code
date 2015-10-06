/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import edu.ohio.graphcuts.ArrayOps;
import edu.ohio.graphcuts.NLabelImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import edu.ohio.graphcuts.alg.NLabelBorderCut;
import edu.ohio.graphcuts.alg.UnionFind;
import edu.ohio.graphcuts.analysis.IntensityKMeans;
import edu.ohio.graphcuts.analysis.KMeans;
import edu.ohio.graphcuts.analysis.RBFCapacityFill;
import edu.ohio.graphcuts.analysis.Statistics;
import edu.ohio.graphcuts.analysis.XYGrayKMeans;
import edu.ohio.graphcuts.analysis.XYGraySquaredKMeans;
import edu.ohio.graphcuts.util.NLabelImageOps;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class ThreePartCut {   
    
    public static BufferedImage createClassImage(int clazz, NLabelImageGraph graph, KMeans kmeans, int w, int h, NLabelImageOps mops) {
        BufferedImage img = mops.blankImage(w, h);
        int[] classes = kmeans.getClassifications();
        int[] verts = graph.getVertices();
        double[][] features = kmeans.getFeatures();
        int pos = 0;
        int max = w*h;
        while (pos < max) {
            if (features[pos].length != 0) {
                if (classes[pos] == clazz) {
                    img.setRGB(graph.getX(pos), graph.getY(pos), mops.makeGrayPixel(verts[pos]));
                }
            }
            pos++;
        }
        return img;
    }

    public static BufferedImage createLabelImage(int label, GraphCut gc, NLabelImageGraph graph) {
        BufferedImage img = new BufferedImage(graph.getWidth(), graph.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int[] verts = graph.getVertices();

        Color c;
        for (int i=0;i<verts.length;i++) {
            if (! graph.isLabel(i) && gc.inLabel(label, i)) {
                c = new Color(verts[i], verts[i], verts[i]);
                img.setRGB(graph.getX(i), graph.getY(i), c.getRGB());
            }
        }
        return img;
    }

    public static void main(String[] args) {
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            System.out.println("Enter the path to image file:");
            String filename = br.readLine();            
            System.out.println("Using image file:  " + filename);

            BufferedImage img = ImageIO.read(new File(filename));
            int w = img.getWidth();
            int h = img.getHeight();
            System.out.println("Image width = "+img.getWidth());
            System.out.println("Image height = "+img.getHeight());
        
            NLabelImageOps mops = new NLabelImageOps();
            System.out.println("Creating MLabelImageGraph with 3 groups...");
            NLabelImageGraph graph = mops.get4NGraph(img, 3);
            System.out.println("Filling edges...");
            int[] labels = graph.getLabels();

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

            System.out.println("Creating kmeans images...");
            for (int j=0;j<labels.length;j++) {
                BufferedImage kimg = createClassImage(j,graph, kmeans,w,h,mops);
                ImageIO.write(kimg, "png", new File("k"+Integer.toString(j)+".png"));
            }

            Statistics stats = new Statistics(kmeans);

            System.out.println("Image grouping stats:");
            for (int i=0;i<labels.length;i++) {
                System.out.println("\tGroup #"+i+" sigma^2 = "+stats.getSigmaSquared(i));
            }

            System.out.println("\tImage sigma^2 = "+Statistics.getImageSigmaSquared(graph));

            System.out.println("Filling edges...");
            RBFCapacityFill rbf = new RBFCapacityFill(graph, kmeans, stats);
            rbf.fillCapacities();
            System.out.println("Finished filling capacities.");

            GraphCut gc = new NLabelBorderCut(graph);
            System.out.println("Starting cut:");
            double flow = gc.maxFlow();
            System.out.println("Max flow is "+flow);

            System.out.println("Creating label images...");
            for (int k=0;k<labels.length;k++) {
                BufferedImage limg = createLabelImage(labels[k], gc, graph);
                ImageIO.write(limg, "png", new File("l"+Integer.toString(k)+".png"));
            }

            UnionFind uf = ((NLabelBorderCut)gc).getUnionFind();
            int[] verts = graph.getVertices();
            System.out.println("node\troot");
            for (int m=0;m<verts.length;m++) {
                System.out.println(m+"\t"+uf.find(m));
            }
            System.out.println("done...");
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(ThreePartCut.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


}
