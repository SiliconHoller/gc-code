/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util;

import edu.ohio.graphcuts.ImageGraph;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.LinkedList;

/**
 * <p>Basic 4-neighbor image graph from a RAW file.</p>
 * <p>When passed a file, the system reads the file and creates
 * a ImageGraph object with the following characteristics:
 * <ul>
 * <li>The int[] array is the original data <em>plus 2</em></li>
 * <li>The src vertex is at original data +1.</li>
 * <li>The sink vertex is at original data +2.</li>
 * <li>The edges includes edges from src to all points.</li>
 * <li>The edges include edges from all points to the sink.</li>
 * <li>The capacities are set to 0.</li>
 * </ul>
 * </p>
 * <p>With this COder/DECoder (CODEC), a basic graph can be created.</p>
 *
 * @author david
 */
public class RawCodec {

    protected File file;
    protected int src;
    protected int sink;
    protected int w; //width of RAW image
    protected int h; //height of RAW image
    protected int[] vertices;
    protected LinkedList<Integer>[] edges;


    public RawCodec(File rawFile, int w, int h) {
        this.file = rawFile;
        this.w = w;
        this.h = h;
    }


    protected int[] getVertices(byte[] bdata) {
        int[] v = new int[bdata.length+2];
        for (int i=0;i<bdata.length;i++) {
            v[i] = bdata[i] & 0xFF;
        }
        return v;
    }

    public static int[] getIntData(byte[] bdata) {
        int[] v = new int[bdata.length];
        for (int i=0;i<bdata.length;i++) {
            v[i] = bdata[i] & 0xFF;
        }
        return v;
    }

    protected byte[] loadData() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(file);
        int k;
        while ((k = fis.read()) != -1) {
            baos.write(k);
        }
        return baos.toByteArray();
    }

    public static byte[] loadData(File f) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(f);
        int k;
        while ((k = fis.read()) != -1) {
            baos.write(k);
        }
        return baos.toByteArray();
    }

    protected LinkedList<Integer>[] make4Edges(int[] v) {
        LinkedList<Integer>[] nedges = (LinkedList<Integer>[])Array.newInstance(LinkedList.class, v.length);
        //First, make all the 4-neighbor edges + to sink
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                nedges[w*j+i] = make4EdgesFrom(i,j);
            }
        }

        //Next, include edges from the src to all other points
        LinkedList<Integer> sedges = new LinkedList<Integer>();
        for (int k=0;k<src;k++) {
            sedges.add(k);
        }
        nedges[src] = sedges;
        
        return nedges;
    }

    protected LinkedList<Integer>[] make8Edges(int[] v) {
        LinkedList<Integer>[] nedges = (LinkedList<Integer>[])Array.newInstance(LinkedList.class,v.length);
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                nedges[w*j+i] = make8EdgesFrom(i,j);
            }
        }
         LinkedList<Integer> sedges = new LinkedList<Integer>();
         for (int k=0;k<src;k++) {
             sedges.add(k);
         }
         nedges[src] = sedges;

         return nedges;
    }

    protected LinkedList<Integer> make8EdgesFrom(int x, int y) {
        LinkedList<Integer> v = new LinkedList<Integer>();
        for (int i=-1;i<2;i++) {
            for (int j=-1;j<2;j++) {
                int dx = x+i;
                int dy = y+j;
                if (dx >= 0 && dy >= 0 && dx < w && dy < h) {
                    if (x == dx && y == dy) {
                        //do nothing
                    } else {
                        v.add(w*dy+dx);
                    }
                }
            }
        }
        return v;
    }

    protected LinkedList<Integer> make4EdgesFrom(int x, int y) {
        LinkedList<Integer> vedges = new LinkedList<Integer>();
        int uy = y-1;
        int dy = y+1;
        int lx = x-1;
        int rx = x+1;
        //above point
        if (uy >= 0) {
            vedges.add(w*uy + x);
        }
        //below point
        if (dy < h) {
            vedges.add(w*dy + x);
        }
        //left point
        if (lx >= 0) {
            vedges.add(w*y+lx);
        }
        //right point
        if (rx < w) {
            vedges.add(w*y+rx);
        }
        vedges.add(sink);
        return vedges;
    }


    public ImageGraph create4NGraph() throws IOException {
        System.out.println("createGraph()...");
        byte[] bdata = loadData();
        System.out.println("/ttotal bytes = "+bdata.length);
        src = bdata.length;
        sink = bdata.length+1;
        vertices = getVertices(bdata);
        edges = make4Edges(vertices);
        ImageGraph graph = new ImageGraph(vertices, edges, src,sink,w,h);
        return graph;
    }

    public ImageGraph create8NGraph() throws IOException {
        byte[] bdata = loadData();
        src = bdata.length;
        sink = bdata.length+1;
        vertices = getVertices(bdata);
        edges = make8Edges(vertices);
        ImageGraph graph = new ImageGraph(vertices, edges, src, sink, w, h);
        return graph;
    }


}
