/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.gui.io;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import edu.ohio.graphcuts.data.PngOps;
import edu.ohio.graphcuts.gui.tree.CutImage;
import java.awt.image.BufferedImage;

/**
 *
 * @author david
 */
public class State {

    private BufferedImage currImage;
    private CutImage cimg;
    private String currDir;
    protected static State instance;
    private ImageGraph graph;
    private GraphCut gc;
    private PngOps pngops;

    public State() {
        instance = this;
    }

    public static State getInstance() {
        if (instance == null) {
            instance = new State();
        }
        return instance;
    }

    public void setCurrentImage(BufferedImage currImage) {
        this.currImage = currImage;
    }

    public BufferedImage getCurrentImage() {
        return currImage;
    }

    public void setCutImage(CutImage cimg) {
        this.cimg = cimg;
    }

    public CutImage getCutImage() {
        return cimg;
    }

    public void setCurrentDirectory(String currDir) {
        this.currDir = currDir;
    }

    public String getCurrentDirectory() {
        return currDir;
    }

    public void setGraph(ImageGraph graph) {
        this.graph = graph;
    }

    public ImageGraph getGraph() {
        return graph;
    }

    public void setGraphCut(GraphCut gc) {
        this.gc = gc;
    }

    public GraphCut getGraphCut() {
        return gc;
    }

    public void setPngOps(PngOps pngops) {
        this.pngops = pngops;
    }

    public PngOps getPngOps() {
        if (pngops == null) pngops = new PngOps();
        return pngops;
    }

}
