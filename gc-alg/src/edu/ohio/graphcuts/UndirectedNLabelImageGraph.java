/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts;

import edu.ohio.graphcuts.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
 * <p>Graph<V,E> structure for image data.  The purpose of this class is to
 * provide a wrapper and utility methods for the graph structure.  Original data
 * is held in as memory-efficient a manner as possible.  This is an undirected graph
 * in structure and methodology.  For directed graphs, use the original
 * NLabelImageGraph class.</p>
 * <p>Additionally, the flow-manipulation operations (finding flow, augmenting flow,
 * calculating residual flow, etc.) are provided as public methods.</p>
 * @author David Days
 */
public class UndirectedNLabelImageGraph extends NLabelImageGraph {





    /**
     * Collection of all edges within a graph.
     */
    protected Vector<Edge> vedges;
   

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
    public UndirectedNLabelImageGraph(int[] vertices, LinkedList<Integer>[] edges, int[] labels, int w, int h) {
        this.vertices = vertices;
        this.labels = labels;
        this.w= w;
        this.h = h;
        init(edges);
    }

    protected UndirectedNLabelImageGraph() {
        
    }

    /**
     * Internal init process that calls createEdgeVector() and sets the local
     * variable edges to null.
     * @param edges  The pre-compiled list of edges
     */
    private void init(LinkedList<Integer>[] edges) {
        createEdgeVector(edges);
        this.edges = null;
    }

    /**
     * Creates the internal Vector<Edge> vedges from the LinkedList<Integer> array.
     */
    protected void createEdgeVector(LinkedList<Integer>[] ledges) {
        vedges = new Vector<Edge>();
        for (int u=0;u<ledges.length;u++) {
            LinkedList<Integer> uv = ledges[u];
            if (uv != null) {
                for (int v :uv) {
                    if (!containsEdge(u,v)) {
                        vedges.add(new Edge(u,v));
                    }
                }
            }
        }
    }


    /**
     * Searches for, and returns on the success or failure, an
     * Edge between the two given end points.
     * @param u One end of the edge to search for
     * @param v Another end of the edge to search for.
     * @return  True if there is an existing edge between the two
     * points; otherwise, false.
     */
    protected boolean containsEdge(int u, int v) {
        boolean retval = false;

        //Search for an equivalent edge from u to v
        retval = vedges.contains(new Edge(u,v));
        if (!retval) {
            //search for the reverse edge from v to u
            retval = vedges.contains(new Edge(v,u));
        }
        return retval;
    }

    /**
     * Internal method to get an Edge (if it exists) between the two
     * given nodes.
     * @param u One endpoint of the Edge to search for.
     * @param v Other endpoint of the Edge to search for.
     * @return The edge connecting the two points, if it exists.  Otherwise,
     * returns null.
     */
    protected Edge getEdge(int u, int v) {
        Edge rval = null;
        int pos = getPositionOfEdge(u,v);

        //If pos != -1, return the Edge at the given position
        if (pos > -1) {
            rval = vedges.elementAt(pos);
        }

        return rval;
    }


    /**
     * Internal maintenance function to return the position in the vedges
     * array of the given edge.
     * @param u One endpoint of the Edge to be found.
     * @param v The other endpoint of the Edge to be found.
     * @return The integer position in the Vector vedges, if the Edge exists.
     * Otherwise, returns -1.
     */
    protected int getPositionOfEdge(int u, int v) {
        int pos = vedges.indexOf(new Edge(u,v));
        if (pos == -1) {
            //forward search didn't work, use reverse
            pos = vedges.indexOf(new Edge(v,u));
        }
        return pos;
    }

    /**
     * Returns the available remaining flow (capacity - flow)
     * along the given edge.
     * @param u Vertex flowing <strong>from</strong>.
     * @param v Vertex flowing <strong>to</strong>.
     * @return Available flow if edge exists; otherwise returns 0.0
     */
    @Override
    public double getAvailable(int u, int v) {
        double avail = 0.0;
        Edge e = getEdge(u,v);
        if (e != null) {
            avail = e.getAvailable();
        }

        return avail;
    }

    /**
     * Returns the capacity for the Edge with the endpoints of u and v, if it exists.
     * Otherwise returns 0.0.
     * @param u One endpoint of the Edge to be found.
     * @param v The other endpoint of the Edge to be found.
     * @return The capacity set for that edge, if it exists.  Otherwise, returns 0.0.
     */
    @Override
    public double getCapacity(int u, int v) {
        double cap = 0.0;
        Edge e = getEdge(u,v);
        if (e != null) {
            cap = e.getCapacity();
        }
        return cap;
    }

