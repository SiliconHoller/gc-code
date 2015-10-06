/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.event;

import edu.ohio.graphcuts.analysis.features.Instance;
import gcgui.panel.LayeredImagePanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class ImageSampler implements MouseListener, MouseMotionListener {

    protected LayeredImagePanel ipanel;
    protected Vector<SampleListener> listeners;
    protected boolean sampling;
    protected Vector<Instance> samples;

    public ImageSampler(LayeredImagePanel ipanel) {
        this.ipanel = ipanel;
    }
    
    public void setSampling(boolean sampling) {
        this.sampling = sampling;
        if (isSampling()) {
            samples = new Vector<Instance>();
        }
    }

    public boolean isSampling() {
        return sampling;
    }

    public void addSampleListener(SampleListener sl) {
        if (listeners == null) {
            listeners = new Vector<SampleListener>();
        }
        listeners.add(sl);
    }

    public void fireSample(Instance inst) {
        if (listeners != null) {
            for (SampleListener sl:listeners) {
                sl.sample(inst);
            }
        }
    }

    public void fireSamples() {
        if (listeners != null) {
            for (SampleListener sl: listeners) {
                sl.sample(samples);
            }
        }
        samples = new Vector<Instance>();
    }

    protected Instance getInstanceFrom(MouseEvent e) {
        double ix = ipanel.getImageX(e.getX());
        double iy = ipanel.getImageY(e.getY());
        return new Instance.GrayPixel(ix, iy, 0.0);
    }

    public void mouseDragged(MouseEvent e) {
        if (sampling) {
            samples.add(getInstanceFrom(e));
        }
    }

    public void mouseMoved(MouseEvent e) {
        //empty
    }

    public void mouseClicked(MouseEvent e) {
        if (sampling) {
            fireSample(getInstanceFrom(e));
        }
    }

    public void mousePressed(MouseEvent e) {
        //empty
    }

    public void mouseReleased(MouseEvent e) {
        if (sampling) {
            fireSamples();
        }
    }

    public void mouseEntered(MouseEvent e) {
        //empty
    }

    public void mouseExited(MouseEvent e) {
        //empty
    }

}
