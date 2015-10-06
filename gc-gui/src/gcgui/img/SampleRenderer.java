/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.img;

import edu.ohio.graphcuts.analysis.features.Instance;
import gcgui.data.Sample;
import gcgui.data.SampleSet;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class SampleRenderer {

    protected Color[] colors;
    protected SampleSet sset;

    public SampleRenderer() {
        //empty constructor for convenience
    }

    protected void setColors() {
        //System.out.println("setColors():");
        Vector<Sample> samples = sset.getSet();
        int num = samples.size();
        colors = new Color[num];
        float h;
        for (int i=0;i<num;i++) {
            h = ((float)i)/(float)num;
            colors[i] = Color.getHSBColor(h,1.0f,1.0f);
        }
    }

    public Color[] getColors() {
        return colors;
    }

    public void sampleSet(SampleSet sset) {
        this.sset = sset;
    }

    public BufferedImage render(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage rimg = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        setColors();
       
        Vector<Sample> samples = sset.getSet();
        Iterator<Sample> it = samples.iterator();
        int spos;
        while (it.hasNext()) {
            Sample s = it.next();
            spos = samples.indexOf(s);
            Collection<Instance> c = s.getRawSample();
            for (Instance i: c) {
                rimg.setRGB((int)i.getVal(0), (int)i.getVal(1), colors[spos].getRGB());
            }
        }
        return rimg;
    }

}
