/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bradbury;

import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 * Class and methods to produce in-process images from
 * the given Graph object and GraphCut instance.
 * @author David Days <david.c.days@gmail.com>
 */
public class Illustrator implements ImageListener {

    protected AffineTransform trans = null;
    protected Vector<ImageConsumer> ilists;

    public Illustrator() {
        //Empty constructor for now
        ilists = new Vector<ImageConsumer>();
    }

    /**
     * Creates an image of the current state of t-links between the given label
     * and the nodes/pixels represented by the ImageGraph instance.  If the t-link
     * has flow available, the pixel is set to the indicated color.  Otherwise, it is
     * left blank.
     *
     * @param graph  ImageGraph instance holding the state of the t-links.
     * @param label Label (typically the src) of the graph from which we wish to display available t-links.
     * @param color Color to be applied to pixels with t-links from the label to that pixel.
     * @return A BufferedImage representing the state of available t-links from the label to the
     * image.
     */
    public BufferedImage getSTLinkImage(ImageGraph graph, int label, Color color) {
        int w = graph.getWidth();
        int h = graph.getHeight();

        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        int pixColor = color.getRGB();
        LinkedList<Integer> pix = graph.getEdgesFrom(label);
        for (int pt : pix) {
            if (graph.getAvailable(label,pt) > 0) {
                img.setRGB(graph.getX(pt), graph.getY(pt), pixColor);
            }
        }
        return img;
    }

    /**
     * Inverse of getSTLinkImage() method.   Creates an image showing available t-links
     * <em>from</em> the image pixel <em>to</em> the given label (typically sink).
     * @param graph ImageGraph instance containing current state of t-links of interest.
     * @param label Label whose incoming t-links are of interest.
     * @param color The Color to be applied to pixels with available t-links.
     * @return A BufferedImage representing the current state of t-links from the pixels
     * to the given node.
     */
    public BufferedImage getTTLinkImage(ImageGraph graph, int label, Color color) {
        int w = graph.getWidth();
        int h = graph.getHeight();

        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        int pixColor = color.getRGB();
        LinkedList<Integer> pix = graph.getEdgesTo(label);
        for (int pt : pix) {

            if (graph.getAvailable(pt,label) > 0) {
                img.setRGB(graph.getX(pt), graph.getY(pt), pixColor);
            }
        }
        return img;
    }

    /**
     * Method to display a trace of the in-image path of interest.
     * @param graph Graph holding the current state of the process.
     * @param path Ordered list of nodes representing the path of interest.
     * @param color Color to be applied to the path nodes given.
     * @return A BufferedImage tracing the path on the image.
     */
    public BufferedImage getPathImage(ImageGraph graph, int[] path, Color color) {
        int w = graph.getWidth();
        int h = graph.getHeight();
        int src = graph.getSrc();
        int sink = graph.getSink();

        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        int pixColor = color.getRGB();

        for (int pos : path) {
            if (pos != src && pos != sink) {
                img.setRGB(graph.getX(pos), graph.getY(pos), pixColor);
            }
        }

        return img;
    }

    public BufferedImage getLabeledImage(ImageGraph graph, GraphCut gc, Color scolor, Color tcolor) {
        int w = graph.getWidth();
        int h = graph.getHeight();

        int s = graph.getSrc();
        int t = graph.getSink();


        int spix = scolor.getRGB();
        int tpix = tcolor.getRGB();

        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);

        
        LinkedList<Integer>[] edges = graph.getEdges();

        int pcolor;
        int pos;
        for (int x=0;x<w;x++) {
            for (int y=0;y<w;y++) {
                pos = w*y+x;
                if (edges[pos] != null) {
                    if (gc.inSource(pos)) {
                        pcolor = spix;
                    } else {
                        pcolor = tpix;
                    }
                    img.setRGB(x, y, pcolor);
                }
            }
        }

        return img;
    }

    public BufferedImage getCutImage(ImageGraph graph, GraphCut gc, Color ccolor) {
        Collection<Edge> cuts = gc.getCuts();

        return getCutImage(cuts, graph, ccolor);

    }

    public void addImageConsumer(ImageConsumer ic) {
        ilists.add(ic);
    }

    public void removeImageConsumer(ImageConsumer ic) {
        ilists.remove(ic);
    }

    public void pathImage(BufferedImage image, int iteration) {
        for (ImageConsumer ic : ilists) {
            ic.pathImage("p"+makeIterationString(iteration), image);
        }
    }

    public void labelImage(BufferedImage img, int iteration) {
        for (ImageConsumer ic : ilists) {
            ic.labelImage(makeIterationString(iteration), img);
        }
    }

    public void tlinkImage(BufferedImage img, int label, int iteration) {
        for (ImageConsumer ic :ilists) {
            ic.tlinkImage(makeIterationString(iteration), img, label);
        }
    }

    protected String makeIterationString(int count) {
        String num = Integer.toString(count);
        //Create leading-edge zeroes
        int lz = 8-num.length();
        if (lz > 0) {
            for (int i=0;i<lz;i++) {
                num = "0"+num;
            }
        }
        return num;
    }

    public void cutImage(BufferedImage img, int iteration) {
        for (ImageConsumer ic : ilists) {
            ic.cutImage(makeIterationString(iteration), img);
        }
    }

    BufferedImage getCutImage(Collection<Edge> cuts, ImageGraph graph, Color ccolor) {
        int w = graph.getWidth();
        int h = graph.getHeight();
        int src = graph.getSrc();
        int sink = graph.getSink();

        int pixcolor = ccolor.getRGB();

        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        for (Edge e : cuts) {
            //Draw u, if not src or sink
            if (e.u != src && e.u != sink) {
                img.setRGB(graph.getX(e.u), graph.getY(e.u), pixcolor);
            }
            if (e.v != src && e.v != sink) {
                img.setRGB(graph.getX(e.v), graph.getY(e.v), pixcolor);
            }

        }

        return img;

    }
}
