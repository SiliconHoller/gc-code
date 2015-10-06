/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg;

import edu.ohio.graphcuts.ArrayOps;
import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.ImageGraph;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

/**
 *<p>Implementation of the GraphCut interface that performs a
 * graph cut on the given ImageGraph via BorderCut algorithm.</p>
 * <p>The algorithm conducts the cut in the following manner:
 * <ul>
 * <li>Similar to the BK algorithm, "trees" from the source and
 * sink nodes are created.  In the Border Cut algorithm, however,
 * the tree-building process continues until all available nodes in the
 * image are claimed.</li>
 * <li>Cut edges are found.  A cut edge is one in which the node on one end
 * of the edge belongs to a different tree than the node on the other end.</li>
 * <li>For each cut edge, the combined path from the source to t sink is found (there
 * are only two possible trees, therefore the edge representing a border crossing
 * is a link in the path from the source to the sink.</li>
 * <li>The path is augmented in the typical manner, pushing the available flow.</li>
 * <li>The trees are then recreated from the first step.</li>
 * <li>This process is repeated until no flow across the cuts edges occurs.</li>
 * </ul>
 * </p>
 * <p>This implementation uses internal arrays to track the trees and parentage of each node.  While providing
 * simple data access and fixed memory size (2 arrays of size [# of pixels + 2]), the normal
 * pit falls of data integrity and array traversal apply.</p>
 * @author david
 */
public class BorderCut implements GraphCut {

    /**
     * The instance representing the image graph.
     */
    protected ImageGraph graph;
    /**
     * Queue of the cuts found in the image.
     */
    protected Queue<Edge> cuts;
    /**
     * Array of edges from the ImageGraph instance.
     */
    protected LinkedList<Integer>[] edges;
    /**
     * <p>Array to track which tree the given node belongs to.</p>
     * <p>Src and Sink belong to their own trees, all others are initialized
     * as -1 (meaning unclaimed).</p>
     */
    protected int[] tree;
    /**
     * Array to track who is the parent of the given node.  Only one parent allowed
     * per node.
     */
    protected int[] parents;
    /**
     * The vertices from the ImageGraph instance.
     */
    protected int[] verts;
    /**
     * The source node from the ImageGraph instance.
     */
    protected int src;
    /**
     * The sink node from the ImageGraph instance.
     */
    protected int sink;
    

    /**
     * Constructor for the BorderCut implementation.
     * @param graph The ImageGraph instance which the algorithm will process.
     */
    public BorderCut(ImageGraph graph) {
        this.graph = graph;
    }

    /**
     * Initialize internal data and structures.
     *
     */
    protected void init() {
        src = graph.getSrc();
        sink = graph.getSink();
        verts = graph.getVertices();
        edges = graph.getEdges();

        tree = new int[verts.length];
        parents = new int[verts.length];

    }

    /**
     * Calculate the max flow through the graph.
     * @return The max max flow calculated for the given flow graph.
     */
    public double maxFlow() {
        double flow = 0.0;
        double augmented = 1.0;
        init();

        do {
            System.out.println("\tresetting...");
            reset();
            System.out.println("\tbuilding Trees...");
            makeTrees();
            System.out.println("\tfinding cuts...");
            //findCuts();
            cuts = new LinkedList<Edge>();
            cuts.addAll(getCuts());
            System.out.println("\taugmenting...");
            augmented = augment();
            System.out.println("\tAugmented = "+BigDecimal.valueOf(augmented).toEngineeringString());
            flow += augmented;
            System.out.println("\tFlow total is now "+flow);
        } while (augmented > 0);




        return flow;
    }

    /**
     * Reset the tracking structures (parents and tree arrays) to initial values.
     */
    protected void reset() {
        Arrays.fill(tree, -1);
        Arrays.fill(parents, -1);
        tree[src] = src;
        tree[sink] = sink;
    }

