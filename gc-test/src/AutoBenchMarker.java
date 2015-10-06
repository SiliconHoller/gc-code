
import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.bench.InstrumentedBKTreeCut;
import edu.ohio.graphcuts.alg.bench.InstrumentedGraphCut;
import edu.ohio.graphcuts.alg.bench.InstrumentedUFBorderCut;
import edu.ohio.graphcuts.analysis.IntensityKMeans;
import edu.ohio.graphcuts.analysis.KMeans;
import edu.ohio.graphcuts.analysis.RBFCapacityFill;
import edu.ohio.graphcuts.analysis.Statistics;
import edu.ohio.graphcuts.util.PngOps;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class AutoBenchMarker {

    private static ImageGraph loadFillGraph(BufferedImage img) {
        //System.out.println("Creating 4-neighbor graph...");
        PngOps pops = new PngOps();
        ImageGraph graph = pops.get4NGraph(img);
        //System.out.println("K-Means analysis...");
        KMeans km = new IntensityKMeans(graph);
        if (km.analyze()) {
            //System.out.println("Analysis complete...");
        } else {
            System.out.println("Analysis did not converge.");
            return null;
        }
        Statistics stats = new Statistics(km);
        //System.out.println("Filling edge capacities...");
        RBFCapacityFill rbf = new RBFCapacityFill(graph,km,stats);
        rbf.fillCapacities();
        return graph;
    }

    public static void storeResults(Map<String,Long> map, String sdir, String type, String imgFile) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Long time = map.get(key);
            String line = key+"\t"+Long.toString(time)+"\n";
            baos.write(line.getBytes());
        }
        FileOutputStream fos = new FileOutputStream(sdir+File.separator+imgFile+"-"+type+".txt");
        fos.write(baos.toByteArray());
        fos.flush();
        fos.close();
    }

    public static void main(String[] args) {
        //try {
            //InputStreamReader isr = new InputStreamReader(System.in);
            //BufferedReader read = new BufferedReader(isr);

            //System.out.println("Enter the image directory to be processed:  ");
            String idir = "./input";
            //System.out.println("Enter the base directory to store the results:  ");
            String sdir = "./output";
            System.out.println();
            File inputDir = new File(idir);
            ImageGraph graph;
            File[] imglist = inputDir.listFiles();
            for (File imgfile : imglist) {
                System.out.println("Current file:  "+imgfile);
                try {
                    BufferedImage img = ImageIO.read(imgfile);

                    /**
                    graph = loadFillGraph(img);
                    if (graph != null) {
                        System.out.println("Ford-Fulkerson:");
                        InstrumentedGraphCut gc = new InstrumentedFordFulkerson(graph);
                        double flow = gc.maxFlow();
                        System.out.println("ff flow = "+flow);
                        Map<String,Long> benchmap = gc.getBenchmarks();
                        storeResults(benchmap,sdir,"ff",imgfile.getName());
                    }
                    
                    graph = loadFillGraph(img);
                    if (graph != null) {
                        System.out.println("Edmonds-Karp:");
                        InstrumentedGraphCut gc = new InstrumentedEdmondsKarp(graph);
                        double flow = gc.maxFlow();
                        System.out.println("ek flow = "+flow);
                        Map<String,Long> benchmap = gc.getBenchmarks();
                        storeResults(benchmap, sdir, "ek",imgfile.getName());
                    }
                    **/

                    graph = loadFillGraph(img);
                    if (graph != null) {
                        System.out.println("BKTreeCut:");
                        InstrumentedGraphCut gc = new InstrumentedBKTreeCut(graph);
                        double flow = gc.maxFlow();
                        System.out.println("bk flow = "+flow);
                        Map<String,Long> benchmap = gc.getBenchmarks();
                        storeResults(benchmap,sdir,"bk",imgfile.getName());
                    }
                    
                    graph = loadFillGraph(img);
                    if (graph != null) {
                        System.out.println("BorderCut:");
                        InstrumentedGraphCut gc = new InstrumentedUFBorderCut(graph);
                        double flow = gc.maxFlow();
                        System.out.println("border flow = "+flow);
                        Map<String,Long> benchmap = gc.getBenchmarks();
                        storeResults(benchmap,sdir,"border",imgfile.getName());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Finished.  goodbye.");
            
        //} catch (IOException ex) {
        //    Logger.getLogger(BenchMarker.class.getName()).log(Level.SEVERE, null, ex);
        //}


    }

}
