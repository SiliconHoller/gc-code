/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.data.PngOps;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import javax.imageio.ImageIO;

/**
 * <p>Class to attempt to get an approximate count of
 * object in an image.  This count is based upon a form of
 * Prim's algorithm, wherein a selected point in the graph
 * is chosen, and any neighbors have the choice of joining that
 * points tree or waiting for the next candidate tree to contact
 * it.</p>
 * <p>More formally, the process works as follows:
 * <ul>
 * <li>Select two points in the image with the highest gradient, then set their gradient to 0.0.</li>
 * <li>Add each of those points to the Tree-set as the root and the processing Queue.</li>
 * <li>Look at the neighbors of each point in the queue.</li>
 * <li>If, according to the Markov CRF transition matrix, the neighbor has the highest likelihood
 * of transitioning to the point being processed, assign it to same tree as the parent,
 * add it to the processing queue, and set the corresponding gradient to 0.0.  If not, move one.</li>
 * <li>Continue until the queue is empty.</li>
 * <li>Repeat for the next tree root.</li>
 * <li>Repeat the entire process until the highest gradient returned is 0.0</li>
 * </ul>
 * </p>
 * <p>While crude, this method should give a rough approximate of all sharply-defined
 * objects in the image.</p>
 * @author david
 */
public class ObjectCount {

    private ImageGraph graph;
    private MarkovCRF markov;
    private Gradient gradient;
    private double[][] gradients;
    private double[][] transitions;
    private LinkedList<Integer>[] edges;
    private int[] verts;
    private int s;
    private int t;
    private int n;
    private int[] trees;
    private int[] parents;
    private Queue<Integer> active;
    private Vector<Integer> roots;

    public ObjectCount(ImageGraph graph, boolean init) {
        this.graph = graph;
        if (init) {
            init();
        }
    }

    public ObjectCount(ImageGraph graph) {
        this(graph,true);
    }

    /*
     * For lazy instantiation--a lot of processing is needed
     * to initialize the gradient and CRF matrices.
     */
    private void init() {
        //Basic info
        s = graph.getSrc();
        t = graph.getSink();
        verts = graph.getVertices();
        edges = graph.getEdges();
        n = verts.length;

        //computation-intensive data
        markov = new MarkovCRF(graph);
        transitions = markov.posteriorTransitions();
        gradient = new Gradient(graph);
        gradients = gradient.getGradients();

        graph.setCapacities(gradients);

        //tracking matrices
        trees = new int[n];
        parents = new int[n];
        Arrays.fill(trees,-1);
        Arrays.fill(parents,-1);
        roots = new Vector<Integer>();
    }

    public int count() {
        System.out.println("ObjectCount.count()...");
        active = new LinkedList<Integer>();
        Edge e = gradient.getHighestGradient();
        while (e.wt > 0.0) {
            System.out.println("\tFirst point is "+e.u);
            System.out.println("\tSecond point is "+e.v);
            graph.addFlow(-1*e.wt, e.u, e.v);
            if (!roots.contains(e.u) && parents[e.u] == -1 && trees[e.u] == -1) {
                System.out.println("\t"+e.u+" is now a root...");
                roots.add(e.u);
                trees[e.u] = e.u;
                active.offer(e.u);
                clearVals(e.u);
            }
            if (!roots.contains(e.v) && parents[e.v] == -1 && trees[e.v] == -1) {
                System.out.println("\t"+e.v+" is now a root...");
                roots.add(e.v);
                trees[e.v] = e.v;
                active.offer(e.v);
                clearVals(e.v);
            }

            System.out.print("\tPoints Queued:  ");
            while (!active.isEmpty()) {
                System.out.print(active.size()+" ");
                grow(active.remove());
            }
            System.out.println();
            System.out.println("\tGetting a new edge...");
            e = gradient.getHighestGradient();
        }
        return roots.size();
    }

    private void grow(int p) {
        LinkedList<Integer> children = edges[p];
        int q = 0;
        if (children != null) {
            Iterator<Integer> it = children.iterator();
            while (it.hasNext()) {
                q = it.next();
                if (q != t && parents[q] == -1 && trees[q] == -1) {

                    if (isHighestBond(p,q)) {
                        bind(p,q);
                        active.offer(q);
                    }
                }
            }
        }
    }
    
    private boolean isHighestBond(int root, int child) {
        LinkedList<Integer> oe = edges[child];
        double[] ot = transitions[child];
        int hivert = -1;
        double hival = 0.0;
        if (oe != null) {
            Iterator<Integer> it = oe.iterator();
            int k;
            //First, check to see if the root is there, and set it's value
            while (it.hasNext()) {
                k = it.next();
                if (k == root) {
                    hivert = root;
                    hival = ot[k];
                }
            }
            //Now see if any others are higher
            Iterator<Integer> sit = oe.iterator();
            while (sit.hasNext()) {
                k = sit.next();
                if (ot[k] > hival) {
                    hivert = k;
                    hival = ot[k];
                }
            }
        }
        return hivert == root;
    }

    private void bind(int p, int q) {
        //set parent and tree
        parents[q] = p;
        trees[q] = trees[p];

        //clear gradients and transitions
        clearVals(q);
    }

    private void clearVals(int p) {
        double[] grads = gradients[p];
        double[] trans = transitions[p];
        for (int i=0;i<grads.length;i++) {
            grads[i] = 0.0;
        }
        for (int j=0;j<trans.length;j++) {
            trans[j] = 0.0;
        }
    }

    public static void main(String[] args) {
        try {
            String fname = args[0];
            System.out.println("Loading image "+fname);
            BufferedImage img = ImageIO.read(new File(fname));

            System.out.println("Creating 4-neighbor graph...");
            PngOps ops = new PngOps();
            ImageGraph graph = ops.get4NGraph(img);

            System.out.println("Creating ObjectCount...");
            ObjectCount counter = new ObjectCount(graph, false);

            System.out.println("Starting init process...");
            counter.init();

            System.out.println("Getting count...");
            int num = counter.count();
            System.out.println("We're finding "+num+" objects in the image.");
            System.out.println("(Remember, that includes the background!)");

            System.out.println("Creating resultant image...");
            BufferedImage rimg = ops.blankImage(img.getWidth(), img.getHeight());
            int gval = 0;
            int x = 0;
            int y = 0;
            for (int i=0;i<counter.trees.length;i++) {
                if (counter.trees[i] != -1) {
                    gval = (counter.roots.indexOf(counter.trees[i])*255)/num;
                    x = graph.getX(i);
                    y = graph.getY(i);
                    rimg.setRGB(x,y,ops.makeGrayPixel(gval));
                }
            }
            ImageIO.write(rimg, "png", new File(fname+"-splits.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


}
