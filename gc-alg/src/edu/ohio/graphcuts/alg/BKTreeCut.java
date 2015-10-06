/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg;

import edu.ohio.graphcuts.ArrayOps;
import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.ImageGraph;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

/**
 *
 * @author david
 */
public class BKTreeCut implements GraphCut {

    protected ImageGraph graph;
    protected int[] parents;
    protected int[] tree;
    protected LinkedList<Integer>[] edges;
    protected int[] vertices;
    protected int sink;
    protected int src;
    protected Queue<Integer> active;
    protected Queue<Integer> orphan;

    public BKTreeCut(ImageGraph graph) {
        this.graph = graph;
    }

    protected void init() {
        vertices = graph.getVertices();
        src = graph.getSrc();
        sink = graph.getSink();
        edges = graph.getEdges();

        parents = new int[vertices.length];
        tree = new int[vertices.length];
        //Init all parents and trees to -1
        for (int i=0;i<parents.length;i++) {
            parents[i] = -1;
            tree[i] = -1;
        }

        //set src and sink to own tree
        tree[src] = src;
        tree[sink] = sink;

        active = new LinkedList<Integer>();
        orphan = new LinkedList<Integer>();
    }

    public double maxFlow() {
        double maxFlow = 0.0;

        init();
        active.offer(src);
        active.offer(sink);
        int[] path = new int[1];
        while (path.length != 0) {
            //System.out.println("\tactive size = "+active.size());
            //System.out.println("\tgrowing...");
            path = grow();
            //printPath("path from grow:  ",path);
            if (path.length == 0) return maxFlow;
            //System.out.println("\taugmenting...");
            maxFlow += augment(path);
            //System.out.println("\tadopting...");
            adopt();
        }
        return maxFlow;
    }

    public boolean inSource(int v) {
        return (tree[v] == src);
    }

    public boolean inSink(int v) {
        return (tree[v] == sink);
    }

    protected boolean hasRoot(int child, int root) {
        int node = child;
        int parent = parents[node];
        while (parent != -1) {
            node = parent;
            parent = parents[node];
        }
        return node == root;
    }

    protected int[] grow() {
        int[] path = new int[0];
        while (!active.isEmpty()) {
            //System.out.println("\tactive size = "+active.size());
            int p = active.peek();
            //System.out.println("p="+p);
            LinkedList<Integer> pedges = edges[p];
            if (pedges != null) {
                for (int q : pedges) {

                    //System.out.println("q="+q);
                    if (graph.getAvailable(p,q) > 0.0) {
                        if (tree[q] == -1) {
                            tree[q] = tree[p];
                            parents[q] = p;
                            active.offer(q);
                        } else if (tree[q] != -1 && tree[q] != tree[p]) {
                            //System.out.println("\tp = "+p+" q = "+q);
                            //System.out.println("\ttree[p] = "+tree[p]+" tree[q] = "+tree[q]);
                            return makePath(p,q);
                        }
                    }
                }
            }
            active.remove();
        }

        return path;
    }

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

    protected double augment(int[] path) {
        //System.out.println("augmenting path...");
        //printPath("augment():  ",path);
        double minFlow = graph.minAvailable(path);
        graph.augmentPath(minFlow,path);
        graph.removeFullEdges(path);
        int len = path.length - 1;
        int p;
        int q;
        for (int i=0;i<len;i++) {
            p = path[i];
            q = path[i+1];
            //System.out.println("\tconsidering p="+p+" q="+q);
            if (graph.getAvailable(p, q) <= 0.0) {
                //System.out.println("\t\tno available flow");
                if (tree[p] == tree[q] && tree[p] == src) {
                    //System.out.println("\t\tp in src, q in src");
                    parents[q] = -1;
                    orphan.offer(q);
                } else if (tree[p] == tree[q] && tree[p] == sink) {
                    //System.out.println("\t\tp in sink, q in sink");
                    parents[p] = -1;
                    orphan.offer(p);
                }
            }
        }
        return minFlow;
    }

    protected void adopt() {
        //System.out.println("adopting...");
        while (!orphan.isEmpty()) {
            //System.out.println("orpans.size() = "+orphan.size());
            int p = orphan.remove();
            process(p);
        }

    }

    protected void process(int p) {
        LinkedList<Integer> neighbors = edges[p];
        if (neighbors != null) {
            for (int q : neighbors) {

                if (tree[q] == tree[p]) {
                    if (graph.getAvailable(q,p) > 0) {
                        if (inSource(q) || inSink(q)) {
                            if (!inLineage(p,q)) {
                                parents[p] = q;
                                break;
                            }
                        }
                        //active.offer(q);
                        //if (parents[q] == p) {
                        //    orphan.offer(q);
                        //    parents[q] = -1;
                        //}
                    }

                }

            }
            if (parents[p] == -1) {
                //Didn't find a parent
                for (int q : neighbors) {

                    if (tree[q] == tree[p]) {
                        if (graph.getAvailable(q,p) > 0) {
                            active.offer(q);
                        }
                        if (parents[q] == p) {
                            orphan.offer(q);
                            parents[q] = -1;
                        }
                    }
                }
                tree[p] = -1;
                active.remove(p);
            }
        }
    }

    protected boolean inLineage(int p,int q) {
        //Is p in the lineage of q?
        int child = q;
        int parent = parents[child];
        while (parent != -1) {
            if (parent == p) {
                return true;
            }
            child = parent;
            parent = parents[child];
        }

        return false;
    }
    
    protected void printPath(String method, int[] path) {
        //System.out.print(method+" ");
        for (int i=0;i<path.length;i++) {
            //System.out.print(path[i]+" ");
        }
        //System.out.println();
    }

    public boolean inLabel(int label, int v) {
        return (tree[v] == label);
    }

    public Collection<Edge> getCuts() {
        Vector<Edge> c = new Vector<Edge>();

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
