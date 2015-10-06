/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * STBKCutFrame.java
 *
 * Created on Nov 16, 2010, 1:21:07 PM
 */

package gcgui.frame;

import edu.ohio.graphcuts.ImageGraph;
import edu.ohio.graphcuts.NLabelImageGraph;
import edu.ohio.graphcuts.alg.BorderCut;
import edu.ohio.graphcuts.alg.GraphCut;
import edu.ohio.graphcuts.alg.NLabelBorderCut;
import edu.ohio.graphcuts.analysis.Cuts;
import edu.ohio.graphcuts.analysis.IntensityKMeans;
import edu.ohio.graphcuts.analysis.KMeans;
import edu.ohio.graphcuts.analysis.RBFCapacityFill;
import edu.ohio.graphcuts.analysis.Statistics;
import edu.ohio.graphcuts.util.NLabelImageOps;
import edu.ohio.graphcuts.util.PngOps;
import gcgui.img.CutImage;
import gcgui.panel.ImagePanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

/**
 *
 * @author david
 */
public class STMstCutFrame extends javax.swing.JFrame {

     GraphCut gc;
     NLabelImageGraph graph;
     
     ImagePanel originalPanel;
     ImagePanel kmeansOnePanel;
     ImagePanel kmeansZeroPanel;
     ImagePanel cutPanel;
     ImagePanel srcPanel;
     ImagePanel sinkPanel;

     File imgFile;
     BufferedImage oimg;
     BufferedImage kzeroimg;
     BufferedImage koneimg;
     BufferedImage simg;
     BufferedImage timg;
     BufferedImage cimg;

     JFileChooser jfc;

    /** Creates new form STBKCutFrame */
    public STMstCutFrame() {
        initComponents();
        originalPanel = new ImagePanel();
        kmeansZeroPanel = new ImagePanel();
        kmeansOnePanel = new ImagePanel();
        srcPanel = new ImagePanel();
        sinkPanel = new ImagePanel();
        cutPanel = new ImagePanel();

        originalPanel.setToolTipText("This is the original image scaled to fill the screen.");
        kmeansZeroPanel.setToolTipText("This is the source labeling result for a simple K-Means classification.");
        kmeansOnePanel.setToolTipText("This is the sink labeling result for a simple K-Means classification.");
        srcPanel.setToolTipText("This is the graph cut labeling for the source.");
        sinkPanel.setToolTipText("This is the graph cut labeling for the sink.");
        cutPanel.setToolTipText("This shows the line of the cut.");

        tabPane.addTab("Original", originalPanel);
        tabPane.addTab("K-Means 0", kmeansZeroPanel);
        tabPane.addTab("K-Means 1", kmeansOnePanel);
        tabPane.addTab("Source Labelling", srcPanel);
        tabPane.addTab("Sink Labelling",sinkPanel);
        tabPane.addTab("Image Cut", cutPanel);
    }

    protected void setStatus(String msg) {
        status.setText(msg);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        selButton = new javax.swing.JButton();
        analyzeButton = new javax.swing.JButton();
        cutButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        fillPanel = new javax.swing.JPanel();
        tabPane = new javax.swing.JTabbedPane();
        status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Two-Part Border Cut Segmentation");

        controlPanel.setLayout(new java.awt.BorderLayout());

        buttonPanel.setLayout(new java.awt.GridLayout(0, 1));

        selButton.setText("1. Select Image...");
        selButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        selButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(selButton);

        analyzeButton.setText("2. Auto-Analyze");
        analyzeButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        analyzeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyzeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(analyzeButton);

        cutButton.setText("3. Perform Cut");
        cutButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        cutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cutButton);

        saveButton.setText("Save Results...");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(saveButton);

