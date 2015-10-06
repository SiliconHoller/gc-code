/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.ImageGraph;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *  <p>This class performs Conditional Random Field calculations over
 * <strong>in-image intensity transitions.</strong></p>
 * <p>Due to the implementation-dependent aspect of source and sink edges, all such
 * edges are left out of this class.  This means that the operations performed and
 * values returned are the most common and basic to all image graphs created.  For example,
 * the method getTransitionSum(int pos) returns the sum of the transition probabilities
 * for that position, based upon the intensity at that position and the surrounding
 * intensities.</p>
 * <p>For example, if pos has a value of 125, and is surrounded by four
 * neighbors of values 125,129,180, and 201, (and the transition[u][v] values are
 * .48,.20,.12,and .03, respectively, then the value returned will be (0.48+0.2+0.12+0.03 =)
 * 0.83.  Edges to the sink will be ignored, since source-sink association is implementation
 * dependent.</p>
 * <p>The same basic design requirement is behind each method.</p>
 * @author david
 */
public class MarkovCRF {

    protected double[][] transitions;
    protected int[][] counts;
    protected ImageGraph graph;
    protected int[] verts;
    protected LinkedList<Integer>[] edges;
    protected int range = 256;
    protected int s;
    protected int t;
    protected int n;

    public MarkovCRF(ImageGraph graph) {
        this.graph = graph;
        this.verts = graph.getVertices();
        this.edges = graph.getEdges();
        this.s = graph.getSrc();
        this.t = graph.getSink();
        this.n = graph.getWidth()*graph.getHeight();
        init();
    }

    public double[][] getTransitionMatrix() {
        return transitions;
    }

    public double[][] getTransitionCopy() {
        double[][] tcopy = new double[range][range];
        for (int i=0;i<range;i++) {
            for (int j=0;j<range;j++) {
                tcopy[i][j] = transitions[i][j];
            }
        }

        return tcopy;
    }

    public int[][] getCounts() {
        return counts;
    }

    public int[][] getCountCopy() {
        int[][] ccopy = new int[range][range];
        for (int i=0;i<range;i++) {
            for (int j=0;j<range;j++) {
                ccopy[i][j] = counts[i][j];
            }
        }
        return ccopy;
    }


    public double getProbability(int u, int v) {
        return transitions[u][v];
    }

    public double getTransitionSum(int pos) {
        double sum = 0.0;
        int u = verts[pos];
        LinkedList<Integer> outs = edges[pos];
        if (outs != null) {
            Iterator<Integer> it = outs.iterator();
            while (it.hasNext()) {
                int vpos = it.next();
                if (vpos != t) {
                    sum += getProbability(u,verts[vpos]);
                }
            }
        }
        return sum;
    }

    private void init() {
        fillCounts();
        transitions = new double[range][range];
        double rowTotal;
        //Convert to probability of each transition from u to v
        for (int i=0;i<range;i++) {
            rowTotal = getRowTotal(i);
            if (rowTotal != 0.0) {
                for (int j=0;j<range;j++) {
                    transitions[i][j] = ((double)counts[i][j])/rowTotal;
                }
            }
        }
    }

    protected double getRowTotal(int row) {
        int sum = 0;
        for (int i=0;i<range;i++) {
            sum += counts[row][i];
        }
        return (double) sum;
    }

    /*
     * Creates the raw counts of the transitions from
     * one intensity to another.  Used to calculate the
     * estimation of the Markov Conditional Random Field
     * Transition matrix.
     */
    protected void fillCounts() {
        counts = new int[range][range];
        //int u = 0;
        //int v = 0;
        LinkedList<Integer> outs;
        for (int i=0;i<s;i++) {
            //u = verts[i];
            outs = edges[i];
            if (outs != null) {
                Iterator<Integer> it = outs.iterator();
                while (it.hasNext()) {
                    int j = it.next();
                    if (j != t) {
                        //System.out.println(u);
                        //System.out.println(v);
                        counts[verts[i]][verts[j]]++;
                    }
                }
            }
        }

    }

    public double[][] priorTransitions() {
        int u = 0;
        int v = 0;
        LinkedList<Integer> uv;
        int vpos;
        for (int i=0;i<n;i++) {
            if (i != s) {
                u = verts[i];
                uv = edges[i];
                if (uv != null) {
                    Iterator<Integer> it = uv.iterator();
                    while (it.hasNext()) {
                        vpos = it.next();
                        if (vpos != t) {
                            v = verts[vpos];
                            graph.setCapacity(i,v,transitions[u][v]);
                        }
                    }
                }
            }
        }
        return graph.getCapacities();
    }

    public static void fillPriorCapacities(ImageGraph graph) {
        MarkovCRF markov = new MarkovCRF(graph);
        double[][] pcaps = markov.priorTransitions();
        graph.setCapacities(pcaps);
    }

    public double[][] posteriorTransitions() {
        double[][] priors = priorTransitions();
        double[][] pcaps = graph.getNewFlows();
        double transSum = 0.0;
        for (int i=0;i<edges.length;i++) {
            if (i != s) {
                LinkedList<Integer> oe = edges[i];
                if (oe != null) {
                    transSum = getTransitionSum(i);
                    double[] prow = priors[i];
                    for (int j=0;j<prow.length;j++) {
                        
                        pcaps[i][j] = (priors[i][j])/transSum;
                        
                    }
                }
            }
        }
        return pcaps;
    }

    public static void fillPosteriorCapacities(ImageGraph graph) {
        MarkovCRF markov = new MarkovCRF(graph);
        double[][] pcaps = markov.posteriorTransitions();
        graph.setCapacities(pcaps);
    }


}
