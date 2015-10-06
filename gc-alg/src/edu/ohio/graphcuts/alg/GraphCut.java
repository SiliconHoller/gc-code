/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg;

import edu.ohio.graphcuts.Edge;
import java.util.Collection;

/**
 *
 * @author david
 */
public interface GraphCut {

    public double maxFlow();
    public boolean inSource(int v);
    public boolean inSink(int v);
    public boolean inLabel(int label, int v);
    public Collection<Edge> getCuts();

}
