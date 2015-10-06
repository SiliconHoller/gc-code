/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class PngToGnuplot {

    public static void main(String[] args) {
        try {
            String imgfile = args[0];
            BufferedImage img = ImageIO.read(new File(imgfile));
            int w = img.getWidth();
            int h = img.getHeight();
            PngOps ops = new PngOps();
            byte[] space = new String(" ").getBytes();
            byte[] nl = new String("\n").getBytes();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int pixel = 0;
            for (int x=0;x<w;x++) {
                for (int y=0;y<h;y++) {
                    pixel = img.getRGB(x, y);
                    if (ops.isVisible(pixel)) {
                        out.write(Integer.toString(x).getBytes());
                        out.write(space);
                        out.write(Integer.toString(y).getBytes());
                        out.write(space);
                        out.write(Integer.toString(ops.getGrayVal(pixel)).getBytes());
                        out.write(nl);
                    }
                }
            }
            FileOutputStream fos = new FileOutputStream(imgfile+".plot");
            fos.write(out.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
