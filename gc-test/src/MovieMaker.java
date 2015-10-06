
import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.bradbury.IllustratedBKTreeCut;
import edu.ohio.graphcuts.alg.bradbury.IllustratedGraphCut;
import edu.ohio.graphcuts.alg.bradbury.IllustratedUFBorderCut;
import edu.ohio.graphcuts.alg.bradbury.Illustrator;
import edu.ohio.graphcuts.analysis.IntensityKMeans;
import edu.ohio.graphcuts.analysis.KMeans;
import edu.ohio.graphcuts.analysis.RBFCapacityFill;
import edu.ohio.graphcuts.analysis.Statistics;
import edu.ohio.graphcuts.util.FileConsumer;
import edu.ohio.graphcuts.util.PngOps;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class MovieMaker {

    public static void main(String[] args) {
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader read = new BufferedReader(isr);
            String gctype = null;
            while (gctype == null) {
                System.out.println("Enter 'bk' for a BK Tree Cut, 'border' for a BorderCut, or 'exit' to quit:");
                gctype = read.readLine();
                if (gctype.equalsIgnoreCase("bk") || gctype.equalsIgnoreCase("border")) {
                    break;
                } else if (gctype.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye.");
                    System.exit(0);
                } else {
                    gctype = null;
                }
            }
            System.out.println("Enter the image file to be processed:  ");
            String ifile = read.readLine();
            System.out.println("Enter the base directory to store the images:  ");
            String sdir = read.readLine();
            System.out.println();
            System.out.println("Reading image...");
            BufferedImage img = ImageIO.read(new File(ifile));
            System.out.println("Creating 4-neighbor graph...");
            PngOps pops = new PngOps();
            ImageGraph graph = pops.get4NGraph(img);
            System.out.println("K-Means analysis...");
            KMeans km = new IntensityKMeans(graph);
            if (km.analyze()) {
                System.out.println("Analysis complete...");
            } else {
                System.out.println("Analysis did not converge...halting.");
                System.exit(1);
            }
            Statistics stats = new Statistics(km);
            System.out.println("Filling edge capacities...");
            RBFCapacityFill rbf = new RBFCapacityFill(graph,km,stats);
            rbf.fillCapacities();

            IllustratedGraphCut gc = null;
            if (gctype.equalsIgnoreCase("bk")) {
                gc = new IllustratedBKTreeCut(graph);
            } else {
                gc = new IllustratedUFBorderCut(graph);
            }

            Illustrator ill = gc.getIllustrator();
            FileConsumer fc = new FileConsumer(sdir, graph.getSrc(), graph.getSink());

            ill.addImageConsumer(fc);

            System.out.println("Beginning graph cut...");
            double flow = gc.maxFlow();
            System.out.println("Calculated max flow is "+flow);
            System.out.println("Goodbye.");
            
        } catch (IOException ex) {
            Logger.getLogger(MovieMaker.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

}
