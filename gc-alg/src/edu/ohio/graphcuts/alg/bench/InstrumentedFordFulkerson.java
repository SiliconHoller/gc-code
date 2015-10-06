/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg.bench;


import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.FordFulkerson;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;



/**
 * Implementation of the Ford-Fulkerson min-cut algorithm.
 */
public class InstrumentedFordFulkerson extends FordFulkerson implements InstrumentedGraphCut {

    protected long cutCpuTime;
    protected long cutUserTime;
    protected long searchCpuTime;
    protected long searchUserTime;
    protected long augmentCpuTime;
    protected long augmentUserTime;


    public InstrumentedFordFulkerson(ImageGraph graph) {
        super(graph);
        cutCpuTime = 0l;
        cutUserTime = 0l;
        searchCpuTime = 0l;
        searchUserTime = 0l;
        augmentCpuTime = 0l;
        augmentUserTime = 0l;
    }


    @Override
    public double maxFlow() {
        long isearchCpu;
        long isearchUser;
        long iaugCpu;
        long iaugUser;
        long startCpu = getCpuTime();
        long startUser = getUserTime();

        double max = 0.0;
        parents = new int[vertices.length];
        isearchCpu = getCpuTime();
        isearchUser = getUserTime();
        int[] path = dfs(src,sink);
        searchUserTime += getUserTime() - isearchUser;
        searchCpuTime += getCpuTime() - isearchCpu;
        while (path.length > 0) {

            double minavail = graph.minAvailable(path);
            max += minavail;
            iaugCpu = getCpuTime();
            iaugUser = getUserTime();
            graph.augmentPath(minavail,path);
            augmentUserTime += getUserTime() - iaugUser;
            augmentCpuTime += getCpuTime() - iaugCpu;
            
            graph.removeFullEdges(path);

            isearchCpu = getCpuTime();
            isearchUser = getUserTime();
            path = dfs(src,sink);
            searchUserTime += getUserTime() - isearchUser;
            searchCpuTime += getCpuTime() - isearchCpu;
        }

        cutUserTime = getUserTime() - startUser;
        cutCpuTime = getCpuTime() - startCpu;
        return max;
    }

    public Map<String, Long> getBenchmarks() {
        HashMap<String,Long> map = new HashMap<String,Long>();
        map.put("Graph vertices", (long)graph.getVertices().length);
        map.put("Running time (CPU)", cutCpuTime);
        map.put("Running time (User)", cutUserTime);
        map.put("Search (CPU)",searchCpuTime);
        map.put("Search (User)",searchUserTime);
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
