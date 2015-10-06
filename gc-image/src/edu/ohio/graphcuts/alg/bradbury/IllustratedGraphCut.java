/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bradbury;

import edu.ohio.graphcuts.alg.GraphCut;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public interface IllustratedGraphCut extends GraphCut {

    public Illustrator getIllustrator();
    public void setIllustrator(Illustrator ill);
    public void setImageListener(ImageListener imgl);
    
}
