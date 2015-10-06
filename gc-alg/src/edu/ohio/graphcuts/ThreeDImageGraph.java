/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts;

import java.lang.reflect.Array;
import java.util.LinkedList;

/**
 * <p>Graph<V,E> structure for image data.  The purpose of this class is to
 * provide a wrapper and utility methods for the graph structure.  Original data
 * is held in as memory-efficient a manner as possible.  For instance, the edge
 * and flow matrices are held as an array-style adjacency list.</p>
 * <p>Additionally, the flow-manipulation operations (finding flow, augmenting flow,
 * calculating residual flow, etc.) are provided as public methods.</p>
 * @author David Days
 */
public class ThreeDImageGraph extends ImageGraph {

    /**
     * The 3-dimensional height of the image represented by this graph
     */
    protected int zth;

    /**
     * Constructor for an ImageGraph instance with pre-configured vertices, edges,
     * source and sink.
     * @param vertices Vertices of the image graph.
     * @param edges Adjacency-list edge array.
     * @param src The vertex designated as the source of the flow.
     * @param sink The vertex designated as the sink.
     * @param w
     * @param h
     */
    public ThreeDImageGraph(int[] vertices, LinkedList<Integer>[] edges, int src, int sink, int w, int h, int zth) {
        super(vertices, edges, w, h, src, sink);
        this.zth = zth;
        init();
    }


    private void init() {
        index = getEdgeArray();
        capacities = getNewFlows();
        flows = getNewFlows();
    }


    /**
     * The 3-dimensional height in z of this image.
     * @return The height of Z of the image.
     */
    public int getZHeight() {
        return zth;
    }


    /**
     * <p>Returns the in-image y-coordinate of the given node.</p>
     * <p>NOTE:  If the given node is outside the image coordinate space (such as a
     * label node), an invalid y-coordinate will be returned.  At this time, it is
     * incumbent upon the developer to check the x-y-z coordinates returned from the
     * getX(), getY(), and getZ() methods.  Otherwise, attempts to manipulate an image of
     * the same dimensions will crash.</p>
     * @param pos
     * @return The in-image y-coordinate of the given node.
     */
    @Override
    public int getY(int pos) {
        return (pos/w)%h;
    }

    /**
     * <p>Returns the in-image z-coordinate of the given node.</p>
     * <p>NOTE:  If the given node is outside the image coordinate space (such as a
     * label node), an invalid z-coordinate will be returned.  At this time, it is
     * incumbent upon the developer to check the x-y-z coordinates returned from the
     * getX(), getY(), and getZ() methods.  Otherwise, attempts to manipulate an image of
     * the same dimensions will crash.</p>
     * @param pos
     * @return The in-image z-coordinate of the given node.
     */
    public int getZ(int pos) {
        return (pos/w)/h;
    }

    /**
     * <p>Returns the node positional value of the given x-y coordinates.</p>
     * <p>NOTE:  At this time, no checking is done upon the value returned.  If invalid
     * coordinates are passed to this parameter, an invalid or unintended position
     * is returns.  It is incumbent upon the developer to ensure that the x-y values
     * passed are within the original image used to construct this graph structure.</p>
     *
     * @param x x-coordinate of interest.
     * @param y y-coordinate of interest.
     * @param z z-coordinate of interest.
     * @return The node position (value) of the resulting point in the image.
     */
    public int getPosition(int x, int y, int z) {
        return (w*h*z+w*y+x);
    }

}
