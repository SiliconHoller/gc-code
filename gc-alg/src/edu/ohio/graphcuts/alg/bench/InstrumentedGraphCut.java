/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bench;

import edu.ohio.graphcuts.alg.GraphCut;
import java.util.Map;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public interface InstrumentedGraphCut extends GraphCut {

    public Map<String,Long> getBenchmarks();
    
}
