/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.event;

import edu.ohio.graphcuts.analysis.features.Instance;
import java.util.Collection;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public interface SampleListener {

    public void sample(Instance inst);

    public void sample(Collection<Instance> c);

}
