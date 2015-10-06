/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.gui.io;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.data.PngOps;
import edu.ohio.graphcuts.data.RawCodec;
import edu.ohio.graphcuts.gui.tree.CutImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Handles basic File, image, and CutImage utility operations for the application
 * @author david
 */
public class SegTreeIO {


    
    public static ImageGraph get4NRawGraph(String fileName, int w, int h) throws IOException {
        ImageGraph graph = null;
        RawCodec codec = new RawCodec(new File(fileName), w, h);
        graph = codec.create4NGraph();
        return graph;
    }

    public static ImageGraph get8NRawGraph(String fileName, int w, int h) throws IOException {
        ImageGraph graph = null;
        RawCodec codec = new RawCodec(new File(fileName), w, h);
        graph = codec.create8NGraph();
        return graph;
    }
    
    public static ImageGraph get4NPngGraph(String pngFile) throws IOException {
        ImageGraph graph = null;
        BufferedImage img = ImageIO.read(new File(pngFile));
        PngOps ops = new PngOps();
        graph = ops.get4NGraph(img);
        return graph;
    }

    public static ImageGraph get8NPngGraph(String pngFile) throws IOException {
        ImageGraph graph = null;
        BufferedImage img = ImageIO.read(new File(pngFile));
        PngOps ops = new PngOps();
        graph = ops.get8NGraph(img);
        return graph;
    }

    public static void savePNG(BufferedImage img, File file) throws IOException {
        ImageIO.write(img, "png", file);
    }

    public static CutImage saveSrcSplitFor(CutImage cimg, BufferedImage img, String baseDir) throws IOException {
        CutImage outImg = cimg.getSrcName();
        String outName = baseDir + File.separator + outImg.getBaseFile() + ".png";
        File outFile = new File(outName);
        savePNG(img, outFile);
        return outImg;
    }

    public static CutImage saveSinkSplitFor(CutImage cimg, BufferedImage img, String baseDir) throws IOException {
        CutImage outImg = cimg.getSinkName();
        String outName = baseDir + File.separator + outImg.getBaseFile() +".png";
        File outFile = new File(outName);
        savePNG(img, outFile);
        return outImg;
    }

    public static CutImage saveCutImageFor(CutImage cimg, BufferedImage img, String baseDir) throws IOException {
        CutImage outImg = cimg.getCutName();
        String outName = baseDir + File.separator + outImg.getBaseFile()+".png";
        File outFile = new File(outName);
        savePNG(img, outFile);
        return outImg;
    }

}
