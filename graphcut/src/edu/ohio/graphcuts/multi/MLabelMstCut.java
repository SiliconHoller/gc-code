/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.multi;

import edu.ohio.graphcuts.alg.*;
import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.util.ArrayOps;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

/**
 *
 * @author david
 */
public class MLabelMstCut implements GraphCut {

    private MLabelImageGraph graph;
    private Queue<Edge> cuts;
    private LinkedList<Integer>[] edges;
    private int[] tree;
    private int[] parents;
    private int[] verts;
    private int[] labels;
    

    public MLabelMstCut(MLabelImageGraph graph) {
        this.graph = graph;
    }

    private void init() {
        labels = graph.getLabels();
        verts = graph.getVertices();
        edges = graph.getEdges();

        tree = new int[verts.length];
        parents = new int[verts.length];

    }

    public double maxFlow() {
        double flow = 0.0;
        double augmented = 1.0;
        init();
        
        while (augmented > 0) {
            System.out.println("\tresetting...");
            reset();
            System.out.println("\tbuilding MST...");
            makeMST();
            System.out.println("\tfinding cuts...");
            //findCuts();
            cuts = new LinkedList<Edge>();
            cuts.addAll(getCuts());
            System.out.println("\taugmenting...");
            augmented = augment();
            System.out.println("\taugmented a total of "+augmented);
            flow = flow + augmented;
        }



        return flow;
    }
    
    private void reset() {
        Arrays.fill(tree, -1);
        Arrays.fill(parents, -1);
        for (int i=0;i<labels.length;i++) {
            tree[labels[i]] = labels[i];
        }
    }

    private void makeMST() {
        Queue<Integer> active = new LinkedList<Integer>();
        
        for (int i=0;i<labels.length;i++) {
            active.offer(labels[i]);
        }
        Collection<Integer> orphans = treeless();
        while (!active.isEmpty() && !orphans.isEmpty()) {
            //System.out.println("\tactive size is "+active.size());
        
            System.out.println("\torphans.size() = "+orphans.size());
            int p = active.remove();
            //System.out.println("\t\tp = "+ p);
            if (tree[p] != -1) {
                LinkedList<Integer> n = graph.getEdgesFrom(p);
                for (int q : n) {
                    //System.out.println("\t\tq = "+q);
                    if (tree[q] == -1 && graph.getAvailable(p,q) > 0) {
                        adopt(p,q);
                        active.offer(q);
                        
                    }
                }
            }

            orphans = treeless();
        }
    }

    private void adopt(int parent, int child) {
        //System.out.println("\t\t"+parent+" adopting "+child);
        tree[child] = tree[parent];
        parents[child] = parent;
    }

    private Collection<Integer> treeless() {
        Vector<Integer> orphans = new Vector<Integer>();
        for (int i=0;i<tree.length;i++) {
            if (tree[i] == -1) orphans.add(i);
        }

        return orphans;
    }

    private int minNeighbor(int p, LinkedList<Integer> neighbors) {
        //System.out.println("\t\tlooking at "+neighbors.size()+" neighbors...");
        int minq = -1;
        double minres = Double.POSITIVE_INFINITY;
        double available = 0.0;
        for (int q:neighbors) {
            available = graph.getAvailable(p, q);
            //System.out.println("\t\tavailable("+p+","+q+") = "+available);
            if (available > 0 && available <= minres) {
                //System.out.println("\t\tnew minimum = "+q);
                minq = q;
                minres = available;
            }
        }
        
        return minq;
    }

    private int minNeighbor(LinkedList<Integer> neighbors, int q) {
        int minp = -1;
        double minres = Double.POSITIVE_INFINITY;
        double available = 0.0;
        for (int p: neighbors) {
            available = graph.getAvailable(p,q);
            if (available > 0 && available <= minres) {
                minp = p;
                minres = available;
            }
        }
        return minp;
    }

    /**
    private void findCuts() {
        cuts = new LinkedList<Edge>();
        double avail = 0.0;
        Collection<Edge> poss = getCuts();
        Iterator<Edge> it = poss.iterator();
        while (it.hasNext()) {
            Edge e = it.next();
            avail = graph.getAvailable(e.u, e.v);
            if (avail > 0) cuts.offer(e);
        }
    }
    */
    
    private double augment() {
        double total = 0.0;
        double available = 0.0;
        //System.out.println("\t\tConsidering "+cuts.size()+" cuts...");
        while (!cuts.isEmpty()) {
            Edge e = cuts.remove();
            int[] path = makePath(e.u, e.v);
            available = graph.minAvailable(path);
            graph.augmentPath(available, path);
            graph.removeFullEdges(path);
            total = total + available;
        }
        return total;
    }

    protected int[] makePath(int p, int q) {
        //System.out.println("\tmakePath considering "+p+" and "+q);
        int[] path = new int[0];
        //two situations possible:  p is in Source Tree
        //or q is in Source Tree
        Vector<Integer> vpath = new Vector<Integer>();
        Stack<Integer> spath = new Stack<Integer>();
        Queue<Integer> tpath = new LinkedList<Integer>();

        spath = srcPath(p);
        tpath = sinkPath(q);

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
        return srcPath;
    }

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
        return sinkPath;
    }

    public boolean inSource(int v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean inSink(int v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean inLabel(int label, int v) {
        return tree[v] == label;
    }

    public Collection<Edge> getCuts() {
        //System.out.println("\tgetCuts()");
        Vector<Edge> c = new Vector<Edge>();
        int[][] earray = graph.getEdgeArray();
        for (int u=0;u<earray.length;u++) {
            //System.out.println("\t\tConsidering u="+u+", tree[u] = "+tree[u]);
            if (!graph.isLabel(u)) {
                int[] varr = earray[u];
                for (int v : varr) {
                    //System.out.print("\t\t\tv="+v);
                    if (!graph.isLabel(v)) {
                        //System.out.println("\t tree[v] = "+tree[v]);
                        if (tree[u] != tree[v]) {
                            c.add(new Edge(u,v));
                        }
                    }
                }
            }
        }
        System.out.println("\t\treturning "+c.size()+" total cuts...");
        return c;
    }

}