        controlPanel.add(buttonPanel, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout fillPanelLayout = new javax.swing.GroupLayout(fillPanel);
        fillPanel.setLayout(fillPanelLayout);
        fillPanelLayout.setHorizontalGroup(
            fillPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 169, Short.MAX_VALUE)
        );
        fillPanelLayout.setVerticalGroup(
            fillPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 171, Short.MAX_VALUE)
        );

        controlPanel.add(fillPanel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(controlPanel, java.awt.BorderLayout.WEST);

        tabPane.setPreferredSize(new java.awt.Dimension(500, 500));
        getContentPane().add(tabPane, java.awt.BorderLayout.CENTER);

        status.setText("Status Messages...");
        status.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(status, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void analyzeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyzeButtonActionPerformed
        Thread t = new Thread(new AnalysisRunner());
        t.start();

    }//GEN-LAST:event_analyzeButtonActionPerformed

    private void selButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selButtonActionPerformed
        if (jfc == null) {
            jfc = new JFileChooser();
        }
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int val = jfc.showDialog(this, "Open Image");
        if (val == JFileChooser.APPROVE_OPTION) {
            imgFile = jfc.getSelectedFile();
            try {
                oimg = ImageIO.read(imgFile);
                originalPanel.setImage(oimg);
                kzeroimg = null;
                koneimg = null;
                simg = null;
                timg = null;
                cimg = null;

                kmeansZeroPanel.setImage(kzeroimg);
                kmeansOnePanel.setImage(koneimg);
                srcPanel.setImage(simg);
                sinkPanel.setImage(timg);
                cutPanel.setImage(cimg);
                
                setStatus("Image loaded.");
            } catch (Exception e) {
                e.printStackTrace();
                status.setText("Error loading image...");
            }
        } else {
            setStatus("Selection cancelled.");
        }
    }//GEN-LAST:event_selButtonActionPerformed

    private void cutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutButtonActionPerformed
        Thread t = new Thread(new CutRunner());
        t.start();
    }//GEN-LAST:event_cutButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (jfc == null) {
            jfc = new JFileChooser();
        }
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int val = jfc.showDialog(this,"Select Directory");
        if (val == JFileChooser.APPROVE_OPTION) {
            String sdir = jfc.getSelectedFile().getAbsolutePath();
            String base = imgFile.getName();
            CutImage ci = new CutImage(base, CutImage.BUFFERED_IMAGE);
            CutImage sci = ci.getSrcName();
            CutImage tci = ci.getSinkName();
            CutImage cci = ci.getCutName();
            CutImage kzi = ci.getKName(0);
            CutImage koi = ci.getKName(1);
            try {
                setStatus("Writing original image...");
                ImageIO.write(oimg, "png", new File(sdir+File.separator+ci.getBaseFile()+".png"));
                setStatus("Writing k-means zero label image...");
                ImageIO.write(kzeroimg,"png",new File(sdir+File.separator+kzi.getBaseFile()+".png"));
                setStatus("Writing k-means one label image...");
                ImageIO.write(koneimg,"png", new File(sdir+File.separator+koi.getBaseFile()+".png"));
                setStatus("Writing source label image...");
                ImageIO.write(simg,"png",new File(sdir+File.separator+sci.getBaseFile()+".png"));
                setStatus("Writing sink label image...");
                ImageIO.write(timg,"png",new File(sdir+File.separator+tci.getBaseFile()+".png"));
                setStatus("Writing cut edge image...");
                ImageIO.write(cimg,"png",new File(sdir+File.separator+cci.getBaseFile()+".png"));
                setStatus("Finished!");
            } catch (IOException e) {
                e.printStackTrace();
                setStatus("Problem storing images...");
            }
        }

    }//GEN-LAST:event_saveButtonActionPerformed

    protected void setImages() {
        
        Cuts cuts = new Cuts(graph,gc);

        NLabelImageOps iops = new NLabelImageOps();
        int w = graph.getWidth();
        int h = graph.getHeight();
        int[] labels = graph.getLabels();
        cimg = iops.cutImage(w, h, cuts.getCuts());
        simg = iops.labelImage(labels[0], gc, graph);
        timg = iops.labelImage(labels[1], gc, graph);

        
        kmeansZeroPanel.setImage(kzeroimg);
        kmeansOnePanel.setImage(koneimg);
        srcPanel.setImage(simg);
        sinkPanel.setImage(timg);
        cutPanel.setImage(cimg);        
    }

    protected BufferedImage getImageFor(KMeans kmeans, int clazz, NLabelImageGraph graph) {
        PngOps ops = new PngOps();
        BufferedImage img = ops.blankImage(graph.getWidth(), graph.getHeight());
        int[] verts = graph.getVertices();
        int[] clazzes = kmeans.getClassifications();
        for (int i=0;i<clazzes.length;i++) {
            if (clazzes[i] == clazz) {
                img.setRGB(graph.getX(i), graph.getY(i), ops.makeGrayPixel(verts[i]));
            }
        }
        return img;
    }

    private class AnalysisRunner implements Runnable {

        public void run() {
            disableButtons();
            setStatus("Beginning Analysis...");
            NLabelImageOps ops = new NLabelImageOps();
            setStatus("Creating graph from image...");
            graph = ops.get4NGraph(oimg, 2);
            setStatus("Performing K-Means analysis...");
            KMeans km = new IntensityKMeans(graph);
            if (km.analyze()) {
                setStatus("Analysis complete...");
            } else {
                setStatus("Analysis did not converge...halting.");
                return;
            }
            Statistics stats = new Statistics(km);
            setStatus("Filling edge capacities...");
            RBFCapacityFill rbf = new RBFCapacityFill(graph,km,stats);
            rbf.fillCapacities();
            setStatus("Creating K-Means image for label 0...");
            kzeroimg = getImageFor(km,0,graph);

            setStatus("Creating K-Means image for label 1...");
            koneimg = getImageFor(km,1,graph);

            kmeansZeroPanel.setImage(kzeroimg);
            kmeansOnePanel.setImage(koneimg);

            setStatus("Graph initalized.  Ready to perform cut.");
            enableButtons();
        }

    }

    private class CutRunner implements Runnable {

        public void run() {
            disableButtons();
            setStatus("Performing Graph Cut...");
            gc = new NLabelBorderCut(graph);
            double flow = gc.maxFlow();
            setStatus("Complete.  Max flow is "+Double.toString(flow));
            setImages();
            enableButtons();
        }

    }

    private void disableButtons() {
        selButton.setEnabled(false);
        analyzeButton.setEnabled(false);
        cutButton.setEnabled(false);
        saveButton.setEnabled(false);
    }

    private void enableButtons() {
        selButton.setEnabled(true);
        analyzeButton.setEnabled(true);
        cutButton.setEnabled(true);
        saveButton.setEnabled(true);
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton analyzeButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton cutButton;
    private javax.swing.JPanel fillPanel;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton selButton;
    private javax.swing.JLabel status;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables

}
