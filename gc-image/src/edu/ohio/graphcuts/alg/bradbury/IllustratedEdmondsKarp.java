/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bradbury;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.EdmondsKarp;
import java.awt.Color;
import java.awt.image.BufferedImage;



/**
 * Implementation of the Edmonds-Karp min-cut algorithm.
 */
public class IllustratedEdmondsKarp extends EdmondsKarp implements IllustratedGraphCut {


    /**
     *
     */
    protected Illustrator ill;
    /**
     *
     */
    protected ImageListener imglistener;
    /**
     *
     */
    protected int pathcount;
    /**
     *
     */
    protected Color pcolor;
    protected Color scolor;
    protected Color tcolor;
    /**
     *
     */
    protected int w;
    /**
     *
     */
    protected int h;

    /**
     *
     * @param graph
     * @param basedir
     */
    public IllustratedEdmondsKarp(ImageGraph graph) {
        super(graph);
        ill = new Illustrator();
        imglistener = ill;
    }

    /**
     * 
     */
    protected void initIllustration() {
        pcolor = Color.green;
        scolor = Color.red;
        tcolor = Color.blue;

        w = graph.getWidth();
        h = graph.getHeight();
 
        pathcount = 0;
    }

    /**
     *
     * @return
     */
    @Override
    public double maxFlow() {
        initIllustration();
        double max = 0.0;
        parents = new int[vertices.length];
        int[] path = bfs(src,sink);
        while (path.length > 0) {
            showProgress();
            showProgress(path);
            double minavail = graph.minAvailable(path);
            max += minavail;
            graph.augmentPath(minavail,path);
            graph.removeFullEdges(path);
            path = bfs(src,sink);
            
            pathcount++;
        }

        showProgress();
        return max;
    }

    protected void showProgress() {

        if (imglistener != null) {
            BufferedImage stimg = ill.getSTLinkImage(graph, src, scolor);
            BufferedImage ttimg = ill.getTTLinkImage(graph, sink, tcolor);
            imglistener.tlinkImage(stimg, src, pathcount);
            imglistener.tlinkImage(ttimg, sink, pathcount);
        }
    }
    /**
     *
     * @param path
     */
    protected void showProgress(int[] path) {
        if (imglistener != null) {
            BufferedImage pimg = ill.getPathImage(graph, path, pcolor);
            imglistener.pathImage(pimg, pathcount);
        }
    }


    /**
     *
     * @return
     */
    public Illustrator getIllustrator() {
        return ill;
    }

    /**
     *
     * @param ill
     */
    public void setIllustrator(Illustrator ill) {
        this.ill = ill;
    }

    /**
     *
     * @param imgl
     */
    public void setImageListener(ImageListener imgl) {
        this.imglistener = imgl;
    }


}
