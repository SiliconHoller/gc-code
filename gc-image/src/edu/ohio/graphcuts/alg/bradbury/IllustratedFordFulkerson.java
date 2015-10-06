/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bradbury;


import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.FordFulkerson;
import java.awt.Color;
import java.awt.image.BufferedImage;



/**
 * Implementation of the Ford-Fulkerson min-cut algorithm.
 */
public class IllustratedFordFulkerson extends FordFulkerson implements IllustratedGraphCut {


    protected Illustrator ill;
    protected ImageListener imglistener;
    protected int pathcount;
    protected Color pcolor;
    protected Color scolor;
    protected Color tcolor;
    protected int w;
    protected int h;

    public IllustratedFordFulkerson(ImageGraph graph) {
        super(graph);
        ill = new Illustrator();
        imglistener = ill;
    }

    protected void initIllustration() {
        pcolor = Color.green;
        scolor = Color.red;
        tcolor = Color.blue;

        w = graph.getWidth();
        h = graph.getHeight();

        pathcount = 0;
    }

    @Override
    public double maxFlow() {
        initIllustration();
        double max = 0.0;
        parents = new int[vertices.length];
        int[] path = dfs(src,sink);
        while (path.length > 0) {
            showProgress();
            showProgress(path);
            double minavail = graph.minAvailable(path);
            max += minavail;
            graph.augmentPath(minavail,path);
            graph.removeFullEdges(path);
            path = dfs(src,sink);
            
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

    protected void showProgress(int[] path) {
        if (imglistener != null) {
            BufferedImage pimg = ill.getPathImage(graph, path, pcolor);
            imglistener.pathImage(pimg, pathcount);
        }
    }



    public Illustrator getIllustrator() {
        return ill;
    }

    public void setIllustrator(Illustrator ill) {
        this.ill = ill;
    }

    public void setImageListener(ImageListener imgl) {
        this.imglistener = imgl;
    }


}
