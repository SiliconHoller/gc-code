/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.EdmondsKarp;
import edu.ohio.graphcuts.analysis.Cuts;
import edu.ohio.graphcuts.data.PngOps;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class MultObject {

    public MultObject() {

    }

    public double[][] getCapacities(ImageGraph graph) {
        //System.out.println("getCapacities()...");
        int[] points = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();
        int plength = graph.getWidth()*graph.getHeight();
        //System.out.println("\tdata length is "+points.length);
        int src = graph.getSrc();
        int sink = graph.getSink();
        //System.out.println("\tsrc = "+src);
        //System.out.println("\tsink = "+sink);
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
                    graph.setCapacity(i,j,Math.exp(-1.0*diff*diff/40000.0));
                    //System.out.println("\tcaps["+i+"]["+outedges[j]+"] = "+caps[i][j]);
                }
            }
        }

        //now do the src edges
        LinkedList<Integer> sedges = edges[src];
        if (sedges != null) {
            for (int k: sedges) {
                double diff = (double)(255 - points[k]);
                graph.setCapacity(src,k,Math.exp(-1.0*diff*diff/40000.0));
                //System.out.println("\tcaps["+src+"]["+sedges[k]+"] = "+caps[src][k]);
            }
        }

        return graph.getCapacities();
    }


    public static void main(String[] args) {
        try {
            CutTest tester = new CutTest();
            PngOps pngops = new PngOps();
            PngMaker maker = new PngMaker();
            int w,h;

            int[] tests = {8,16,32,64};
            for (int i=0;i<tests.length;i++) {
                System.out.println("Testing image size = "+tests[i]);
                w = tests[i];
                h = tests[i];
                BufferedImage img = maker.getDiagImg(w, h);
                ImageIO.write(img,"png",new File("original"+Integer.toString(w)+".png"));
                System.out.println("Edmonds-Karp cut:  "+w+"x"+w);
                ImageGraph ekgraph = pngops.get4NGraph(img);
                System.out.println("\tsrc = "+ekgraph.getSrc()+", sink = "+ekgraph.getSink());
                ekgraph.setCapacities(tester.getCapacities(ekgraph));
                EdmondsKarp ek = new EdmondsKarp(ekgraph);
                System.out.println("\tflow = "+ek.maxFlow());
                Cuts ekcuts = new Cuts(ekgraph,ek);
                System.out.println("\tThere are "+ekcuts.getContiguousCount()+" cut lines...");
                BufferedImage ekcutImg = pngops.cutImage(w, h, ekcuts.getCuts());
                BufferedImage eksrcImg = pngops.srcImage(w, h, ek, ekgraph);
                BufferedImage eksinkImg = pngops.sinkImage(w,h,ek,ekgraph);

                File ekcutsFile = new File("ekcuts"+Integer.toString(w)+".png");
                File eksrcresultFile = new File("eksrc"+Integer.toString(w)+".png");
                File eksinkresultFile = new File("eksink"+Integer.toString(w)+".png");
                System.out.println("\tSaving s-t images...");
                ImageIO.write(ekcutImg,"png",ekcutsFile);
                ImageIO.write(eksrcImg,"png",eksrcresultFile);
                ImageIO.write(eksinkImg,"png",eksinkresultFile);
                /*
                //Boykov-Kolmogorov test
                System.out.println("Boykov-Kolmogorov cut:  "+w+"x"+w);
                ImageGraph bkgraph = pngops.get4NGraph(img);
                //System.out.println("src = "+bkgraph.getSrc());
                //System.out.println("sink = "+bkgraph.getSink());
                //int[][] edges = bkgraph.getEdges();
                for (int x=0;x<edges.length;x++) {
                    System.out.print(x+" ");
                    int[] xe = edges[x];
                    for (int y=0;y<xe.length;y++) {
                        System.out.print(xe[y]+" ");
                    }
                    System.out.println();
                }
                
                System.out.println("\tsrc = "+bkgraph.getSrc()+", sink = "+bkgraph.getSink());
                bkgraph.setCapacities(tester.getCapacities(bkgraph));
                BKTreeCut bk = new BKTreeCut(bkgraph);
                System.out.println("\tflow = "+bk.maxFlow());
                Cuts bkcuts = new Cuts(bkgraph,bk);
                System.out.println("\tThere are "+bkcuts.getContiguousCount()+" lines...");
                BufferedImage bkcutImg = pngops.cutImage(w,h,bkcuts.getCuts());
                BufferedImage bksrcImg = pngops.srcImage(w,h,bk,bkgraph.getVertices());
                BufferedImage bksinkImg = pngops.sinkImage(w,h,bk,bkgraph.getVertices());
                File bkcutsFile = new File("bkcutresults"+Integer.toString(w)+".png");
                File bksrcresultFile = new File("bksrcresults"+Integer.toString(w)+".png");
                File bksinkresultFile = new File("bksinkresults"+Integer.toString(w)+".png");
                System.out.println("\tSaving s-t images...");
                ImageIO.write(bkcutImg,"png",bkcutsFile);
                ImageIO.write(bksrcImg,"png",bksrcresultFile);
                ImageIO.write(bksinkImg,"png",bksinkresultFile);
                */
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
