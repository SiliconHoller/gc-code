/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gcgui.data;

import edu.ohio.graphcuts.NLabelImageGraph;
import edu.ohio.graphcuts.analysis.VectorMath;
import edu.ohio.graphcuts.analysis.features.DataSet;
import edu.ohio.graphcuts.analysis.features.GrayImageDataSet;
import edu.ohio.graphcuts.analysis.features.ImageDataSet;
import edu.ohio.graphcuts.analysis.features.InstanceMap;
import edu.ohio.graphcuts.util.NLabelImageOps;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class NGraphFiller {

    protected BufferedImage img;
    protected double[][] kmeans;
    protected double[][][] covk;
    protected int nLabels;
    protected NLabelImageGraph graph;
    protected ImageDataSet imgData;
    protected int[] labels;
    protected int[] vertices;
    protected VectorMath vmath;

    public NGraphFiller(BufferedImage img, ImageDataSet imgData, double[][] kmeans, double[][][] covk) {
        this.img = img;
        this.imgData = imgData;
        this.kmeans = kmeans;
        this.covk = covk;
        nLabels = kmeans.length;
        vmath = new VectorMath();
    }


    public NLabelImageGraph getFilledGraph() {
        //Get graph structure
        NLabelImageOps nlio = new NLabelImageOps();
        graph = nlio.get4NGraph(img, nLabels);
        vertices = graph.getVertices();
        labels = graph.getLabels();
        fillNLinks();
        fillTLinks();
        return graph;
    }

    protected void fillNLinks() {
        double nsum = 0.0;
        DataSet iset = imgData.getDataSet();
        InstanceMap imap = imgData.getInstanceMap();
        double[][] icov = iset.getCovarianceMatrix();
        diagonalize(icov);
        double det = vmath.getDeterminant(icov);
        //System.out.println("Determinant is "+det);
        double norm = vmath.normalizingFactor(det, icov.length);
        //System.out.println("Normalizing factor is "+norm);
        double[][] invcov = vmath.inverse(icov);
        double[] uvals;
        double[] vvals;
        double cap;
        double expval;
        //double diff;
        for (int u=0;u<vertices.length;u++) {
            if (!graph.isLabel(u)) {
                uvals = imap.get(u).getVals();
                LinkedList<Integer> nu = graph.getEdgesFrom(u);
                for (int v : nu) {
                    if (!graph.isLabel(v)) {
                        vvals = imap.get(v).getVals();
                        //diff = uvals[2]-vvals[2];
                        expval = vmath.mahalanobisDistanceSquared(vvals, uvals, invcov);
                        //cap = norm*Math.exp(-0.5*expval);
                        //expval = (diff*diff)/icov[2][2];
                        cap = Math.exp(-0.5*expval);
                        graph.setCapacity(u, v, cap);
                        nsum += cap;
                        //System.out.println(uvals[2]+"\t"+vvals[2]+"\t"+icov[2][2]+"\t"+cap);
                    }
                }


            }
        }
        System.out.println("N-link flow = "+nsum);
    }

    protected void fillTLinks() {
        for (int k=0;k<labels.length;k++) {
            System.out.println("T-Links for label "+k);
            fillTLinks(labels[k],kmeans[k],covk[k]);
        }
    }

    protected void fillTLinks(int label, double[] mean, double[][] cov) {
        double tsum = 0.0;
        InstanceMap imap = imgData.getInstanceMap();
        double det = vmath.getDeterminant(cov);
        //System.out.println("Determinant is "+det);
        double norm = vmath.normalizingFactor(det, mean.length);
        //System.out.println("Normalizing factor is "+norm);
        double[][] invcov = vmath.inverse(cov);
        //System.out.println("Inverse is ");
        //ArrayOps.printArray(invcov);
        double[] nvals;
        double expval;
        double cap;
        //double diff;
        LinkedList<Integer> nodes = graph.getEdgesFrom(label);
        for (int n : nodes) {
            nvals = imap.get(n).getVals();
            //diff = mean[2]-nvals[2];
            expval = vmath.mahalanobisDistanceSquared(nvals, mean, invcov);
            cap = norm*Math.exp(-0.5*expval);
            //expval = (diff*diff)/cov[2][2];
            //cap = Math.exp(-0.5*expval);
            graph.setCapacity(label, n, cap);
            tsum += cap;
            //System.out.println(mean[2]+"\t"+nvals[2]+"\t"+cov[2][2]+"\t"+cap);
        }
        System.out.println("T-Link flow = "+tsum);
    }

    protected void diagonalize(double[][] sqmatrix) {
        int len = sqmatrix.length;
        for (int i=0;i<len;i++) {
            for (int j=0;j<len;j++) {
                if (i != j) sqmatrix[i][j] = 0.0;
            }
        }
    }
}
