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

import edu.ohio.graphcuts.UndirectedNLabelImageGraph;
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
import java.util.Iterator;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;

/**
 *
 * @author david
 */
public class NLabelBorderCutFrame extends javax.swing.JFrame {

     GraphCut gc;
     UndirectedNLabelImageGraph graph;
     
     ImagePanel originalPanel;
     ImagePanel cutPanel;


     Vector<ImagePanel> kPanels;
     Vector<ImagePanel> labelPanels;

     File imgFile;
     BufferedImage oimg;
     BufferedImage cimg;

     JFileChooser jfc;

     String kttip = "This is the labeling result for a simple K-Means classification, Group-";
     String cttip = "These are the cut edges at the end of the GraphCut process.";
     String lttip = "This is the labeling result for the GraphCut, Group-";

     String ktitle = "K-Means Group-";
     String ltitle = "GC Group-";
     String ctitle = "GC Cuts";

     /** Creates new form STBKCutFrame */
    public NLabelBorderCutFrame() {
        initComponents();
        originalPanel = new ImagePanel();
        kPanels = new Vector<ImagePanel>();
        labelPanels = new Vector<ImagePanel>();
        cutPanel = new ImagePanel();

        originalPanel.setToolTipText("This is the original image scaled to fill the screen.");

        cutPanel.setToolTipText("This shows the line of the cut.");

        tabPane.addTab("Original", originalPanel);

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
        numLabelComboBox = new javax.swing.JComboBox();
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

        numLabelComboBox.setModel(new DefaultComboBoxModel(new Integer[] {2,3,4,5,6}));
        numLabelComboBox.setSelectedIndex(0);
        numLabelComboBox.setToolTipText("Select the number of labels (partitions) to be sought in the image.");
        buttonPanel.add(numLabelComboBox);

        analyzeButton.setText("3. Auto-Analyze");
        analyzeButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        analyzeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyzeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(analyzeButton);

        cutButton.setText("4. Perform Cut");
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

                //Remove old image panels, if any
                Iterator<ImagePanel> kit = kPanels.iterator();
                while (kit.hasNext()) {
                    tabPane.remove(kit.next());
                }

                Iterator<ImagePanel> lit = labelPanels.iterator();
                while (lit.hasNext()) {
                    tabPane.remove(lit.next());
                }

                tabPane.remove(cutPanel);
                cimg = null;

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

            try {
                setStatus("Writing original image...");
                ImageIO.write(oimg, "png", new File(sdir+File.separator+ci.getBaseFile()+".png"));
                setStatus("Writing k-means images...");
                Iterator<ImagePanel> kit = kPanels.iterator();
                while (kit.hasNext()) {
                    ImagePanel ip = kit.next();
                    BufferedImage kimg = ip.getImage();
                    ImageIO.write(kimg,"png",new File(sdir+File.separator+base+"-K"+Integer.toString(kPanels.indexOf(ip))+".png"));
                }
                setStatus("Writing label images...");
                Iterator<ImagePanel> lit = labelPanels.iterator();
                while (lit.hasNext()) {
                    ImagePanel ip = lit.next();
                    BufferedImage limg = ip.getImage();
                    ImageIO.write(limg,"png", new File(sdir+File.separator+base+"-L"+Integer.toString(labelPanels.indexOf(ip))+".png"));
                }
                setStatus("Writing cut edge image...");
                ImageIO.write(cimg,"png",new File(sdir+File.separator+ci.getCutName()+".png"));
                setStatus("Finished!");
            } catch (IOException e) {
                e.printStackTrace();
                setStatus("Problem storing images...");
            }
        }

    }//GEN-LAST:event_saveButtonActionPerformed

    protected void setImages() {
        
        Cuts cuts = new Cuts(graph,gc);

        PngOps pngops = new PngOps();
        NLabelImageOps mops = new NLabelImageOps();
        int w = graph.getWidth();
        int h = graph.getHeight();

        cimg = pngops.cutImage(w, h, cuts.getCuts());

        int[] labels = graph.getLabels();
        
        for (int m=0;m<labels.length;m++) {
            setStatus("Creating image for Label "+Integer.toString(m)+"...");
            BufferedImage limg = mops.labelImage(labels[m], gc, graph);
            ImagePanel ipan = new ImagePanel();
            ipan.setImage(limg);
            ipan.setToolTipText(lttip+Integer.toString(m));
            tabPane.add(ltitle+Integer.toString(m),ipan);
            labelPanels.add(ipan);
        }

        cutPanel.setImage(cimg);
        tabPane.add(ctitle,cutPanel);
    }

    public BufferedImage createClassImage(int clazz, KMeans kmeans, int w, int h, NLabelImageOps mops) {
        BufferedImage img = mops.blankImage(w, h);
        int[] classes = kmeans.getClassifications();
        double[][] features = kmeans.getFeatures();
        int pos = 0;
        int max = w*h;
        int[] verts = graph.getVertices();
        while (pos < max) {
            if (features[pos].length != 0) {
                if (classes[pos] == clazz) {
                    //double[] f = features[pos];
                    img.setRGB(graph.getX(pos), graph.getY(pos), mops.makeGrayPixel(verts[pos]));
                }
            }
            pos++;
        }
        return img;
    }

    private class AnalysisRunner implements Runnable {

        public void run() {
            disableButtons();
            setStatus("Beginning Analysis...");
            NLabelImageOps ops = new NLabelImageOps();
            setStatus("Creating graph from image...");
            int num = ((Integer)numLabelComboBox.getSelectedItem()).intValue();
            graph = ops.getUndirected4NGraph(oimg,num);
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
            setStatus("Creating K-Means images...");

            int[] labels = graph.getLabels();
            for (int i=0;i<labels.length;i++) {
                setStatus("Creating image for Group "+Integer.toString(i)+"...");
                BufferedImage kimg = createClassImage(i, km, graph.getWidth(), graph.getHeight(), ops);
                //Create panel and add
                ImagePanel ipan = new ImagePanel();
                ipan.setImage(kimg);
                ipan.setToolTipText(kttip+Integer.toString(i));
                tabPane.add(ktitle+Integer.toString(i),ipan);
                kPanels.add(ipan);
            }

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
    private javax.swing.JComboBox numLabelComboBox;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton selButton;
    private javax.swing.JLabel status;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables

}