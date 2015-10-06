/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util;



import edu.ohio.graphcuts.NLabelImageGraph;
import edu.ohio.graphcuts.UndirectedNLabelImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.LinkedList;

/**
 *
 * @author david
 */
public class NLabelImageOps {


    public NLabelImageOps() {

    }
    
    public int getGrayVal(int pixel) {
        int val = 0;

        int red = getRedVal(pixel);
        int green = getGreenVal(pixel);
        int blue = getBlueVal(pixel);
        val = (red+green+blue)/3;
        //System.out.println("pixel value = "+val);
        return val;
    }

    public int makeGrayPixel(int gval) {
        return makeRGBPixel(255,gval,gval,gval);
    }

    public int makeRGBPixel(int aval, int rval, int gval, int bval) {
        return ((aval << 24) & 0xFF000000) | ((rval << 16) & 0x00FF0000)
                | ((gval << 8) & 0x0000FF00) | (bval & 0x000000FF);
    }

    public int getRedVal(int pixel) {
        return (pixel & 0x00FF0000) >> 16;
    }

    public int getGreenVal(int pixel) {
        return (pixel & 0x0000FF00) >> 8;
    }

    public int getBlueVal(int pixel) {
        return pixel & 0x000000FF;
    }

    public boolean isVisible(int pixel) {
        int alpha = ((pixel & 0xFF000000) >> 24);
        return alpha != 0;
    }
    
    public NLabelImageGraph get4NGraph(BufferedImage img, int numLabels) {
        System.out.println("\tPngOps.get4NGraph():...");
        NLabelImageGraph graph = null;
        int w = img.getWidth();
        int h = img.getHeight();
        int[] labels = new int[numLabels];
        int first = w*h;
        for (int i=0;i<labels.length;i++) {
            labels[i] = first+i;
        }
        System.out.println("\t\tGetting vertices...");
        int[] vertices = getVertices(img,labels);
        //System.out.println("Vertices.length = "+vertices.length);
        System.out.println("\t\tGetting edges...");
        LinkedList<Integer>[] edges = make4Edges(img,vertices,labels);
        System.out.println("\t\tInstantiating ImageGraph object...");
        graph = new NLabelImageGraph(vertices,edges,labels,w,h);
        return graph;
    }

    public UndirectedNLabelImageGraph getUndirected4NGraph(BufferedImage img, int numLabels) {
        System.out.println("\tNLabelImageOps.getUndirected4NGraph():...");
        UndirectedNLabelImageGraph graph = null;
        int w = img.getWidth();
        int h = img.getHeight();
        int[] labels = new int[numLabels];
        int first = w*h;
        for (int i=0;i<labels.length;i++) {
            labels[i] = first+i;
        }
        System.out.println("\t\tGetting vertices...");
        int[] vertices = getVertices(img,labels);
        //System.out.println("Vertices.length = "+vertices.length);
        System.out.println("\t\tGetting edges...");
        LinkedList<Integer>[] edges = make4Edges(img,vertices,labels);
        System.out.println("\t\tInstantiating ImageGraph object...");
        graph = new UndirectedNLabelImageGraph(vertices,edges,labels,w,h);
        return graph;
    }

