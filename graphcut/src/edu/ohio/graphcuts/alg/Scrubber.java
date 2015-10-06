/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg;

import edu.ohio.graphcuts.ImageGraph;

/**
 * <p>Applies a pre-processing step to the given graph.</p>
 * <p>For all paths of the form s->node->t:
 * <ul>
 * <li>fmax = min(available(s->node),available(node->))</li>
 * <li>graph.addFlow(fmax,s,node)</li>
 * <li>graph.addFlow(fmax,node,t)</li>
 * </ul>
 * </p>
 * @author david
 */
public class Scrubber {

    protected ImageGraph graph;

    public Scrubber(ImageGraph graph) {
        this.graph = graph;
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

    public double scrubGraph() {
        double flow = 0.0;
        int s = graph.getSrc();
        int t = graph.getSink();
        double sn = 0.0; //s->node available
        double nt = 0.0; //node->t available
        double min = 0.0; //min available
        int[] verts = graph.getVertices();
        for (int i=0;i<verts.length;i++) {
            
            sn = graph.getAvailable(s,i);
            nt = graph.getAvailable(i,t);
            min = min(sn,nt);
            flow += min;
            //if (min > 0.0) {
            //    graph.addFlow(flow, s,i);
            //    graph.addFlow(flow,i,t);
            //}
            if (sn > nt) {
                //remove node-> t
                graph.removeEdge(i, t);
            } else {
                //remove s->node
                graph.removeEdge(s,i);
            }
            
        }
        return flow;
    }
}
