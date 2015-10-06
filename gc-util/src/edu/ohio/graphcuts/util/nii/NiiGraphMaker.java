/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util.nii;

import edu.ohio.graphcuts.ThreeDImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import java.lang.reflect.Array;
import java.util.LinkedList;

/**
 * Creates a ThreeDImageGraph from the given double[][][] volume.
 * @author David Days <david.c.days@gmail.com>
 */
public class NiiGraphMaker {


    public NiiGraphMaker() {

    }

    /**
     * <p>Creates a 6-neighbor graph from the given volume array.</p>
     * <p>If the value for the particular point is 0 (no data), then that position
     * is ignored for the purpose of neighbor values.
     * @param volArray Z-Y-X volume to be converted.
     * @return A 6-neighbor graph.
     */
    public ThreeDImageGraph make6NGraph(double[][][] volArray) {
        ThreeDImageGraph graph = null;
        int zth = volArray.length;
        int h = volArray[0].length;
        int w = volArray[0][0].length;
        int src = w*h*zth;
        int sink = src+1;
        System.out.println("\t\tCreating vertices...");
        int[] verts = getVertices(volArray, (w*h*zth+2));
        //System.out.println("\t\tForcing Garbage Collection...");
        //System.gc();
        System.out.println("\t\tCreating edges...");
        LinkedList<Integer>[] edges = make6Edges(volArray,verts,src,sink);
        //System.out.println("\t\tForcing Garbage Colleciton...");
        //System.gc();
        System.out.println("\t\tCreating graph instance...");
        graph = new ThreeDImageGraph(verts, edges, w,h,zth,src,sink);
        return graph;
    }

    /**
     * @task Right now, arbitrarily multiplies the double value in the original
     * array by 1024 before placing in the int[] vertices array.  Need to check
     * to see if there is a better way to handle this.
     * @param vol
     * @param len
     * @return
     */
     //TODO:  Figure out the 1024 multiplication--either validate of change vert data type
    public int[] getVertices(double[][][] vol, int len) {
        int[] v = new int[len];
        int z = vol.length;
        int h = vol[0].length;
        int w = vol[0][0].length;
        System.out.println("w = "+w);
        System.out.println("h = "+h);
        System.out.println("z = "+z);
        double voxel = 0;
        for (int i=0;i<w;i++) {
            //System.out.println("i="+i);
            for (int j=0;j<h;j++) {
                //System.out.println("j="+j);
                for (int k=0;k<z;k++) {
                    //System.out.println("k="+k);
                    voxel = vol[k][j][i];
                    //Since the original graph-cut was designed for
                    //integer values, we'll arbitrarily multiply the value
                    //of the original voxel by 1024.  Might be a problem...we'll see.
                    if (voxel > 0) v[h*w*k+w*j+i] = (int)(1024*voxel);
                }
                
            }
        }
        return v;
    }

