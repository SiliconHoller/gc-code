/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bradbury;

import edu.ohio.graphcuts.Edge;
import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.UFBorderCut;
import edu.ohio.graphcuts.alg.UnionFind;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *<p>Implementation of the GraphCut interface that performs a
 * graph cut on the given ImageGraph via BorderCut algorithm.</p>
 * <p>The algorithm conducts the cut in the following manner:
 * <ul>
 * <li>Similar to the BK algorithm, "trees" from the source and
 * sink nodes are created.  In the Border Cut algorithm, however,
 * the tree-building process continues until all available nodes in the
 * image are claimed.</li>
 * <li>Cut edges are found.  A cut edge is one in which the node on one end
 * of the edge belongs to a different tree than the node on the other end.</li>
 * <li>For each cut edge, the combined path from the source to t sink is found (there
 * are only two possible trees, therefore the edge representing a border crossing
 * is a link in the path from the source to the sink.</li>
 * <li>The path is augmented in the typical manner, pushing the available flow.</li>
 * <li>The trees are then recreated from the first step.</li>
 * <li>This process is repeated until no flow across the cuts edges occurs.</li>
 * </ul>
 * </p>
 * <p>This implementation uses a UnionFind instance to track labeling-trees.</p>
 * @author david
 */
public class IllustratedUFBorderCut extends UFBorderCut implements IllustratedGraphCut {

  
    protected Illustrator ill;
    protected ImageListener imglistener = null;
    protected Color scolor;
    protected Color tcolor;
    protected Color pcolor;
    protected Color ccolor;
    protected int count;
    protected int pcount;

    /**
     * Constructor for the BorderCut implementation.
     * @param graph The ImageGraph instance which the algorithm will process.
     */
    public IllustratedUFBorderCut(ImageGraph graph) {
        super(graph);
        ill = new Illustrator();
        imglistener = ill;

    }

    @Override
    protected void init() {
        super.init();

        count = 0;
        pcount = 0;

        scolor = new Color(255,0,0);
        tcolor = new Color(0,0,255);
        pcolor = new Color(0,255,0);
        ccolor = new Color(255,255,0);
    }


    /**
     * Calculate the max flow through the graph.
     * @return The max max flow calculated for the given flow graph.
     */
    @Override
   public double maxFlow() {
        double flow = 0.0;
        double augmented = 1.0;
        double passflow = 1.0;
        //System.out.println("init()");
        init();
        //System.out.println("showTLinks()");
        showTLinks();
        while (passflow > 0.0) {
            passflow = 0.0;
            //System.out.println("new UnionFind()");
            uf = new UnionFind(verts.length,labels);
            do {
                //System.out.println("\tresetting...");

                reset();
                //System.out.println("\tbuilding Trees...");
                makeTrees();
                showLabels();
                //System.out.println("\tfinding cuts...");
                //findCuts();
                cuts = getCuts();
                showCuts();
                //System.out.println("\taugmenting...");
                augmented = augment();
                //System.out.println("\tAugmented = "+BigDecimal.valueOf(augmented).toEngineeringString());
                passflow += augmented;
                count++;
                showTLinks();
                
                //System.out.println("\taugmented = "+augmented);
            } while (augmented > 0);
            flow += passflow;
            //System.out.println("\ttotal flow = "+flow);
        }
        showLabels();
        //showCuts();
        showTLinks();
        
        return flow;
    }

    protected void showLabels() {
        if (imglistener != null) {
            BufferedImage limg = ill.getLabeledImage(graph, this, scolor, tcolor);
            imglistener.labelImage(limg, count);
        }
    }

    protected void showPath(int[] path) {
        if (imglistener != null) {
            BufferedImage pimg = ill.getPathImage(graph, path, pcolor);
            imglistener.pathImage(pimg, pcount);
        }
    }

    protected void showTLinks() {
        if (imglistener != null) {
            BufferedImage simg = ill.getSTLinkImage(graph, src, scolor);
            BufferedImage timg = ill.getTTLinkImage(graph, sink, tcolor);
            imglistener.tlinkImage(simg, src, count);
            imglistener.tlinkImage(timg, sink, count);
        }
    }

    protected void showCuts() {
        if (imglistener != null) {
            BufferedImage cimg = ill.getCutImage(cuts, graph, ccolor);
            //BufferedImage cimg = ill.getCutImage(graph, this, ccolor);
            imglistener.cutImage(cimg, count);
        }
    }


    /**
     * <p>Augment the cuts found after the tree assignments.</p>
     * <p>Each cut is process, the path from the source to the sink
     * is constructed, and the flow is augmented along the resulting
     * path in standard fashion.</p>
     * <p>Processing continues until no more edges remain to be augmented.</p>
     * @return The total flow that was pushed from source to sink in this step.
     */
    @Override
    protected double augment() {
        double total = 0.0;
        double available = 0.0;
        //System.out.println("\t\tConsidering "+cuts.size()+" cuts...");
        int[] path;
        while (!cuts.isEmpty()) {
            Edge e = cuts.remove();
            path = makePath(e.u, e.v);
            available = graph.minAvailable(path);
            graph.augmentPath(available, path);
            graph.removeFullEdges(path);
            //System.out.println("available = "+Double.toString(available));
            total = total + available;
            showPath(path);
            //showTLinks();
            pcount++;
        }
        //System.out.println("Total augmentation = "+total);
        return total;
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
