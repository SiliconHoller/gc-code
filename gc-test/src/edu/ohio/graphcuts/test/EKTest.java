/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.EdmondsKarp;
import edu.ohio.graphcuts.util.PngOps;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class EKTest {

    public EKTest() {

    }

    public double[][] getCapacities(ImageGraph graph) {
        System.out.println("getCapacities()...");
        
        int[] points = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();
        int plength = graph.getWidth()*graph.getHeight();
        System.out.println("\tdata length is "+points.length);
        int src = graph.getSrc();
        int sink = graph.getSink();
        System.out.println("\tsrc = "+src);
        System.out.println("\tsink = "+sink);
        for (int i=0;i<plength;i++) {
            LinkedList<Integer> outedges = edges[i];
            if (outedges != null) {
                for (int j: outedges) {
                    //Simple gaussian intensity difference
                    int pval = 0;
                    if (j == sink) {
                        pval = 0;
                    } else {
                        pval = points[j];
                    }
                    double diff = (double)(points[i] - pval);
                    graph.setCapacity(i,j,Math.exp(-1.0*diff*diff/16384.0));
                    //System.out.println("\tcaps["+i+"]["+j+"] = "+caps[i][j]);
                }
            }
        }

        //now do the src edges
        LinkedList<Integer> sedges = edges[src];
        for (int k: sedges) {
            double diff = (double)(255 - points[k]);
            graph.setCapacity(src,k,Math.exp(-1.0*diff*diff/40000.0));
        }

        return graph.getCapacities();
    }

    public BufferedImage srcImage(int w, int h, EdmondsKarp ek, int[] pixels) {
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        //init to all alpha=0
        int alpha = 255;
        int red = 0;
        int green = 0;
        int blue = 0;
        int alph = 0;
        int pixel = 0;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                img.setRGB(i,j,0);
            }
        }

        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                int pos = w*j+i;
                if (ek.inSource(pos)) {
                    alph = (alpha << 24) & 0xFF000000;
                    red = (pixels[pos] << 16) & 0x00FF0000;
                    green = (pixels[pos] << 8) & 0x0000FF00;
                    blue = (pixels[pos]) & 0x000000FF;
                    pixel = alph | red | green | blue;
                    img.setRGB(i,j,pixel);
                }
            }
        }
        return img;
    }

    public BufferedImage sinkImage(int w, int h, EdmondsKarp ek, int[] pixels) {
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        //init to all alpha=0
        int alpha = 255;
        int red = 0;
        int green = 0;
        int blue = 0;
        int alph = 0;
        int pixel = 0;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                img.setRGB(i,j,0);
            }
        }

        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                int pos = w*j+i;
                if (!ek.inSource(pos)) {
                    alph = (alpha << 24) & 0xFF000000;
                    red = (pixels[pos] << 16) & 0x00FF0000;
                    green = (pixels[pos] << 8) & 0x0000FF00;
                    blue = (pixels[pos]) & 0x000000FF;
                    pixel = alph | red | green | blue;
                    img.setRGB(i,j,pixel);
                }
            }
        }
        return img;
    }

    public static void main(String[] args) {
        try {
            EKTest tester = new EKTest();
            PngOps pngops = new PngOps();
            PngMaker maker = new PngMaker();
            int w,h;

            int[] tests = {8,16,32,64};
            for (int i=0;i<tests.length;i++) {
                System.out.println("Testing image size = "+tests[i]);
                w = tests[i];
                h = tests[i];
                BufferedImage img = maker.getDiagImg(w, h);
                ImageGraph graph = pngops.get4NGraph(img);
                graph.setCapacities(tester.getCapacities(graph));
                EdmondsKarp ek = new EdmondsKarp(graph);
                System.out.println("flow = "+ek.maxFlow());
                BufferedImage srcImg = tester.srcImage(w, h, ek, graph.getVertices());
                BufferedImage sinkImg = tester.sinkImage(w,h,ek,graph.getVertices());
                File origFile = new File("test"+Integer.toString(w)+".png");
                File srcresultFile = new File("srcresults"+Integer.toString(w)+".png");
                File sinkresultFile = new File("sinkresults"+Integer.toString(w)+".png");
                ImageIO.write(img, "png", origFile);
                ImageIO.write(srcImg,"png",srcresultFile);
                ImageIO.write(sinkImg,"png",sinkresultFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