    /**
     * Creates an edge-list from each vertex to its 6 neighbors.  Any vertex with a value of
     * zero is ignored in the creation (no neighbors are created, nor is it used as a neighbor).
     * @param vol Z-Y-X volume from which to create the graph.
     * @param verts Vertices to be used.
     * @param src Src node for this graph.
     * @param sink Sink node for this graph.
     * @return A complete list of edges.
     */
    protected LinkedList<Integer>[] make6Edges(double[][][] vol, int[] verts, int src, int sink) {
        System.out.println("\tNiiGraphMaker.make6Edges():");
        System.out.println("\t\tCreating int[][] index...");
        LinkedList<Integer>[] edges = (LinkedList<Integer>[])Array.newInstance(LinkedList.class, verts.length);
        int z = vol.length;
        int h = vol[0].length;
        int w = vol[0][0].length;
        double voxel = 0;
        System.out.println("\t\tCreating internal and sink edges...");
        //First, make all the 4-neighbor edges + to sink
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                for (int k=0;k<z;k++) {
                    edges[w*h*k+w*j+i] = make6EdgesFrom(vol,i,j,k,sink);

                }
            }
            //System.out.println("\t\tForcing Garbage collection...");
            //System.gc();
        }
        System.out.println("\t\tCreating source edges...");
        //Next, include edges from the src to all other visible points
        LinkedList<Integer> srcv = new LinkedList<Integer>();
        for (int m=0;m<w;m++) {
            for (int n=0;n<h;n++) {
                for (int o=0;o<z;o++) {
                    voxel = vol[o][n][m];
                    if (voxel > 0.0) srcv.add(w*h*o+w*n+m);
                }
            }
        }
        edges[src] = srcv;
        System.out.println("\t\tReturning new edge array...");
        return edges;

    }

    /**
     * Returns the neighbor list from the given coordinate.
     * @param vol volume to be converted.
     * @param x X coordinate of the point of interest.
     * @param y Y coordinate of the point of interest.
     * @param z Z coordinate of the point of interest.
     * @param sink Sink value to be added to the list.
     * @return A LinkedList of all neighbors connected from the point of interest.
     */
    protected LinkedList<Integer> make6EdgesFrom(double[][][] vol, int x, int y, int z, int sink) {
        //System.out.println("make4EdgesFrom(): "+w+" "+h+" "+x+" "+y+" "+sink);
        int w = vol.length;
        int h = vol[0].length;
        int zth = vol[0][0].length;
        double voxel = 0;
        LinkedList<Integer> vedges = new LinkedList<Integer>();
        voxel = vol[z][y][x];
        if (voxel > 0.0) {
            int decy = y-1;
            int incy = y+1;
            int decx = x-1;
            int incx = x+1;
            int decz = z-1;
            int incz = z+1;
            //above point
            if (decy >= 0) {
                voxel = vol[z][decy][x];
                if (voxel > 0.0) vedges.add(w*h*z+w*decy + x);
            }
            //below point
            if (incy < h) {
                voxel = vol[z][incy][x];
                if (voxel > 0.0) vedges.add(w*h*z+w*incy + x);
            }
            //left point
            if (decx >= 0) {
                voxel = vol[z][y][decx];
                if (voxel > 0.0) vedges.add(w*h*z+w*y+decx);
            }
            //right point
            if (incx < w) {
                voxel = vol[z][y][incx];
                if (voxel > 0.0) vedges.add(w*h*z+w*y+incx);
            }
            //down-z point
            if (decz >= 0) {
                voxel = vol[decz][y][x];
                if (voxel > 0.0) vedges.add(w*h*decz+w*y+x);
            }
            //up-z point
            if (incz < zth) {
                voxel = vol[incz][y][x];
                if (voxel > 0.0) vedges.add(w*h*incz+w*y+x);
            }
            vedges.add(sink);
        }

        return vedges;
    }


    /**
     * Returns a z-y-x volume only containing the voxels associated with the given
     * source after the graph-cut is complete.
     * @param gc GraphCut algorithm.
     * @param graph Original graph.
     * @param ovol Original 3-d volume.
     * @return A new volume that only contains the source-labeled voxels.
     */
    public double[][][] makeSrcVolume(GraphCut gc, ThreeDImageGraph graph, double[][][] ovol) {
        int w = graph.getWidth();
        int h = graph.getHeight();
        int zth = graph.getZHeight();
        int src = graph.getSrc();
        int sink = graph.getSink();
        int[] verts = graph.getVertices();
        double[][][] svol = new double[zth][h][w];
        int x;
        int y;
        int z;
        for (int i=0;i<verts.length;i++) {
            if (i != src && i != sink) {
                if (gc.inSource(i)) {
                    x = graph.getX(i);
                    y = graph.getY(i);
                    z = graph.getZ(i);
                    svol[z][y][x] = ovol[z][y][x];
                }
            }
        }
        return svol;

    }

    /**
     * Returns a new volume only containing the sink-labeled values from the given
     * GraphCut algorithm.
     * @param gc GraphCut algorithm used.
     * @param graph Graph used in the partitioning.
     * @param ovol Original volume.
     * @return A new z-y-x volume containing only the sink-labeled values.
     */
    public double[][][] makeSinkVolume(GraphCut gc, ThreeDImageGraph graph, double[][][] ovol) {
        int w = graph.getWidth();
        int h = graph.getHeight();
        int zth = graph.getZHeight();
        int src = graph.getSrc();
        int sink = graph.getSink();
        int[] verts = graph.getVertices();
        double[][][] svol = new double[zth][h][w];
        int x;
        int y;
        int z;
        for (int i=0;i<verts.length;i++) {
            if (i != src && i != sink) {
                if (gc.inSink(i)) {
                    x = graph.getX(i);
                    y = graph.getY(i);
                    z = graph.getZ(i);
                    svol[z][y][x] = ovol[z][y][x];
                }
            }
        }
        return svol;
    }


}
