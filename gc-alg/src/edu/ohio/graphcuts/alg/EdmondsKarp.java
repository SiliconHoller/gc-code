/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg;

import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.ImageGraph;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;



/**
 * Implementation of the Edmonds-Karp min-cut algorithm.
 */
public class EdmondsKarp implements GraphCut {

    protected ImageGraph graph;
    protected int[] vertices;
    protected LinkedList<Integer>[] edges;
    protected int[] parents;
    protected double eps = 1e-4;
    protected int src;
    protected int sink;

    public EdmondsKarp(ImageGraph graph) {
        this.graph = graph;
        this.edges = graph.getEdges();
        this.vertices = graph.getVertices();
        this.src = graph.getSrc();
        this.sink = graph.getSink();
    }

    public double maxFlow() {
        double max = 0.0;
        parents = new int[vertices.length];
        int[] path = bfs(src,sink);
        while (path.length > 0) {
            double minavail = graph.minAvailable(path);
            max += minavail;
            graph.augmentPath(minavail,path);
            graph.removeFullEdges(path);
            path = bfs(src,sink);
        }

        return max;
    }

    public boolean inSource(int v) {
        return (bfs(src,v).length > 0);
    }

    public boolean inSink(int u) {
        return !inSource(u);
    }

    protected int[] bfs(int u, int v) {
        int[] path = new int[0];
        Arrays.fill(parents,-1);
        Queue<Integer> q = new LinkedList<Integer>();
        q.offer(u);
        while (!q.isEmpty()) {
            int qu = q.peek();
            LinkedList<Integer> outedges = edges[qu];
            if (outedges != null) {
                for (int qv: outedges) {
                    if (parents[qv] == -1 && graph.getAvailable(qu,qv) > eps) {
                        //we've not seen this one before
                        //And flow is available
                        parents[qv] = qu;
                        if (qv == v) {
                            return makePath();
                        }
                        q.offer(qv);
                    }
                }
            }
            q.poll();
        }
        return path;
    }


    protected int[] makePath() {
        Vector<Integer> path = new Vector<Integer>();
        int v = sink;
        while (v != -1) {
            path.add(v);
            v = parents[v];
        }
        int[] p = new int[path.size()];
        for (int i=0;i<p.length;i++) {
            p[i] = path.lastElement();
            path.removeElementAt(path.size() -1);
        }
        return p;
    }

    public boolean inLabel(int label, int v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<Edge> getCuts() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
