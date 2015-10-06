/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bradbury;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.BKTreeCut;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 */
public class IllustratedBKTreeCut extends BKTreeCut implements IllustratedGraphCut {


    protected Illustrator ill;
    protected ImageListener imglistener = null;
    protected Color scolor;
    protected Color tcolor;
    protected Color pcolor;
    protected Color ccolor;

    public IllustratedBKTreeCut(ImageGraph graph) {
        super(graph);
        ill = new Illustrator();
        imglistener = ill;
    }

    @Override
    protected void init() {
        super.init();


        scolor = new Color(255,0,0);
        tcolor = new Color(0,0,255);
        pcolor = new Color(0,255,0);
        ccolor = new Color(255,255,0);
    }

    @Override
    public double maxFlow() {
        double maxFlow = 0.0;
        int count = 0;
        init();
        active.offer(src);
        active.offer(sink);
        int[] path = new int[1];
        while (path.length != 0) {
            try {
                //System.out.println("\tactive size = "+active.size());
                //System.out.println("\tgrowing...");
                path = grow();
                //printPath("path from grow:  ",path);
                if (path.length == 0) {
                    return maxFlow;
                }
                maxFlow += augment(path);
                //System.out.println("\tadopting...");
                adopt();
                showProgress(count, path);
                count++;
            } catch (IOException ex) {
                Logger.getLogger(IllustratedBKTreeCut.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            showProgress(count, path);
        } catch (IOException ex) {
            Logger.getLogger(IllustratedBKTreeCut.class.getName()).log(Level.SEVERE, null, ex);
        }
        return maxFlow;
    }

    protected void showProgress(int count, int[] path) throws IOException {
        if (imglistener != null) {
            //Get the individual progress images
            //Image showing existing source t-links
            BufferedImage stimg = ill.getSTLinkImage(graph, graph.getSrc(), scolor);

            //Image showing existing sink t-links
            BufferedImage ttimg = ill.getTTLinkImage(graph, graph.getSink(), tcolor);


            //Image showing current in-image labels
            BufferedImage limg =  ill.getLabeledImage(graph, this, scolor, tcolor);

            //Image showing path of interest this iteration
            BufferedImage pimg = ill.getPathImage(graph, path, pcolor);

            BufferedImage cimg = ill.getCutImage(graph, this, ccolor);
            //Now pass these images on to the object listening for them
            imglistener.labelImage(limg, count);
            imglistener.tlinkImage(stimg, graph.getSrc(), count);
            imglistener.tlinkImage(ttimg, graph.getSink(), count);
            imglistener.pathImage(pimg, count);
            imglistener.cutImage(cimg, count);

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
