/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ImagePanel.java
 *
 * Created on Aug 1, 2010, 12:00:08 AM
 */

package edu.ohio.graphcuts.gui.panel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 *
 * @author david
 */
public class ImagePanel extends javax.swing.JPanel {

    private BufferedImage img;
    private double iw;
    private double ih;

    /** Creates new form ImagePanel */
    public ImagePanel() {
        img = null;
        initComponents();
    }

    public void setImage(BufferedImage img) {
        this.img = img;
        if (img != null) {
            iw = (double)img.getWidth();
            ih = (double)img.getHeight();
        } else {
            iw = 1.0;
            ih = 1.0;
        }
        repaint();
    }

    public BufferedImage getImage() {
        return img;
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        double xscale = ((double)getWidth())/iw;
        double yscale = ((double)getHeight())/ih;
        AffineTransform at = AffineTransform.getScaleInstance(xscale,yscale);
        AffineTransformOp aop = new AffineTransformOp(at,AffineTransformOp.TYPE_BICUBIC);
        g2.drawImage(img, aop, 0,0);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


}
