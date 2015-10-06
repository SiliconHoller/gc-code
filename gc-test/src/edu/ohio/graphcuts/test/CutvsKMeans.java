/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class CutvsKMeans {

    public static void main(String[] args) {
        try {
            PngMaker maker = new PngMaker();
            BufferedImage img = maker.getFadingSpike();
            ImageIO.write(img,"png",new File("fadingspike.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
