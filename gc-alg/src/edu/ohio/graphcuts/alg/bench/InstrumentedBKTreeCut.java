/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bench;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.BKTreeCut;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author david
 */
public class InstrumentedBKTreeCut extends BKTreeCut implements InstrumentedGraphCut {


    protected long cutCpuTime;
    protected long cutUserTime;
    protected long treeCpuTime;
    protected long treeUserTime;
    protected long adoptCpuTime;
    protected long adoptUserTime;
    protected long augmentCpuTime;
    protected long augmentUserTime;

    public InstrumentedBKTreeCut(ImageGraph graph) {
        super(graph);
        cutCpuTime = 0l;
        cutUserTime = 0l;
        treeCpuTime = 0l;
        treeUserTime = 0l;
        adoptCpuTime = 0l;
        adoptUserTime = 0l;
        augmentCpuTime = 0l;
        augmentUserTime = 0l;
    }


    @Override
    public double maxFlow() {

        long startCutCpuTime = getCpuTime();
        long startCutUserTime = getUserTime();
        long itreeCpuTime;
        long itreeUserTime;
        long iadoptCpuTime;
        long iadoptUserTime;
        long iaugCpuTime;
        long iaugUserTime;

        double maxFlow = 0.0;
        init();
        active.offer(src);
        active.offer(sink);
        int[] path = new int[1];
        while (path.length != 0) {

            itreeCpuTime = getCpuTime();
            itreeUserTime = getUserTime();
            path = grow();
            treeUserTime += (getUserTime() - itreeUserTime);
            treeCpuTime += (getCpuTime() - itreeCpuTime);

            if (path.length == 0) {
                cutUserTime = getUserTime() - startCutUserTime;
                cutCpuTime = getCpuTime() - startCutCpuTime;
                return maxFlow;
            }

            iaugCpuTime = getCpuTime();
            iaugUserTime = getUserTime();
            maxFlow += augment(path);
            augmentUserTime += (getUserTime() - iaugUserTime);
            augmentCpuTime += (getCpuTime() - iaugCpuTime);

            iadoptCpuTime = getCpuTime();
            iadoptUserTime = getUserTime();
            adopt();
            adoptUserTime += getUserTime() - iadoptUserTime;
            adoptCpuTime += getCpuTime() - iadoptCpuTime;

        }
        cutUserTime = getUserTime() - startCutUserTime;
        cutCpuTime = getCpuTime() - startCutCpuTime;

        return maxFlow;
    }

    public Map<String, Long> getBenchmarks() {
        HashMap<String,Long> map = new HashMap<String,Long>();
        map.put("Graph vertices", (long)vertices.length);
        map.put("Running time (CPU)", cutCpuTime);
        map.put("Running time (User)", cutUserTime);
        map.put("Building Trees (CPU)",treeCpuTime);
        map.put("Building Trees (User)",treeUserTime);
        map.put("Adopt (CPU)",adoptCpuTime);
        map.put("Adopt (User)",adoptUserTime);
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
