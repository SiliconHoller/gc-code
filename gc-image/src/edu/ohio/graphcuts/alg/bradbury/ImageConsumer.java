/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bradbury;

import java.awt.image.BufferedImage;

/**
 * <p>Interface representing an instance that is interested in
 * the specified image.</p>
 * <p>Implementing classes should return as quickly as practicable
 * in order to limit slowing the algorithm supplying the information.  For
 * instance, a file system or an analysis ImageConsumer could, conceivably, start a thread
 * to perform storage or processing while returning immediately to the calling
 * instance.</p>
 * @author David Days <david.c.days@gmail.com>
 */
public interface ImageConsumer {

    /**
     * Method to pass a BufferedImage representing the path under consideration,
     * as well as a pre-compiled String with additional information.
     * @param title String containing pre-processed information to be passed.
     * @param img BufferedImage instance representing the path under consideration.
     */
    public void pathImage(String title, BufferedImage img);
    /**
     * Method to pass a BufferedImage representing the current labeling in the GraphCut
     * algorithm.
     * @param title  String containing pre-processed information to be passed.
     * @param img BufferedImage instance representing the current labeling.
     */
    public void labelImage(String title, BufferedImage img);

    /**
     * Method to pass a BufferedImage representing the current state of t-links
     * to or from the indicated label node.
     * @param title String containing pre-processed information to be passed.
     * @param img BufferedImage instance representing the current state of t-links.
     * @param label Label/node of interest.
     */
    public void tlinkImage(String title, BufferedImage img, int label);
    /**
     * Method to pass a BufferedImage representing the current cut set of the image.
     * @param title String containing pre-processed information to be passed along.
     * @param img BufferedImage object.
     */
    public void cutImage(String title, BufferedImage img);
    /**
     * Method to pass both the BufferedImage and the iteration count directly.
     * @param iteration Sequence in the iteration of the algorithm.
     * @param img BufferedImage object.
     */
    public void pathImage(int iteration, BufferedImage img);
    /**
     * Method to pass along the BufferedImage representing the current labeling.
     * @param iteration Sequence in the iteration of the algorithm.
     * @param img Image to be transferred.
     */
    public void labelImage(int iteration, BufferedImage img);
    /**
     * Method to pass along the iteration, label, and BufferedImage object representing the
     * current t-links in the algorithm.
     * @param iteration Sequence in the iteration.
     * @param img Image to be transferred.
     * @param label Label to/from which the t-links connect.
     */
    public void tlinkImage(int iteration, BufferedImage img, int label);
    /**
     * Method to pass along the iteration and representation of the current cut set.
     * @param iteration Sequence in the iteration.
     * @param img Image to be transferred.
     */
    public void cutImage(int iteration, BufferedImage img);
}
