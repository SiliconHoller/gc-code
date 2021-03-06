/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
 *
 * @author david
 */
public class Cuts {

    protected ImageGraph graph;
    protected GraphCut gc;
    protected int[][] cuts;
    protected int[] tree;

    public Cuts(ImageGraph graph, GraphCut gc) {
        this.graph = graph;
        this.gc = gc;
        cuts = new int[0][0];
        tree = new int[0];
    }

    public int getContiguousCount() {
        if (cuts.length == 0) {
            cuts = getCuts();
        }
        int size = 0;
        if (tree.length == 0) {
            processCuts();
        }

        Vector<Integer> count = new Vector<Integer>();
        for (int i=0;i<tree.length;i++) {
            if (!count.contains(tree[i])) {
                count.add(tree[i]);
            }
        }
        size = count.size();
        return size;
    }

    protected void processCuts() {
        //variation on Kruskal's algorithm
        tree = new int[cuts.length];
        Arrays.fill(tree,-1);
        //Cascade down the array--each edge
        //checks following edges, assigning them to it's
        //own tree if there's a match
        for (int i=0;i<tree.length;i++) {
            int u1 = cuts[i][0];
            int v1 = cuts[i][1];
            for (int j=i+1;j<tree.length;j++) {
                int u2 = cuts[j][0];
                int v2 = cuts[j][1];
                if (u1 == u2 || v1 == v2 || u1 == v2 || v1 == u2) {
                    if (tree[i] == -1 && tree[j] == -1) {
                        //neither belong to a tree--set to i index
                        tree[i] = i;
                        tree[j] = i;
                    } else if (tree[i] == -1 && tree[j] != -1) {
                        //Assign i to j's tree
                        tree[i] = tree[j];
                    } else if (tree[i] != -1 && tree[j] == -1) {
                        //Assign j = i's tree
                        tree[j] = tree[i];
                    } else {
                        //Combine the two trees
                        combineTrees(tree[i],tree[j]);
                    }
                }
            }
        }

    }

    protected void combineTrees(int newTree, int oldTree) {
        for (int i=0;i<tree.length;i++) {
            if (tree[i] == oldTree) {
                tree[i] = newTree;
            }
        }
    }

    public int[] getTrees() {
        return tree;
    }
    
    public int[][] getCuts() {
        if (cuts.length != 0) {
            return cuts;
        }
        LinkedList<Integer>[] edges = graph.getEdges();
        Vector<Edge> vcuts = new Vector<Edge>();
        int src = graph.getSrc();
        int sink = graph.getSink();
        for (int u = 0;u<edges.length;u++) {
            //Don't care about src edges
            if (u != src) {
                LinkedList<Integer> vees = edges[u];
                boolean usrc = gc.inSource(u);
                if (vees != null) {
                    Iterator<Integer> it = vees.iterator();
                    while (it.hasNext()) {
                        int v = it.next();
                        //Don't care about edges to sink
                        if (v != sink) {
                            boolean vsink = gc.inSink(v);
                            if ((usrc &&  vsink) || (!usrc && !vsink)) {
                                vcuts.add(new Edge(u,v));
                            }
                        }
                    }
                }
            }
        }
        //Now let's array-ify the cut edge list
        int[][] cutarray = new int[vcuts.size()][2];
        Edge ce;
        for (int i=0;i<cutarray.length;i++) {
            ce = vcuts.elementAt(i);
            cutarray[i][0] = ce.u;
            cutarray[i][1] = ce.v;
        }
        return cutarray;
    }

}
