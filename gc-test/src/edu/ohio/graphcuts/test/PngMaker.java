/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import edu.ohio.graphcuts.util.PngOps;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class PngMaker {
    
    public PngMaker(){
        //empty constructor
    }

    protected int getDiagVal(int x, int y, int w, int h) {
        int pixel = 0;
        int alpha = 255;
        int red = 0;
        int green = 0;
        int blue = 0;
        if ((x+y) < (w-1)) {
            //leave defaults
        } else if ((x+y) > w+1) {
            red = 255;
            green = 255;
            blue = 255;
        } else {
            red = 128;
            green = 128;
            blue = 128;
        }
        pixel = ((alpha << 24) & 0xFF000000) | ((red << 16) & 0x00FF0000)
                | ((green << 8) & 0x0000FF00) | ((blue & 0x000000FF));
        return pixel;
    }

    protected int getThreeVal(int x, int y, int w, int h) {
        int pixel = 0;
        int alpha = 255;
        int red = 0;
        int green = 0;
        int blue = 0;
        if ((x) < (w/3)) {
            //leave defaults
        } else if (x > w/3 && x < (2*w)/3) {
            red = 255;
            green = 255;
            blue = 255;
        } else {
            red = 128;
            green = 128;
            blue = 128;
        }
        pixel = ((alpha << 24) & 0xFF000000) | ((red << 16) & 0x00FF0000)
                | ((green << 8) & 0x0000FF00) | ((blue & 0x000000FF));

        return pixel;
    }


    public BufferedImage getDiagImg(int w, int h) {
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                img.setRGB(i, j, getDiagVal(i,j,w,h));
            }
        }
        return img;
    }

    public BufferedImage getThreePartImg(int w, int h) {
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                img.setRGB(i,j, getThreeVal(i,j,w,h));
            }
        }
        return img;
    }

    public BufferedImage getFadingSpike() {
        PngOps ops = new PngOps();
        BufferedImage img = ops.blankImage(512, 512);
        //first, set all values to 0
        int black = ops.makeGrayPixel(0);
        for (int x = 0;x<512;x++) {
            for (int y=0;y<512;y++) {
                img.setRGB(x,y,black);
            }
        }

        //Next, make a white square near the middle-left
        int white = ops.makeGrayPixel(255);
        for (int x=10;x<110;x++) {
            for (int y=200;y<300;y++) {
                img.setRGB(x,y,white);
            }
        }

        //Finally, create a fading line extending to the right
        int gray = 0;
        for (int x=110;x<356;x++) {
            gray = ops.makeGrayPixel(365-x);
            for (int y=240;y<261;y++) {
                img.setRGB(x,y,gray);
            }
        }
        return img;
    }

    public int getGrayVal(int pixel) {
        int val = 0;
        int alpha = 0;

        alpha = pixel & 0xFF000000;
        if (alpha == 0) {
            return -1;
        }
        int red = pixel & 0x00FF0000;
        int green = pixel & 0x0000FF00;
        int blue = pixel & 0x000000FF;
        val = (red+green+blue)/3;
        return val;
    }

    public static void main(String[] args) {
        PngMaker maker = new PngMaker();
        BufferedImage img = maker.getThreePartImg(64,64);
        try {
            ImageIO.write(img, "png", new File("three64.png"));
        } catch (IOException ex) {
            Logger.getLogger(PngMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
