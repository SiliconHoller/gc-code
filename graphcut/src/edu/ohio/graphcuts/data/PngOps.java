/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.data;

import edu.ohio.graphcuts.ImageGraph;
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
public class PngOps {


    public PngOps() {

    }
    
    public int getGrayVal(int pixel) {
        int val = 0;

        int red = (pixel & 0x00FF0000) >> 16;
        int green = (pixel & 0x0000FF00) >> 8;
        int blue = pixel & 0x000000FF;
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
    
    public ImageGraph get4NGraph(BufferedImage img) {
        System.out.println("\tPngOps.get4NGraph():...");
        ImageGraph graph = null;
        int w = img.getWidth();
        int h = img.getHeight();
        int src = w*h;
        int sink = src+1;
        System.out.println("\t\tGetting vertices...");
        int[] vertices = getVertices(img,(w*h+2),src,sink);
        //System.out.println("Vertices.length = "+vertices.length);
        System.out.println("\t\tGetting edges...");
        LinkedList<Integer>[] edges = make4Edges(img,vertices,src,sink);
        System.out.println("\t\tInstantiating ImageGraph object...");
        graph = new ImageGraph(vertices,edges,src,sink,w,h);
        return graph;
    }

    public ImageGraph getColor4NGraph(BufferedImage img) {
        ImageGraph graph = get4NGraph(img);
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

    public ImageGraph get8NGraph(BufferedImage img) {
        ImageGraph graph = null;
        int w = img.getWidth();
        int h = img.getHeight();
        int src = w*h;
        int sink = src+1;
        int[] vertices = getVertices(img,(w*h+2),src,sink);
        LinkedList<Integer>[] edges = make8Edges(img,vertices,src,sink);
        graph = new ImageGraph(vertices,edges,src,sink,w,h);

        return graph;
    }

    public LinkedList<Integer>[] make8Edges(BufferedImage img, int[] vertices, int src, int sink) {
        LinkedList<Integer>[] edges = (LinkedList<Integer>[])Array.newInstance(LinkedList.class, vertices.length);
        int w = img.getWidth();
        int h = img.getHeight();
        int pixel = 0;
        //First, make all 8-neighbor edges + to sink
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                edges[w*j+i] = make8EdgesFrom(img,i,j,sink);
            }
        }

        //Next, include edges from the src to all other visible points
        LinkedList<Integer> srcv = new LinkedList<Integer>();
        for (int m=0;m<w;m++) {
            for (int n=0;n<h;n++) {
                pixel = img.getRGB(m, n);
                if (isVisible(pixel)) srcv.add(w*n+m);
            }
        }
        
        edges[src] = srcv;

        return edges;
    }

    public LinkedList<Integer> make8EdgesFrom(BufferedImage img, int x, int y, int sink) {
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

            //Add the sink edge
            v.add(sink);
        }

        return v;
    }

    protected int[] getVertices(BufferedImage img, int len, int src, int sink) {
        int[] v = new int[len];
        int w = img.getWidth();
        int h = img.getHeight();
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
    protected LinkedList<Integer>[] make4Edges(BufferedImage img, int[] v, int src, int sink) {
        System.out.println("\tPngOps.make4Edges():");
        System.out.println("\t\tCreating LinkedList<Integer>[]...");
        LinkedList<Integer>[] nedges = (LinkedList<Integer>[])Array.newInstance(LinkedList.class, v.length);
        int w = img.getWidth();
        int h = img.getHeight();
        int pixel = 0;
        System.out.println("\t\tCreating internal and sink edges...");
        //First, make all the 4-neighbor edges + to sink
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                nedges[w*j+i] = make4EdgesFrom(img,i,j,sink);
            }
        }
        System.out.println("\t\tCreating source edges...");
        //Next, include edges from the src to all other visible points
        LinkedList<Integer> srcv = new LinkedList<Integer>();
        for (int m=0;m<w;m++) {
            for (int n=0;n<h;n++) {
                pixel = img.getRGB(m, n);
                if (isVisible(pixel)) srcv.add(w*n+m);
            }
        }
        nedges[src] = srcv;
        System.out.println("\t\tReturning new edge array...");
        return nedges;
    }

    protected LinkedList<Integer> make4EdgesFrom(BufferedImage img, int x, int y, int sink) {
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
            vedges.add(sink);
        }

        return vedges;
    }


    public BufferedImage srcImage(int w, int h, GraphCut gc, ImageGraph graph) {
        //init to all alpha=0
        BufferedImage img = blankImage(w,h);
        int[] pixels = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();
        int pixel = 0;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                int pos = w*j+i;
                if ( edges[pos] != null && gc.inSource(pos)) {
                    pixel = makeGrayPixel(pixels[pos]);
                    img.setRGB(i,j,pixel);
                }
            }
        }
        return img;
    }

    public BufferedImage sinkImage(int w, int h, GraphCut gc, ImageGraph graph) {
        BufferedImage img = blankImage(w,h);
        int[] pixels = graph.getVertices();
        LinkedList<Integer>[] edges = graph.getEdges();
        int pixel = 0;

        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                int pos = w*j+i;
                if (edges[pos] != null && !gc.inSource(pos)) {
                    pixel = makeGrayPixel(pixels[pos]);
                    img.setRGB(i,j,pixel);
                }
            }
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
        PngOps pops = new PngOps();
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