    public NLabelImageGraph getColor4NGraph(BufferedImage img, int numLabels) {
        NLabelImageGraph graph = get4NGraph(img, numLabels);
        //Reset the vertice values to the color values
        int w = img.getWidth();
        int h = img.getHeight();
        int[] verts = graph.getVertices();
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                verts[w*j+i] = img.getRGB(i, j);
            }
        }

        return graph;
    }

    public UndirectedNLabelImageGraph getUndirected8NGraph(BufferedImage img, int numLabels) {
        UndirectedNLabelImageGraph graph = null;
        int w = img.getWidth();
        int h = img.getHeight();
        int first = w*h;
        int[] labels = new int[numLabels];
        for (int i=0;i<labels.length;i++) {
            labels[i] = first+i;
        }
        int[] vertices = getVertices(img,labels);
        LinkedList<Integer>[] edges = make8Edges(img,vertices,labels);
        graph = new UndirectedNLabelImageGraph(vertices,edges,labels,w,h);

        return graph;
    }

    public NLabelImageGraph get8NGraph(BufferedImage img, int numLabels) {
        NLabelImageGraph graph = null;
        int w = img.getWidth();
        int h = img.getHeight();
        int first = w*h;
        int[] labels = new int[numLabels];
        for (int i=0;i<labels.length;i++) {
            labels[i] = first+i;
        }
        int[] vertices = getVertices(img,labels);
        LinkedList<Integer>[] edges = make8Edges(img,vertices,labels);
        graph = new NLabelImageGraph(vertices,edges,labels,w,h);

        return graph;
    }

    public LinkedList<Integer>[] make8Edges(BufferedImage img, int[] vertices, int[] labels) {
        LinkedList<Integer>[] edges = (LinkedList<Integer>[])Array.newInstance(LinkedList.class, vertices.length);
        int w = img.getWidth();
        int h = img.getHeight();
        int pixel = 0;
        //First, make all 8-neighbor edges + to each label
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                edges[w*j+i] = make8EdgesFrom(img,i,j,labels);
            }
        }

        //Next, include edges from the labels to all other visible points
        for (int l=0;l<labels.length;l++) {
            int src = labels[l];
            LinkedList<Integer> srcv = new LinkedList<Integer>();
            for (int m=0;m<w;m++) {
                for (int n=0;n<h;n++) {
                    pixel = img.getRGB(m, n);
                    if (isVisible(pixel)) srcv.add(w*n+m);
                }
            }

            edges[src] = srcv;
        }
        return edges;
    }

    public LinkedList<Integer> make8EdgesFrom(BufferedImage img, int x, int y, int[] labels) {
        LinkedList<Integer> v = new LinkedList<Integer>();
        int w = img.getWidth();
        int h = img.getHeight();
        int pixel = img.getRGB(x, y);
        if (isVisible(pixel)) {
            int vx = 0;
            int vy = 0;
            for (int i=-1;i<2;i++) {
                for (int j=-1;j<2;j++) {
                    vx = x+i;
                    vy = y+j;
                    if (vx >= 0 && vx < w && vy >= 0 && vy < h) {
                        if (vx == x && vy == y) {
                            //do nothing
                        } else {
                            pixel = img.getRGB(vx,vy);
                            if (isVisible(pixel)) v.add(w*vy+vx);
                        }
                    }
                }
            }

            //Add the "sink" edges
            for (int s=0;s<labels.length;s++) {
                v.add(labels[s]);
            }
        }

        return v;
    }

    protected int[] getVertices(BufferedImage img, int[] labels) {
        int w = img.getWidth();
        int h = img.getHeight();
        int len = w*h+labels.length;
        int[] v = new int[len];
        int pixel = 0;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                pixel = img.getRGB(i,j);
                if (isVisible(pixel)) v[w*j+i] = getGrayVal(pixel);
            }
        }
        return v;
    }

    @SuppressWarnings("unchecked")
    protected LinkedList<Integer>[] make4Edges(BufferedImage img, int[] v, int[] labels) {
        //System.out.println("\tPngOps.make4Edges():");
        //System.out.println("\t\tCreating LinkedList<Integer>[]...");
        LinkedList<Integer>[] nedges = (LinkedList<Integer>[])Array.newInstance(LinkedList.class, v.length);
        int w = img.getWidth();
        int h = img.getHeight();
        int pixel = 0;
        //System.out.println("\t\tCreating internal and sink edges...");
        //First, make all the 4-neighbor edges + to sink
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                nedges[w*j+i] = make4EdgesFrom(img,i,j,labels);
            }
        }
        //System.out.println("\t\tCreating source edges...");
        //Next, include edges from the srcs to all other visible points
        for (int l=0;l<labels.length;l++) {
            int src = labels[l];
            LinkedList<Integer> srcv = new LinkedList<Integer>();
            for (int m=0;m<w;m++) {
                for (int n=0;n<h;n++) {
                    pixel = img.getRGB(m, n);
                    if (isVisible(pixel)) srcv.add(w*n+m);
                }
            }
            nedges[src] = srcv;
        }
        //System.out.println("\t\tReturning new edge array...");
        return nedges;
    }

    protected LinkedList<Integer> make4EdgesFrom(BufferedImage img, int x, int y, int[] labels) {
        //System.out.println("make4EdgesFrom(): "+w+" "+h+" "+x+" "+y+" "+sink);
        int w = img.getWidth();
        int h = img.getHeight();
        int pixel = 0;
        LinkedList<Integer> vedges = new LinkedList<Integer>();
        pixel = img.getRGB(x,y);
        if (isVisible(pixel)) {
            int uy = y-1;
            int dy = y+1;
            int lx = x-1;
            int rx = x+1;

            //above point
            if (uy >= 0) {
                pixel = img.getRGB(x,uy);
                if (isVisible(pixel)) vedges.add(w*uy + x);
            }
            //below point
            if (dy < h) {
                pixel = img.getRGB(x,dy);
                if (isVisible(pixel)) vedges.add(w*dy + x);
            }
            //left point
            if (lx >= 0) {
                pixel = img.getRGB(lx,y);
                if (isVisible(pixel)) vedges.add(w*y+lx);
            }
            //right point
            if (rx < w) {
                pixel = img.getRGB(rx,y);
                if (isVisible(pixel)) vedges.add(w*y+rx);
            }
            for (int i=0;i<labels.length;i++) {
                vedges.add(labels[i]);
            }
        }

        return vedges;
    }


    public BufferedImage labelImage(int label, GraphCut gc, NLabelImageGraph graph) {
        //init to all alpha=0
        int w = graph.getWidth();
        int h = graph.getHeight();
        BufferedImage img = blankImage(w,h);
        int[] pixels = graph.getVertices();
        
        int pixel = 0;
        for (int i=0;i<w;i++) {
            //System.out.print("\tx = "+i+":  ");
            for (int j=0;j<h;j++) {
                //System.out.print("y="+j+" ");
                int pos = w*j+i;
                if ( gc.inLabel(label, pos)) {
                    pixel = makeGrayPixel(pixels[pos]);
                    img.setRGB(i,j,pixel);
                }
            }
            //System.out.println("");
        }
        return img;
    }



    public BufferedImage cutImage(int w, int h, int[][] cuts) {
        BufferedImage img = blankImage(w,h);
        //Set a full-on green pixel
        int pixel = makeRGBPixel(255,0,255,0);

        for (int i=0;i<cuts.length;i++) {
            int u = cuts[i][0];
            int v = cuts[i][1];
            int ux = u%w;
            int uy = u/w;
            int vx = v%w;
            int vy = v/w;
            img.setRGB(ux,uy,pixel);
            img.setRGB(vx,vy,pixel);
        }
        return img;
    }

    public BufferedImage blankImage(int w, int h) {
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                img.setRGB(i, j, 0);
            }
        }
        return img;
    }

    public BufferedImage rawToPng(File rawFile,int w, int h) throws IOException {
        BufferedImage img = blankImage(w,h);
        byte[] bdata = RawCodec.loadData(rawFile);
        int[] idata = RawCodec.getIntData(bdata);
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                img.setRGB(i, j, makeGrayPixel(idata[w*j+i]));
            }
        }
        return img;
    }

    public static BufferedImage makeGrayScaleImage(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        NLabelImageOps pops = new NLabelImageOps();
        BufferedImage gimg = pops.blankImage(w, h);
        
        int grayval;
        int pixel;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                pixel = img.getRGB(i,j);
                if (pops.isVisible(pixel)) {
                    grayval = pops.getGrayVal(pixel);
                    gimg.setRGB(i,j,pops.makeGrayPixel(grayval));
                }
            }
        }

        return gimg;
    }

}
