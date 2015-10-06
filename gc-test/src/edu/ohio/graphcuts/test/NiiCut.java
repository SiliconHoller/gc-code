/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import edu.ohio.graphcuts.ThreeDImageGraph;
import edu.ohio.graphcuts.util.nii.NiftiData;
import edu.ohio.graphcuts.util.nii.NiiGraphMaker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class NiiCut {

    public static void main(String[] args) {
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            System.out.println("Enter the path to the .nii data.");
            String path = br.readLine();
            System.out.println("Getting NiftiData instance...");
            NiftiData ndata = NiftiData.getMemoryInstance();
            System.out.println("Loading data...");
            ndata.load(path);
            System.out.println("Reading volume data...");
            double[][][] volData = ndata.readDoubleVol((short)0);
            NiiGraphMaker gm = new NiiGraphMaker();
            System.out.println("Creating ThreeDImageGraph...");
            ThreeDImageGraph graph = gm.make6NGraph(volData);
            System.out.println("Graph has following stats:");
            System.out.println("Dimensions are "+graph.getWidth()+"x"+graph.getHeight()+"x"+graph.getZHeight());
            System.out.println("Src = "+graph.getSrc());
            System.out.println("Sink = "+graph.getSink());
        } catch (IOException ex) {
            Logger.getLogger(NiiCut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
