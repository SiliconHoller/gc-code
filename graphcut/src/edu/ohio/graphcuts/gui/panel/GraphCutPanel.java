/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GraphCutPanel.java
 *
 * Created on Aug 10, 2010, 12:10:53 AM
 */

package edu.ohio.graphcuts.gui.panel;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import edu.ohio.graphcuts.data.PngOps;
import edu.ohio.graphcuts.gui.io.State;
import edu.ohio.graphcuts.gui.msg.StatusMessage;
import java.awt.image.BufferedImage;

/**
 *
 * @author david
 */
public class GraphCutPanel extends javax.swing.JPanel {

    private ImageGraph graph;
    private GraphCut gc;
    private ImagePanel srcPanel;
    private ImagePanel sinkPanel;

    /** Creates new form GraphCutPanel */
    public GraphCutPanel() {
        initComponents();
        srcPanel = new ImagePanel();
        sinkPanel = new ImagePanel();
        srcPanel.setToolTipText("Source Image");
        sinkPanel.setToolTipText("Sink Image");
        imgPanel.add(srcPanel);
        imgPanel.add(sinkPanel);
    }

    public void setCut(ImageGraph graph, GraphCut gc) {
        this.graph = graph;
        this.gc= gc;
    }

    public void start() {
        Thread t = new Thread(new CutRunner());
        t.start();
    }

    private void showCut(double flow) {
        StatusMessage.getInstance().post("Flow is calculated to be "+Double.toString(flow));
        PngOps ops = State.getInstance().getPngOps();
        int w = graph.getWidth();
        int h = graph.getHeight();
        
        BufferedImage srcImg = ops.srcImage(w,h,gc,graph);
        StatusMessage.getInstance().post("Source image created.");
        BufferedImage sinkImg = ops.sinkImage(w,h,gc,graph);
        StatusMessage.getInstance().post("Sink image created.");
        srcPanel.setImage(srcImg);
        sinkPanel.setImage(sinkImg);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        imgPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(jPanel1);

        imgPanel.setLayout(new java.awt.GridLayout(0, 1));
        add(imgPanel);
    }// </editor-fold>//GEN-END:initComponents

    private class CutRunner implements Runnable {


        public CutRunner() {

        }

        public void run() {
            double flow = gc.maxFlow();
            showCut(flow);
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel imgPanel;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}