/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util;

import edu.ohio.graphcuts.alg.bradbury.ImageConsumer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class FileConsumer implements ImageConsumer {

    protected String pdir;
    protected String ldir;
    protected String ttdir;
    protected String stdir;
    protected String cdir;
    protected String basedir;
    int src;
    int sink;

    public FileConsumer(String basedir, int src, int sink) {
        this.basedir = basedir;
        this.src = src;
        this.sink = sink;
        pdir = basedir+File.separator+"path";
        ldir = basedir+File.separator+"labels";
        ttdir = basedir+File.separator+"t";
        stdir = basedir+File.separator+"s";
        cdir = basedir + File.separator+"cuts";
        dirCheck();
    }

    private void dirCheck() {
        new File(pdir).mkdirs();
        new File(ldir).mkdirs();
        new File(ttdir).mkdirs();
        new File(stdir).mkdirs();
        new File(cdir).mkdirs();
    }

    public void pathImage(String title, BufferedImage img) {
        PNGSaver ps = new PNGSaver(img, pdir+File.separator+"p" + title);
        Thread t = new Thread(ps);
        t.start();
    }

    public void labelImage(String title, BufferedImage img) {

        PNGSaver ps = new PNGSaver(img, ldir+File.separator+ title);
        Thread t = new Thread(ps);
        t.start();
    }

    public void tlinkImage(String title, BufferedImage img, int label) {
        String pre = (label == src ? "s":"t");
        String dir = (label == src ? stdir:ttdir);

        PNGSaver ps = new PNGSaver(img, dir+File.separator+pre + title);
        Thread t = new Thread(ps);
        t.start();

    }

    public void cutImage(String title, BufferedImage img) {
       PNGSaver ps = new PNGSaver(img, cdir+File.separator+"c"+title);
       Thread t = new Thread(ps);
       t.start();
    }

    public void pathImage(int iteration, BufferedImage img) {
        //no implementation for now
    }

    public void labelImage(int iteration, BufferedImage img) {
        //no implementation for now
    }

    public void tlinkImage(int iteration, BufferedImage img, int label) {
        //no implementation for now
    }

    public void cutImage(int iteration, BufferedImage img) {
        //no implementation for now
    }

    public class PNGSaver implements Runnable {

        private BufferedImage img;
        private String fname;

        public PNGSaver(BufferedImage img, String fname) {
            this.img = img;
            this.fname = fname;
        }

        public void run() {
            try {
                ImageIO.write(img, "png", new File(fname + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(FileConsumer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
