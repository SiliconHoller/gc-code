/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis.features;

import java.awt.image.BufferedImage;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public abstract class ImageDataSet {

    protected BufferedImage img;
    protected int w;
    protected int h;
    protected InstanceMap imap;
    protected DataSet dset;

    protected ImageDataSet(BufferedImage img) {
        this.img = img;
        this.w = img.getWidth();
        this.h = img.getHeight();
        imap = new InstanceMap.TwoD(w, h);
    }

    protected abstract void extract();

    public InstanceMap getInstanceMap() {
        return imap;
    }

    public DataSet getDataSet() {
        return dset;
    }
    
}
