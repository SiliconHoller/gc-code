/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.ImageGraph;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author david
 */
public class Gradient {

    private ImageGraph graph;
    private double[][] gradients;
    private boolean calculated = false;
    private LinkedList<Integer>[] edges;
    private int[] vertices;
    private int src;
    private int sink;

    public Gradient(ImageGraph graph) {
        this.graph = graph;
    }

    public double[][] getGradients() {
        if (!calculated) {
            calcGradients();
        }
        return gradients;
    }

    protected void calcGradients() {
        gradients = graph.getNewFlows();
        edges = graph.getEdges();
        vertices = graph.getVertices();
        sink = graph.getSink();
        src = graph.getSrc();
        int j = 0;
        for (int i=0;i<vertices.length;i++) {
            if (i != src && i != sink) {
                LinkedList<Integer> oe = edges[i];
                if (oe != null) {
                    Iterator<Integer> it = oe.iterator();
                    while (it.hasNext()) {
                        j = it.next();
                        if (j != sink) {
                            gradients[i][j] = calcGradient(i,j);
                        }
                    }
                }
            }
        }
        calculated = true;
    }

    protected double calcGradient(int u, int v) {
        double grad = 0.0;
        int ux = graph.getX(u);
        int uy = graph.getY(u);
        int vx = graph.getX(v);
        int vy = graph.getY(v);
        double diffI = vertices[u]-vertices[v];
        double diffx = ux - vx;
        double diffy = uy - vy;
        grad = (diffI)/(Math.sqrt(diffx*diffx+diffy*diffy));
        return grad;
    }

    public Edge getHighestGradient() {
        if (!calculated) {
            calcGradients();
        }
        Edge e = null;
        int umax = 0;
        int vmax = 0;
        double gmax = 0.0;
        for (int u=0;u<gradients.length;u++) {
            double[] oe = gradients[u];
            for (int v = 0;v<oe.length;v++) {
                if (Math.abs(gradients[u][v]) > gmax) {
                    umax = u;
                    vmax = v;
                    gmax = gradients[u][v];
                }
            }
        }
        e = new Edge(umax,vmax,gmax);
        return e;
    }
}
