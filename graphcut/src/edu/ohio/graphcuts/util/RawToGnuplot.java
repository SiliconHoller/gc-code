/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author david
 */
public class RawToGnuplot {

    public static void main(String[] args) {
        try {
            String imgfile = "/home/david/Desktop/graph-cut/mr.raw";
            int w = 256;
            int h = 256;
            FileInputStream fis = new FileInputStream(imgfile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int k = 0;
            while ((k = fis.read()) != -1) {
                baos.write(k);
            }
            fis.close();
            byte[] data = baos.toByteArray();
            byte[] space = new String(" ").getBytes();
            byte[] nl = new String("\n").getBytes();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int i=0;i<data.length;i++) {
                int x = i/w;
                int y = i%w;
                int z = data[i] & 0xFF;
                out.write(Integer.toString(x).getBytes());
                out.write(space);
                out.write(Integer.toString(y).getBytes());
                out.write(space);
                out.write(Integer.toString(z).getBytes());
                out.write(nl);
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
