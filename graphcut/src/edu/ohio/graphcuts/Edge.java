/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts;

/**
 *
 * @author david
 */
public class Edge implements Comparable {

    public int u = 0;
    public int v = 0;
    public double wt = 0.0;

    public Edge(int u, int v, double wt) {
        this.u = u;
        this.v = v;
        this.wt = wt;
    }

    public Edge(int u, int v) {
        this.u = u;
        this.v = v;
    }

    public int compareTo(Object o) {
        if (!(o instanceof Edge)) {
            throw new RuntimeException("Cannot compare Edge to "+o.toString());
        }
        Edge oe = (Edge) o;
        int rval = 0;
        if (wt < oe.wt) rval = -1;
        if (wt > oe.wt) rval = 1;
        return rval;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Edge)) return false;
        Edge oe = (Edge) o;
        return (this.u == oe.u && this.v == oe.v);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.u;
        hash = 97 * hash + this.v;
        return hash;
    }





}