    /**
     * <p>Make the connection trees for the source and the sink.</p>
     * <p>A Queue is created, and the first two nodes added are the
     * src and sink nodes.</p>
     * <p>Neighbors are then found for the top node "p" in the queue.  They are processed
     * as follows:
     * <ul>
     * <li>If the neighboring node is unclaimed (tree[q] == -1) and flow is
     * available, then it is "adopted" by the current node being processed.</li>
     * <li>If the node "p" is in the source tree, the available flow is processed as flow
     * from p to q.</li>
     * <li>If the node "p" is in the sink tree, the available flow is processed as flow from
     * q to p.</li>
     * <li>If the node is adopted, then q is put into the Queue, and its neighbors are
     * processed accordingly.</li>
     * <li>Repeat until the Queue is empty.</li>
     * </ul>
     * </p>
     *
     */
    protected void makeTrees() {
        Queue<Integer> active = new LinkedList<Integer>();
        active.offer(src);
        active.offer(sink);
        //Collection<Integer> orphans = treeless();
        int p;
        while (!active.isEmpty() && treelessCount() > 0) {
            //System.out.println("\torphans.size() = "+orphans.size());
            p = active.remove();
            //System.out.println("\t\tp = "+ p);
            if (tree[p] == src) {
                LinkedList<Integer> n = graph.getEdgesFrom(p);
                for (int q : n) {
                    if (tree[q] == -1 && graph.getAvailable(p,q) > 0) {
                        adopt(p,q);
                        active.offer(q);
                        
                    }
                }
            } else if (tree[p] == sink) {
                LinkedList<Integer> n = graph.getEdgesTo(p);
                for (int q : n) {
                    if (tree[q] == -1 && graph.getAvailable(q,p) > 0) {
                        adopt(p,q);
                        active.offer(q);
                        
                    }
                }
            }
            //orphans = treeless();
        }
    }

    /**
     * Adjust the tracking arrays to show parentage accordingly.  parents[child] = parent and
     * tree[child] = tree[parent];
     * @param parent  The node that will be the logical parent.
     * @param child  The node that will be the logical child.
     */
    protected void adopt(int parent, int child) {
        //System.out.println("\t\t"+parent+" adopting "+child);
        tree[child] = tree[parent];
        parents[child] = parent;
    }


    /**
     * Return a count of how many nodes are without parentage (tree[] == -1)
     * @returnNumber of unclaimed nodes.
     */
    protected int treelessCount() {
        int count = 0;
        for (int i=0;i<tree.length;i++) {
            if (tree[i] == -1) count++;
        }
        return count;
    }

    /**
     * <p>Augment the cuts found after the tree assignments.</p>
     * <p>Each cut is process, the path from the source to the sink
     * is constructed, and the flow is augmented along the resulting
     * path in standard fashion.</p>
     * <p>Processing continues until no more edges remain to be augmented.</p>
     * @return The total flow that was pushed from source to sink in this step.
     */
    protected double augment() {
        double total = 0.0;
        double available = 0.0;
        //System.out.println("\t\tConsidering "+cuts.size()+" cuts...");
        int[] path;
        while (!cuts.isEmpty()) {
            Edge e = cuts.remove();
            path = makePath(e.u, e.v);
            available = graph.minAvailable(path);
            if (available > 0) {
                graph.augmentPath(available, path);
                graph.removeFullEdges(path);
                //System.out.println("available = "+Double.toString(available));
                total = total + available;
            }
        }
        //System.out.println("Total augmentation = "+total);
        return total;
    }

    /**
     * <p>Make a path from source to sink that crosses the edge denoted
     * by p and q.</p>
     * <p>Internally, the method evaluates which node is in the source tree, then
     * creates a complete path in the proper orientation.</p>
     * @param p One endpoint of the connecting edge.
     * @param q One endpoint of the connecting edge.
     * @return An integer array denoting the path from source to sink through
     * the edge p-q.
     */
    protected int[] makePath(int p, int q) {
        //System.out.println("\tmakePath considering "+p+" and "+q);
        int[] path = new int[0];
        //two situations possible:  p is in Source Tree
        //or q is in Source Tree
        Vector<Integer> vpath = new Vector<Integer>();
        Stack<Integer> spath = new Stack<Integer>();
        Queue<Integer> tpath = new LinkedList<Integer>();
        if (tree[p] == src && tree[q] == sink) {
            //System.out.println("p is in Src...");
            spath = srcPath(p);
            tpath = sinkPath(q);

        } else if (tree[q] == src && tree[p] == sink) {
            //System.out.println("p is in Sink...");
            spath = srcPath(q);
            tpath = sinkPath(p);
        }
        while (!spath.empty()) {
            vpath.add(spath.pop());
        }
        while (!tpath.isEmpty()) {
            vpath.add(tpath.poll());
        }
        //"arrayify" the vector
        path = ArrayOps.vectorToArray(vpath);
        //System.out.println("\tmakepath path size is "+path.length);
        //printPath("\tmakePath path:  ",path);
        return path;
    }


