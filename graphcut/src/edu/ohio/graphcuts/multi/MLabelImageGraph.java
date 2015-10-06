/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.multi;

import edu.ohio.graphcuts.util.ArrayOps;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * <p>Graph<V,E> structure for image data.  The purpose of this class is to
 * provide a wrapper and utility methods for the graph structure.  Original data
 * is held in as memory-efficient a manner as possible.  For instance, the edge
 * and flow matrices are held as an array-style adjacency list.</p>
 * <p>Additionally, the flow-manipulation operations (finding flow, augmenting flow,
 * calculating residual flow, etc.) are provided as public methods.</p>
 * @author David Days
 */
public class MLabelImageGraph {


    /**
     * Array of the vertices as integer values.  For images (the original purpose
     * of this class), the length is the width*height + 2.
     */
    protected int[] vertices;
    /**
     * Adjacency-list style edges.  The length is (should be) the same
     * as vertices.length, and the size of each sub-array equals the out-degree
     * of that node.
     */
    protected LinkedList<Integer>[] edges;

    protected int[][] index;

    protected int[] labels;

    /**
     * Adjacency-list style capacity matrix.  The length is (should be) the same
     * as vertices.length, and the index of the sub-array matches the index of
     * the edge in the edge array.
     */
    protected double[][] capacities;
    /**
     * Adjacency-list style flow matrix, initialized as having 0.0 at all positions.
     */
    protected double[][] flows;
    /**
     * The width of the image that is represented.
     */
    protected int w;
    /**
     * The height of the image that is represented.
     */
    protected int h;

    /**
     * Constructor for an ImageGraph instance with pre-configured vertices, edges,
     * source and sink.
     * @param vertices Vertices of the image graph.
     * @param edges Adjacency-list edge array.
     * @param src The vertex designated as the source of the flow.
     * @param sink The vertex designated as the sink.
     * @param w
     * @param h
     */
    public MLabelImageGraph(int[] vertices, LinkedList<Integer>[] edges, int[] labels, int w, int h) {
        this.vertices = vertices;
        this.edges = edges;
        this.labels = labels;
        this.w= w;
        this.h = h;
        init();
    }

    protected MLabelImageGraph() {
        
    }

    private void init() {
        System.out.println("\tImageGraph.init():");
        System.out.println("\t\tCreating edge index...");
        index = getEdgeArray();
        System.out.println("\t\tCreating capacity array...");
        capacities = getNewFlows();
        System.out.println("\t\tCreating flow array...");
        flows = ArrayOps.cloneArray(capacities);
    }

    /**
     * Returns the width of the image being represented.
     * @return
     */
    public int getWidth() {
        return w;
    }



    /**
     * Returns the height of the image being represented.
     * @return
     */
    public int getHeight() {
        return h;
    }

    public int[][] getEdgeIndex() {
        return index;
    }

    /**
     * Sets the capacity matrix for this image.
     * @param capacities
     */
    public void setCapacities(double[][] capacities) {
        this.capacities = capacities;
    }


    /**
     * Returns the position in vertices of the designated source.
     * @return
     */
    public int[] getLabels() {
        return labels;
    }


    /**
     * Returns the integer array of the vertices in the graph.
     * @return
     */
    public int[] getVertices() {
        return vertices;
    }



    /**
     * Returns the edge matrix.
     * @return
     */
    public LinkedList<Integer>[] getEdges() {
        return edges;
    }

    /**
     * Returns the capacity matrix for this graph.
     * @return
     */
    public double[][] getCapacities() {
        return capacities;
    }

    /**
     * Returns a new adjacency-list copy of the capacity matrix with all
     * values initialized to 0.0.
     * @return
     */
    public double[][] getNewFlows() {
        double[][] array = new double[edges.length][0];
        for (int i=0;i<index.length;i++) {
            int[] irow = index[i];
            double[] arow = new double[irow.length];
            array[i] = arow;
        }
        return array;
    }

    public int[][] getEdgeArray() {
        int[][] array = new int[edges.length][0];
        int size = 0;
        int j=0;
        /*
        *for (int i=0;i<edges.length;i++) {
        *    //System.out.println("looking at edges for "+i);
        *    LinkedList<Integer> elist = edges[i];
        *    if (elist != null) {
        *        size = elist.size();
        *        array[i] = new int[size];
        *        for (int j=0;j<size;j++) {
        *            array[i][j] = elist.get(j).intValue();
        *        }
        *    }
        *}
        */
        for (int i=0;i<edges.length;i++) {
            LinkedList<Integer> elist = edges[i];
            if (elist != null) {
                size = elist.size();
                j = 0;
                array[i] = new int[size];
                Iterator<Integer> it = elist.iterator();
                while (it.hasNext()) {
                    array[i][j] = it.next();
                    j++;
                }
            }
        }
        return array;
    }

