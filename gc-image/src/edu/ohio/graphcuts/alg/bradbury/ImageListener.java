/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bradbury;

import java.awt.image.BufferedImage;

/**
 * <p>Interface for the object that is interested in the production
 * of a new image of the specified type.</p>
 * <p>Specifically, implementing classes can and should act as a central dispatch
 * for the associated ImageConsumers.  For instance, an image is passed to the
 * ImageListener, which then passes it to a file system ImageConsumer instance (for storage),
 * a GUI ImageConsumer (for display), and an analysis ImageConsumer (for further processing).</p>
 * @author David Days <david.c.days@gmail.com>
 */
public interface ImageListener {

    /**
     * Adds the given ImageConsumer to the list of object wishing to
     * receive the appropriate image(s).
     * @param ic
     */
    public void addImageConsumer(ImageConsumer ic);
    /**
     * Removes the given ImageConsumer from those object wishing to receive
     * images.
     * @param ic
     */
    public void removeImageConsumer(ImageConsumer ic);
    /**
     * Passes to the ImageListener the BufferedImage representing the
     * path under consideration.
     * @param img  BufferedImage displaying the path.
     * @param iteration The iteration in the process (data to be passed on).
     */
    public void pathImage(BufferedImage img, int iteration);
    /**
     * Passes to the ImageListener the BufferedImage representing the current
     * labeling of the image pixels.
     * @param img BufferedImage representing the current labeling.
     * @param iteration Data to be pass on to the ImageConsumer.
     */
    public void labelImage(BufferedImage img, int iteration);
    /**
     * Passes to the ImageListener the BufferedImage representing the current available
     * t-links to or from the given label.
     * @param img BufferedImage representing the current state of t-links to or from the
     * indicated label.
     * @param label Label of interest.
     * @param iteration Data to be passed on to the ImageConsumer.
     */
    public void tlinkImage(BufferedImage img, int label, int iteration);
    /**
     * Passes to the ImageListener the BufferedImage representing the current state of
     * cuts in the ImageGraph object.
     * @param img  BufferedImage representing the current cuts in the ImageGraph.
     * @param iteration Data to be passed on to the ImageConsumer.
     */
    public void cutImage(BufferedImage img, int iteration);

}
