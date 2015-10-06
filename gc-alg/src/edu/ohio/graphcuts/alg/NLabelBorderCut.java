/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg;

import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.NLabelImageGraph;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

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
 * <p>This implementation uses a UnionFind instance to track labeling-trees.</p>
 * @author david
 */
public class NLabelBorderCut implements GraphCut {

     /**
     * The instance representing the image graph.
     */
    protected NLabelImageGraph graph;
    /**
     * Queue of the cuts found in the image.
     */
    protected Queue<Edge> cuts;
    /**
     * Array of edges from the ImageGraph instance.
     */
    protected LinkedList<Integer>[] edges;
    /**
     * Label list used in the UnionFind instance.
     */
    protected int[] labels;
    /**
     * UnionFind instance to perform tree-labeling and query operations.
     */
    protected UnionFind uf;
    /**
     * The vertices from the ImageGraph instance.
     */
    protected int[] verts;

    

    /**
     * Constructor for the BorderCut implementation.
     * @param graph The ImageGraph instance which the algorithm will process.
     */
    public NLabelBorderCut(NLabelImageGraph graph) {
        this.graph = graph;
    }

   /**
    * Initialize internal data and structures.
    *
    */
   protected void init() {
        verts = graph.getVertices();
        edges = graph.getEdges();

        labels = graph.getLabels();

        uf = new UnionFind(verts.length,labels);

    }

   public UnionFind getUnionFind() {
       return uf;
   }

    /**
     * Calculate the max flow through the graph.
     * @return The max max flow calculated for the given flow graph.
     */
   public double maxFlow() {
        double flow = 0.0;
        double augmented = 1.0;
        double passflow = 1.0;
        init();
        while (passflow > 0.0) {
            passflow = 0.0;
            uf = new UnionFind(verts.length,labels);
            do {
                //System.out.println("\tresetting...");
                reset();
                //System.out.println("\tbuilding Trees...");
                makeTrees();
                //System.out.println("\tfinding cuts...");
                //findCuts();
                cuts = getCuts();
                //System.out.println("\taugmenting...");
                augmented = augment();
                //System.out.println("\tAugmented = "+BigDecimal.valueOf(augmented).toEngineeringString());
                passflow += augmented;
                //System.out.println("\tFlow total is now "+flow);
            } while (augmented > 0);

            flow += passflow;
       }



        return flow;
    }

    /**
    * Reset the UnionFind instance to start (re)building trees.
    */
    protected void reset() {
        uf.resetTrees();
    }

