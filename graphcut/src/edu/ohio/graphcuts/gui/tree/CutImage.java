/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.gui.tree;

import edu.ohio.graphcuts.ImageGraph;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author david
 */
public class CutImage {

    private String baseFile;
    private BufferedImage img = null;
    private int w = -1;
    private int h = -1;
    public static final int RAWFILE = 0;
    public static final int PHOTOFILE = 1;
    public static final int BUFFERED_IMAGE = 2;
    public static final int CUT_IMAGE = 4;
    private int type;

    public CutImage(String rawFile, int w, int h) {
        type = RAWFILE;
        this.w = w;
        this.h = h;
        this.baseFile = rawFile;
        stripSuffix();
    }

    public CutImage(String imgFile) {
        type = PHOTOFILE;
        this.baseFile = imgFile;
        stripSuffix();
    }

    public CutImage(String imgFile, int type) {
        this.type = type;
        this.baseFile = imgFile;
        stripSuffix();
    }

    public CutImage(String imgFile, BufferedImage img) {
        type = BUFFERED_IMAGE;
        this.img = img;
        this.w = img.getWidth();
        this.h = img.getHeight();
        this.baseFile = imgFile;
        stripSuffix();
    }

    public String getBaseFile() {
        return baseFile;
    }

    public int getType() {
        return type;
    }

    protected void stripSuffix() {
        if (baseFile.endsWith(".png") || baseFile.endsWith(".PNG") ||
                baseFile.endsWith(".jpg") || baseFile.endsWith(".JPG") ||
                baseFile.endsWith(".jpeg") || baseFile.endsWith(".JPEG") ||
                baseFile.endsWith(".raw") || baseFile.endsWith(".RAW")) {
            String baseName = "";
            baseName = baseFile.substring(0,baseFile.lastIndexOf("."));
            baseFile = baseName;
        }
    }

    public CutImage getSrcName() {
        String srcname = baseFile+"S";
        return new CutImage(srcname,CutImage.PHOTOFILE);
    }

    public CutImage getSinkName() {
        String tname = baseFile+"T";
        return new CutImage(tname,CutImage.PHOTOFILE);
    }

    public CutImage getCutName() {
        String cname = "C"+baseFile;
        return new CutImage(cname,CutImage.CUT_IMAGE);
    }

    @Override
    public String toString() {
        return baseFile;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof CutImage)) return false;
        return baseFile.equals(((CutImage)o).getBaseFile());
    }
}
