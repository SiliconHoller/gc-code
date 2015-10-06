/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bench;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.UFBorderCut;
import edu.ohio.graphcuts.alg.UnionFind;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

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
public class InstrumentedUFBorderCut extends UFBorderCut implements InstrumentedGraphCut {


    protected long cutCpuTime;
    protected long cutUserTime;
    protected long treeCpuTime;
    protected long treeUserTime;
    protected long findCutCpuTime;
    protected long findCutUserTime;
    protected long augmentCpuTime;
    protected long augmentUserTime;

    /**
     * Constructor for the BorderCut implementation.
     * @param graph The ImageGraph instance which the algorithm will process.
     */
    public InstrumentedUFBorderCut(ImageGraph graph) {
        super(graph);
        cutCpuTime = 0l;
        cutUserTime = 0l;
        treeCpuTime = 0l;
        treeUserTime = 0l;
        findCutCpuTime = 0l;
        findCutUserTime = 0l;
        augmentCpuTime = 0l;
        augmentUserTime = 0l;
    }


    /**
     * Calculate the max flow through the graph.
     * @return The max max flow calculated for the given flow graph.
     */
    @Override
   public double maxFlow() {

        long startCutCpuTime = getCpuTime();
        long startCutUserTime = getUserTime();
        long itreeCpuTime;
        long itreeUserTime;
        long icutCpuTime;
        long icutUserTime;
        long iaugCpuTime;
        long iaugUserTime;

        double flow = 0.0;
        double augmented = 1.0;
        double passflow = 1.0;

        init();

        while (passflow > 0.0) {
            passflow = 0.0;

            uf = new UnionFind(verts.length,labels);
            do {


                reset();
                itreeCpuTime = getCpuTime();
                itreeUserTime = getUserTime();
                makeTrees();
                treeUserTime += (getUserTime() - itreeUserTime);
                treeCpuTime += (getCpuTime() - itreeCpuTime);

                icutCpuTime = getCpuTime();
                icutUserTime = getUserTime();
                cuts = getCuts();
                findCutUserTime += (getUserTime() - icutUserTime);
                findCutCpuTime += (getCpuTime() - icutCpuTime);

                iaugCpuTime = getCpuTime();
                iaugUserTime = getUserTime();
                augmented = augment();
                augmentUserTime += (getUserTime() - iaugUserTime);
                augmentCpuTime += (getCpuTime() - iaugCpuTime);

                passflow += augmented;


            } while (augmented > 0);
            flow += passflow;

        }

        cutUserTime = getUserTime() - startCutUserTime;
        cutCpuTime = getCpuTime() - startCutCpuTime;

        return flow;
    }


    public Map<String, Long> getBenchmarks() {
        HashMap<String,Long> map = new HashMap<String,Long>();
        map.put("Graph vertices", (long)verts.length);
        map.put("Running time (CPU)", cutCpuTime);
        map.put("Running time (User)", cutUserTime);
        map.put("Building Trees (CPU)",treeCpuTime);
        map.put("Building Trees (User)",treeUserTime);
        map.put("Find Cuts (CPU)",findCutCpuTime);
        map.put("Find Cuts (User)",findCutUserTime);
        map.put("Augment (CPU)",augmentCpuTime);
        map.put("Augment (User)",augmentUserTime);

        return map;
    }

    public long getUserTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : 0L;
    }

    public long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
    }

}