    /**
     * <p>Make the connection trees for the source and the sink.</p>
     * <p>A Queue is created, and the first two nodes added are the
     * src and sink nodes.</p>
     * <p>Neighbors are then found for the top node "p" in the queue.  They are processed
     * as follows:
     * <ul>
     * <li>If the node is in the src tree (UnionFind.find(src,p)):
     * <ul>
     * <li>Edges from the p node to its neighbors are extracted.</li>
     * <li>If flow is available from p to q and union(p,q) != 0 (a successful union), q is added
     * to the processing queue.</li>
     * </ul>
     * </li>
     * <li>If he node is in the sink tree (UnionFind.find(sink,p)):
     * <ul>
     * <li>Edge <em>to</em>p from neighbors are extracted.</li>
     * <li>If flow is available from q to p and union(p,q) != 0 (a successful union),
     * q is added to the processing queue.</li>
     * </ul>
     * </li>
     * </ul>
     * The entire process is repeated until the queue is empty.
     * </p>
     *
     */
    protected void makeTrees() {
        Queue<Integer> active = new LinkedList<Integer>();

        for (int i=0;i<labels.length;i++) {
            active.offer(labels[i]);
        }

        //Collection<Integer> orphans = treeless();
        int p;
        //while there are nodes to process and not all nodes have been claimed
        while (!active.isEmpty() && uf.countRoots() > labels.length) {
            //System.out.println("\torphans.size() = "+orphans.size());
            //System.out.println("\t\t# nodes to process = "+active.size());
            //System.out.println("\t\t# independent trees = "+uf.countRoots());
            p = active.remove();
            //System.out.println("\t\tp = "+ p);
            int proot = uf.find(p);
            //System.out.println("p = "+p+"\tproot = "+proot);
            LinkedList<Integer> n = graph.getEdgesFrom(p);
            //System.out.println("looking at "+n.size()+" edges.");
            for (int q : n) {
                int qroot = uf.find(q);
                //System.out.println("available(p,q) = "+graph.getAvailable(p,q));
                if (proot != qroot && !graph.isLabel(qroot) && graph.getAvailable(p, q) > 0) {
                    //System.out.println("\t"+p+" taking "+q);
                    uf.union(p, q);
                    active.offer(q);
                }
            }
            
        }
        
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
        Edge e;
        while (!cuts.isEmpty()) {
            e = cuts.remove();
            //if (graph.isLabel(uf.find(e.u)) && graph.isLabel(uf.find(e.v))) {
                path = makePath(e.u, e.v);
                //ArrayOps.printArray(path);
                available = graph.minAvailable(path);
                if (available > 0.0) {
                    graph.augmentPath(available, path);
                    graph.removeFullEdges(path);
                    total = total + available;
                }
            //}
            //System.out.println("available = "+Double.toString(available));           
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

        int[] spath = uf.pathFromRoot(p);

        int[] tpath = uf.pathToRoot(q);

        int[] fpath = concat(spath,tpath);

        int[] rpath = reverse(fpath);

        int[] path = concat(fpath,rpath);
        //ArrayOps.printArray(path);
        return path;
    }

    protected int[] concat(int[] path1, int[] path2) {
        int[] npath = new int[path1.length+path2.length];
        System.arraycopy(path1, 0, npath, 0, path1.length);
        System.arraycopy(path2, 0, npath, path1.length, path2.length);
        return npath;
    }

    protected int[] reverse(int[] path) {
        int[] rpath = new int[path.length-1];
        int end = rpath.length;
        for (int i=0;i<rpath.length;i++) {
            rpath[i] = path[end-i-1];
        }
        return rpath;
    }
    /**
     * Returns true if the given node is in the source labeling.
     * @param v Node to be evaluated.
     * @return true if the node is in the source labeling, otherwise false.
     */
    public boolean inSource(int v) {
        throw new IllegalArgumentException("inSrc() not used in NLabelBorderCut");
    }

    /**
     * Returns true if the given node is in the sink labeling.
     * @param v The node to be evaluated.
     * @return true if the node is in the sink labeling, otherwise false.
     */
    public boolean inSink(int v) {
        throw new IllegalArgumentException("inSink() not used in NLabelBorderCut");
    }

    /**
     * Returns true if the two points/nodes are in the same tree.
     * @param label Labeling tree against which to check the node.
     * @param v Node to be checked.
     * @return true if the node is in the same label as the one given;otherwise, false.
     */
    public boolean inLabel(int label, int v) {
        return uf.find(label, v);
    }


    /**
     * <p>Find and return a LinkedList of all edges in which the endpoints
     * are in different labeling trees.</p>
     * @return A LinkedList containing all edges with different-tree endpoints.
     */
    public LinkedList<Edge> getCuts() {
        //System.out.println("\tgetCuts()");
        LinkedList<Edge> c = new LinkedList<Edge>();

        //Alternate method for getting the cuts
        int v;
        for (int u=0;u<edges.length;u++) {
            if (edges[u] != null) {
                Iterator<Integer> it = edges[u].iterator();
                while (it.hasNext()) {
                    v = it.next();
                    if (!uf.find(u, v)) {
                        c.add(new Edge(u,v));
                    }
                }

            }
        }
        //System.out.println("\t\treturning "+c.size()+" total cuts...");
        return c;
    }

}
