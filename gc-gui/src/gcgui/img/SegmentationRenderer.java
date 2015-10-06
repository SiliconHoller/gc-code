/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.img;

import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.NLabelImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class SegmentationRenderer {

    public SegmentationRenderer() {
        //Empty constructor for convenience
    }

    /**
     * Creates a new image with the labeled section colored appropriately.
     * @param graph Graph representation of the image on which segmentation was performed.
     * @param gc GraphCut implementation that performed the segmentation.
     * @param label Label to be highlighted.
     * @param color Color to be used.
     * @return A new BufferedImage with the labeled pixels colored appropriately.
     */
    public BufferedImage getColoredSegment(NLabelImageGraph graph, GraphCut gc, int label, Color color) {
        int w = graph.getWidth();
        int h = graph.getHeight();
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        int pixel = color.getRGB();
        int pos;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                pos = graph.getPosition(i, j);
                if (gc.inLabel(label, pos)) img.setRGB(i, j, pixel);
            }
        }
        return img;
    }

    /**
     * Returns a fully-colored image, using the supplied colors.
     * @param graph Graph representation of the image.
     * @param gc GraphCut implementation used to perform the segmentation.
     * @param colors Colors to be used to fill in the image.
     * @return A new BufferedImage object with the appropriate coloring applied.
     */
    public BufferedImage getColoredImage(NLabelImageGraph graph, GraphCut gc, Color[] colors) {
        int w = graph.getWidth();
        int h = graph.getHeight();
        int[] labels = graph.getLabels();
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        int pixel;
        int pos;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                pos = graph.getPosition(i,j);
                pixel = colorOf(gc,pos,labels,colors);
                img.setRGB(i,j,pixel);
            }
        }
        return img;
    }

    /**
     * <p>Convenience method to mass-produce the appropriate images.</p>
     * <p>This method makes repeated appropriate calls to the getSegmentedImage() method.</p>
     * @param graph Graph representation of the original image.
     * @param gc GraphCut implementation on which the segmentation was performed.
     * @param oimg Original image.
     * @return An array of length equal to the number of labels, containing the segmented images.
     */
    public BufferedImage[] getSegmentedImages(NLabelImageGraph graph, GraphCut gc, BufferedImage oimg) {
        int[] labels = graph.getLabels();
        BufferedImage[] imgs = new BufferedImage[labels.length];
        for (int i=0;i<labels.length;i++) {
            imgs[i] = getSegmentedImage(graph, gc, oimg, labels[i]);
        }
        return imgs;
    }

    /**
     * Given the graph, GraphCut implementation, and original image, this method returns a new image
     * with only visible pixels in the desired label.
     * @param graph Processed graph representation of the original image.
     * @param gc GraphCut implementation.
     * @param oimg Original image on which segmentation was performed.
     * @param label Desired label.
     * @return A new image displaying only parts of the original assigned to the desired label.
     */
    public BufferedImage getSegmentedImage(NLabelImageGraph graph, GraphCut gc, BufferedImage oimg, int label) {
        int w = graph.getWidth();
        int h = graph.getHeight();
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        int pos;
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                pos = graph.getPosition(i,j);
                if (gc.inLabel(label, pos)) img.setRGB(i,j,oimg.getRGB(i, j));
            }
        }
        return img;
    }

    /**
     * <p>Outlines the segmentation edges in the appropriate color scheme.</p>
     * <p>This method calls the getCuts() method of GraphCut implementations.  It is
     * therefore necessary that the implementation make a proper return.</p>
     * @param img  Image to be annotated.
     * @param gc GraphCut implementation to find the segmentation.
     * @param graph Graph representation.
     * @param labels Array of label vertices.
     * @param colors Array of color associations.
     * @return An annotated copy of the image.
     */
    public BufferedImage outline(BufferedImage img, GraphCut gc, NLabelImageGraph graph, int[] labels, Color[] colors) {
        //Create a new image copy of the old one
        BufferedImage oimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = oimg.createGraphics();
        g.drawImage(img, null, 0, 0);

        Collection<Edge> cuts = gc.getCuts();
        Iterator<Edge> it = cuts.iterator();
        Edge e;
        while (it.hasNext()) {
            e = it.next();
            if (!graph.isLabel(e.u) && !graph.isLabel(e.v)) {
                oimg.setRGB(graph.getX(e.u), graph.getY(e.u), colorOf(gc,e.u,labels,colors));
                oimg.setRGB(graph.getX(e.v),graph.getY(e.v),colorOf(gc,e.v,labels,colors));
        
            }
        }
        return oimg;
    }

    /**
     * Convenience method to create individual outline images of each label.  This method
     * makes repeated calls to getOutline().
     * @param graph Graph representation of the image.
     * @param gc GraphCut implementation that performed the segmentation.
     * @param colors Array of colors to be used in the outlining process.
     * @return Array of images annotated with the requested outlines.
     */
    public BufferedImage[] getOutlines(NLabelImageGraph graph, GraphCut gc, Color[] colors) {
        int[] labels = graph.getLabels();
        BufferedImage[] imgs = new BufferedImage[labels.length];
        for (int i=0;i<imgs.length;i++) {
            imgs[i] = getOutline(labels[i],gc,graph,colors[i]);
        }
        return imgs;
    }

    /**
     * Returns a new BufferedImage with the edges of the given label delineated in the
     * assigned color.
     * @param label Label set to be outlined.
     * @param gc GraphCut implementation which performed segmentation.
     * @param graph Graph representation of the image.
     * @param color Color in which to annotate outline.
     * @return New annotated BufferedImage.
     */
    public BufferedImage getOutline(int label, GraphCut gc, NLabelImageGraph graph, Color color) {
        int w = graph.getWidth();
        int h = graph.getHeight();
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        int pixel = color.getRGB();
        Collection<Edge> cuts = gc.getCuts();
        Iterator<Edge> it = cuts.iterator();
        Edge e;
        while (it.hasNext()) {
            e = it.next();
            if (!graph.isLabel(e.u) && gc.inLabel(label, e.u)) {
                img.setRGB(graph.getX(e.u), graph.getY(e.u), pixel);
            } else if (!graph.isLabel(e.v) && gc.inLabel(label,e.v)) {
                img.setRGB(graph.getX(e.v), graph.getY(e.v), pixel);
            }
        }
        return img;
    }
    
    /**
     * <p>Given a GraphCut process and a vertex in the graph defined by pos, this
     * method returns the appropriate ARGB pixel color from the Color[] array of possibilities.</p>
     * <p>If no appropriate color is found, the returned value is 0, indicating a
     * transparent black pixel.</p>
     * @param gc GraphCut algorithm used to determine label association
     * @param pos Vertex identifier.
     * @param labels Array of possible labels.
     * @param colors Array of possible colors.
     * @return The appropriate color of the given vertex, or 0 if none is found.
     */
    public int colorOf(GraphCut gc, int pos, int[] labels, Color[] colors) {
        int pixel = 0;
        boolean found = false;
        int i=0;
        while (!found && i<labels.length) {
            if (gc.inLabel(labels[i], pos)) {
                pixel = colors[i].getRGB();
                found = true;
            }
            i++;
        }
        return pixel;
    }

}