    /**
     * Create a path from the source to the given node (if available.)
     * @param child  The node to which a path is desired.
     * @return A Stack implementation representing the steps from the src to the given node.
     */
    protected Stack<Integer> srcPath(int child) {
        //System.out.println("srcPath("+child+")");
        Stack<Integer> srcPath = new Stack<Integer>();
        int node = child;
        int parent = parents[node];
        //System.out.println("node = "+node);
        //System.out.println("parent = "+parent);
        while(parent != -1) {
            //System.out.println("\tnode = "+node);
            //System.out.println("\tparent = "+parent);
            srcPath.push(node);
            node = parent;
            parent = parents[node];
        }
        //System.out.println("\tFinal push is "+node);
        srcPath.push(node);
        //Sanity check to make sure that we've ended up at the Source node
        if (node != src) {
            //Return an empty stack
            srcPath = new Stack<Integer>();
        }
        return srcPath;
    }

    /**
     * Create a path from the given node to the sink.  The path is
     * returned as a Queue<Integer> so that the steps from the node
     * to the sink may be traced.
     * @param child The node from which a path is desired.
     * @return A queue representing the path from the node to the sink.
     */
    protected Queue<Integer> sinkPath(int child) {
        //System.out.println("sinkPath("+child+")");
        Queue<Integer> sinkPath = new LinkedList<Integer>();
        int node = child;
        int parent = parents[node];
        while (parent != -1) {
            //System.out.println("\tnode = "+node);
            //System.out.println("\tparent = "+parent);
            //System.out.println("\tsinkPath offering "+node);
            sinkPath.offer(node);
            node = parent;
            parent = parents[node];
        }
        //System.out.println("\tfinal offer is "+node);
        sinkPath.offer(node);
        //Sanity check to ensure we have reached the sink
        if (node != sink) {
            sinkPath = new LinkedList<Integer>();
        }
        return sinkPath;
    }

    /**
     * Check if the given node is in the Source labeling.
     * @param v Node to be checked.
     * @return True if tree[v] == src, otherwise false.
     */
    public boolean inSource(int v) {
        return tree[v] == src;
    }

    /**
     * Check if the given node is in the Sink labeling.
     * @param v Node to be checked.
     * @return True if tree[v] == sink, otherwise false.
     */
    public boolean inSink(int v) {
        return tree[v] == sink;
    }

    /**
     * Check if the given node is in the given labeling.
     * @param label Label value to be compared.
     * @param v Node to be checked.
     * @return True if tree[v] == label, otherwise false.
     */
    public boolean inLabel(int label, int v) {
        return tree[v] == label;
    }

    /**
     * <p>Find and return a Collection of all edges in which the endpoints
     * are in different labeling trees.</p>
     * @return A Collection<Edge> containing all edges with different-tree endpoints.
     */
    public Collection<Edge> getCuts() {
        //System.out.println("\tgetCuts()");
        Vector<Edge> c = new Vector<Edge>();

        //Alternate method for getting the cuts
        int v;
        int utree;
        for (int u=0;u<edges.length;u++) {
            if (edges[u] != null) {
                utree = tree[u];
                Iterator<Integer> it = edges[u].iterator();
                while (it.hasNext()) {
                    v = it.next();
                    if (utree != tree[v]) {
                        c.add(new Edge(u,v));
                    }
                }

            }
        }
        //System.out.println("\t\treturning "+c.size()+" total cuts...");
        return c;
    }

}