    /**
     * Sets the capacity for the given edge.
     * @param u One endpoint of the edge.
     * @param v Other endpoint of the Edge.
     * @param cap Value to be set.
     */
    @Override
    public void setCapacity(int u, int v, double cap) {
        Edge e = getEdge(u,v);
        if (e != null) {
            e.setCapacity(cap);
        }
    }


    /**
     * Adds the given flow to the path provided.
     * @param flow The amount of flow to add along the path.
     * @param path The list of vertices traversed.
     */
    @Override
    public void augmentPath(double flow, int[] path) {
        int l = path.length -1;
        for (int i=0;i<l;i++) {
            addFlow(flow, path[i],path[i+1]);
        }
    }

    /**
     * <p>Specifically for use in the BorderCut algorithm.</p>
     * <p>The flow passed in via the flow parameter is added once to the assoicated
     * edges.  This method overrides NLabelImageGraph for the following reasons:
     * <ul>
     * <li>As an undirected graph, this implementation only need apply
     * the flow once to accomplish the modification.</li>
     * <li>The internal edge structure is completely different for this implementation.</li>
     * </ul>
     * </p>
     * <p>The addition of this method allows the algorithm to concentrate on the decision
     * process, while "hiding" the ugly details of it's implementation.  NLabelImageGraph,
     * for example, would add the flow in both directions--yet the algorithm can process both.</p>
     * <p>Care should be taken not to inadvertently override this  function by trying to
     * "fix" the augmentation process with outside path augmentation.  Combined internal/external
     * augmentations would lead to strange and hard-to-trace errors.</p>
     * <p>...and in that direction lies madness.</p>
     *
     * @param flow  The amount of flow to be pushed along the path set.
     * @param upath The path from the current label to node u.
     * @param vpath The path from the current label to node v.
     * @param u  One end of the Border between the label set to which u belongs and the label
     *  set to which v belongs.
     * @param v The other end of the Border between the label set to which u belongs and the
     * label set to which v belongs.
     */
    @Override
    public void augmentPathSet(double flow, int[] upath, int[] vpath, int u, int v) {
        if (upath != null) {
            //Forward augmentation for upath
            int ul = upath.length -1;
            for (int i=0;i<ul;i++) {
                addFlow(flow, upath[i],upath[i+1]);
            }
        }

        if (vpath != null) {
            //Forward augmentation for vpath
            int vl = vpath.length -1;
            for (int i=0;i<vl;i++) {
                addFlow(flow, vpath[i],vpath[i+1]);
            }
        }

        //Forward augmentation for Edge(u,v);
        addFlow(flow, u, v);
    }

    /**
     * Adds the given flow to the given edge.  If the edge does
     * not exist, no flow is added.
     * @param flow The amount of flow to add to that edge.
     * @param u The vertex from which the edge originates.
     * @param v The vertex to which the edge connects.
     */
    @Override
    public void addFlow(double flow, int u, int v) {
        Edge e = getEdge(u,v);
        if (e != null) {
            e.addFlow(flow);
        }
    }

    /**
     * Removes the given edge from the internal graph structure, if it exists.
     * @param u One endpoint of the Edge.
     * @param v The other endpoint of the edge.
     */
    @Override
    public void removeEdge(int u, int v) {
        int pos = getPositionOfEdge(u,v);
        if (pos != -1) {
            vedges.removeElementAt(pos);
        }
    }


    /**
     * Returns a LinkedList of all unique edges in which the
     * given node is an endpoint.
     * @param u The node from which to search.
     * @return A LinkedList of all nodes connected to the given node.  Otherswise,
     * an empty list.
     */
    @Override
    public LinkedList<Integer> getEdgesFrom(int u) {
        //To guarantee uniqueness of each element,
        //create a set object that accepts new elements
        //but simply rejects repeats.
        HashSet<Integer> s = new HashSet<Integer>();
        Iterator<Edge> it = vedges.iterator();
        Edge e;
        while (it.hasNext()) {
            e = it.next();
            if (e.u == u) {
                //add the other end
                s.add(e.v);
            } else if (e.v == u) {
                //add the other end
                s.add(e.u);
            }
        }

        //Convert to the required LinkedList
        LinkedList<Integer> oe = new LinkedList<Integer>();
        Iterator<Integer> sit = s.iterator();
        while (sit.hasNext()) {
            oe.add(sit.next());
        }
        return oe;
    }

    /**
     * Externally, this method finds edge <em>to</em> the given node.  However,
     * it merely returns the same values (by calling) getEdgesFrom() (this being
     * and undirected graph).
     * @param v  the
     * @return
     */
    @Override
    public LinkedList<Integer> getEdgesTo(int v) {
        return getEdgesFrom(v);
    }


}