    /**
     * Returns the available remaining flow (capacity - flow)
     * along the given edge.
     * @param u Vertex flowing <strong>from</strong>.
     * @param v Vertex flowing <strong>to</strong>.
     * @return Available flow if edge exists; otherwise returns 0.0
     */
    public double getAvailable(int u, int v) {
        double avail = 0.0;
        int[] erow = index[u];
        for (int i=0;i<erow.length;i++) {
            if (erow[i] == v) {
                avail = capacities[u][i] - flows[u][i];
            }
        }

        return avail;
    }

    public double getCapacity(int u, int v) {
        int[] erow = index[u];
        for (int i=0;i<erow.length;i++) {
            if (erow[i] == v) {
                return capacities[u][i];
            }
        }

        return 0.0;
    }

    public void setCapacity(int u, int v, double cap) {
        int[] erow = index[u];
        for (int i=0;i<erow.length;i++) {
            if (erow[i] == v) {
                capacities[u][i] = cap;
                return;
            }
        }
    }

    /**
     * Returns the minimum flow available along the given path.
     * @param path The list if vertices traversed, in order
     * @return The minimum flow available along that path.
     */
    public double minAvailable(int[] path) {
        //System.out.println("\tpath.length = "+path.length);
        double minFlow = Double.POSITIVE_INFINITY;
        int l = path.length - 1;
        for (int i=0;i<l;i++) {
            minFlow = min(minFlow,getAvailable(path[i],path[i+1]));
        }
        //System.out.println("minFlow = "+minFlow);
        return minFlow;
    }

    /**
     * Utility method to return the minimum of two values.
     * @param x First value
     * @param y Second value
     * @return Returns x if x is less than y, otherwise returns y.
     */
    protected double min(double x, double y) {
        return (x < y) ? x : y;
    }

    /**
     * Adds the given flow to the path provided.
     * @param flow The amount of flow to add along the path.
     * @param path The list of vertices traversed.
     */
    public void augmentPath(double flow, int[] path) {
        int l = path.length -1;
        for (int i=0;i<l;i++) {
            addFlow(flow, path[i],path[i+1]);
        }
    }

    /**
     * Adds the given flow to the given edge.  If the edge does
     * not exist, no flow is added.
     * @param flow The amount of flow to add to that edge.
     * @param u The vertex from which the edge originates.
     * @param v The vertex to which the edge connects.
     */
    public void addFlow(double flow, int u, int v) {
        int[] erow = index[u];
        for (int i=0;i<erow.length;i++) {
            if (erow[i] == v) {
                flows[u][i] += flow;
            }
        }


    }

    public void removeEdge(int u, int v) {
        LinkedList<Integer> list = edges[u];
        if (list != null) {
            list.remove(new Integer(v));
        }
    }

    public void removeFullEdges(int[] path) {
        int l = path.length -1;
        double avail = 0.0;
        for (int i=0;i<l;i++) {
            avail = getAvailable(path[i],path[i+1]);
            if (avail <= 0) {
                removeEdge(path[i],path[i+1]);
            }
        }
    }

    public int getX(int pos) {
        return pos%w;
    }

    public int getY(int pos) {
        return pos/w;
    }

    public int getPosition(int x, int y) {
        return (w*y+x);
    }

    public boolean isLabel(int u) {
        boolean ret = false;
        for (int i=0;i<labels.length;i++) {
            if (labels[i] == u) {
                ret = true;
            }
        }
        return ret;
    }

    public LinkedList<Integer> getEdgesFrom(int u) {
        LinkedList<Integer> oe = new LinkedList<Integer>();
        LinkedList<Integer> n = edges[u];
        if (n != null) {
            oe = n;
        }

        return oe;
    }

    public LinkedList<Integer> getEdgesTo(int v) {
        LinkedList<Integer> ie = new LinkedList<Integer>();
        LinkedList<Integer> ue;
        for (int i=0;i<vertices.length;i++) {
            ue = edges[i];
            if (ue != null) {
                for (int pv : ue) {
                    if (pv == v) {
                        ie.add(i);
                    }
                }
            }
        }
        return ie;
    }


}
