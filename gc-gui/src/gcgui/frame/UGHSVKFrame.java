/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UserGuidedFrame.java
 *
 * Created on Mar 13, 2011, 6:06:10 PM
 */

package gcgui.frame;

import edu.ohio.graphcuts.ArrayOps;
import edu.ohio.graphcuts.NLabelImageGraph;
import edu.ohio.graphcuts.alg.GraphCut;
import edu.ohio.graphcuts.alg.NLabelBorderCut;
import edu.ohio.graphcuts.analysis.DataSetKMeans;
import edu.ohio.graphcuts.analysis.features.DataSet;
import edu.ohio.graphcuts.analysis.features.HSVImageDataSet;
import edu.ohio.graphcuts.analysis.features.Instance;
import gcgui.data.HSV2DSample;
import gcgui.data.NGraphFiller;
import gcgui.data.Sample;
import gcgui.data.SampleSet;
import gcgui.event.ImageSampler;
import gcgui.event.SampleListener;
import gcgui.img.SampleRenderer;
import gcgui.img.SegmentationRenderer;
import gcgui.panel.ImagePanel;
import gcgui.panel.LayeredImagePanel;
import gcgui.widgets.SampleSetListPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class UGHSVKFrame extends javax.swing.JFrame implements SampleListener {

    private SampleSetListPanel ssList;
    private SampleRenderer srenderer;
    private ImageSampler isampler;
    private SampleSet sset;
    private DataSet dset;
    private HSVImageDataSet hsvset;
    private LayeredImagePanel ipanel;
    private Sample currentSample;
    private File imgfile;
    private BufferedImage cimg;
    private BufferedImage simg;
    private JFileChooser fchooser;
    private Vector<JPanel> segPanels = new Vector<JPanel>();
    private double[][] kmeans;
    private double[][][] covk;
    private BufferedImage outline;
    private BufferedImage colored;
    private BufferedImage[] segmented;


    /** Creates new form UserGuidedFrame */
    public UGHSVKFrame() {
        initComponents();
        //Create components
        sset = new SampleSet();
        ipanel = new LayeredImagePanel();
        isampler = new ImageSampler(ipanel);
        srenderer = new SampleRenderer();
        srenderer.sampleSet(sset);
        ssList = new SampleSetListPanel();
        isampler.addSampleListener(this);
        //Configure and place components appropriately
        ssList.setSampleSet(sset);
        ipanel.addMouseListener(isampler);
        ipanel.addMouseMotionListener(isampler);
        add(ssList, BorderLayout.WEST);
        tabPane.add("Sampling",ipanel);

    }

    private void clearPanels() {
        Iterator<JPanel> it = segPanels.iterator();
        while (it.hasNext()) {
            tabPane.remove(it.next());
        }
    }

    private void showResults(GraphCut gc, NLabelImageGraph graph) {
        
        segPanels = new Vector<JPanel>();
        Color[] colors = srenderer.getColors();
        int[] labels = graph.getLabels();
        //Get the result images
        SegmentationRenderer segr = new SegmentationRenderer();
        outline = segr.outline(cimg, gc, graph, labels, colors);
        colored = segr.getColoredImage(graph, gc, colors);
        BufferedImage[] outlines = segr.getOutlines(graph, gc, colors);
        segmented = segr.getSegmentedImages(graph, gc, cimg);
        //Show the resulting images
        ImagePanel olpanel = new ImagePanel();
        olpanel.setImage(outline);
        addToTab("Outline",olpanel);
        ImagePanel cpanel = new ImagePanel();
        cpanel.setImage(colored);
        addToTab("Filled",cpanel);
        String t;
        ImagePanel ip;
        for (int i=0;i<outlines.length;i++) {
            t = "Outline-"+Integer.toString(i);
            ip = new ImagePanel();
            ip.setImage(outlines[i]);
            addToTab(t,ip);
        }
        for (int j=0;j<segmented.length;j++) {
            t = "Segment-"+Integer.toString(j);
            ip = new ImagePanel();
            ip.setImage(segmented[j]);
            addToTab(t,ip);
        }
    }

    private void addToTab(String title, JPanel panel) {
        segPanels.add(panel);
        tabPane.addTab(title,panel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        sampleButton = new javax.swing.JToggleButton();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        runButton = new javax.swing.JButton();
        tabPane = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        loadMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        saveMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("User-Initialized and K-Means-Smooth N-Label Border Cut");

        sampleButton.setText("Sample");
        sampleButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sampleButtonStateChanged(evt);
            }
        });
        buttonPanel.add(sampleButton);

        addButton.setText("Add Sample");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(addButton);

        removeButton.setText("Remove Sample");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(removeButton);

        jLabel1.setText("     ");
        buttonPanel.add(jLabel1);

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(runButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);
        getContentPane().add(tabPane, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        loadMenuItem.setText("Load Image...");
        loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(loadMenuItem);
        jMenu1.add(jSeparator1);

        saveMenuItem.setText("Save Results...");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(saveMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMenuItemActionPerformed
        if (fchooser == null) {
            fchooser = new JFileChooser();
        }
        int val = fchooser.showOpenDialog(this);
        if (val == JFileChooser.APPROVE_OPTION) {
            imgfile = fchooser.getSelectedFile();
            loadFile();
        }
        
    }//GEN-LAST:event_loadMenuItemActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        Sample s = new HSV2DSample();
        ssList.addSample(s);
    }//GEN-LAST:event_addButtonActionPerformed

    private void sampleButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sampleButtonStateChanged
        isampler.setSampling(sampleButton.isSelected());
    }//GEN-LAST:event_sampleButtonStateChanged

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        Sample s = ssList.getSelectedSample();
        if (s != null) {
            ssList.removeSample(s);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        runData();
    }//GEN-LAST:event_runButtonActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        JFileChooser sfc = new JFileChooser();
        sfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int val = sfc.showSaveDialog(this);
        if (val == JFileChooser.APPROVE_OPTION) {
            String sdir = sfc.getSelectedFile().getPath();
            try {
                storeResults(sdir);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void loadFile() {
        try {
            cimg = ImageIO.read(imgfile);
            ipanel.setBackgroundImage(cimg);
            clearPanels();
            sset = new SampleSet();
            ssList.setSampleSet(sset);
            srenderer.sampleSet(sset);
            ipanel.setForegroundImage(null);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void showImage() {
        ipanel.setForegroundImage(simg);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem loadMenuItem;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton runButton;
    private javax.swing.JToggleButton sampleButton;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables

    public void sample(Instance inst) {
        currentSample = ssList.getSelectedSample();
        if (currentSample != null) {
            currentSample.add(inst);
            renderImage();
        }
    }

    public void sample(Collection<Instance> c) {
        currentSample = ssList.getSelectedSample();
        if (currentSample != null) {
            currentSample.add(c);
            renderImage();
        }
    }

    public void renderImage() {
        try {
            simg = srenderer.render(cimg);
            showImage();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void runData() {
        Collection<DataSet> dsets = sset.getDataSets(cimg);
        System.out.println("Creating means set...");
        makeKMeans(dsets);

        System.out.println("Creating covariance set...");
        makeCovk(dsets);
        printData();
        //Perform cut
        System.out.println("Creating filled graph...");
        NGraphFiller ngf = new NGraphFiller(cimg,hsvset,kmeans, covk);
        NLabelImageGraph graph = ngf.getFilledGraph();
        System.out.println("Creating GraphCut...");
        GraphCut gc = new NLabelBorderCut(graph);
        System.out.println("Performing cut...");
        double flow = gc.maxFlow();
        showResults(gc,graph);
    }

    private void printData() {
        int len = kmeans.length;
        for (int k=0;k<len;k++) {
            System.out.println("Mean for class "+k);
            ArrayOps.printArray(kmeans[k]);
            System.out.println("Covariance for class "+k);
            ArrayOps.printArray(covk[k]);
        }
    }

    private void makeKMeans(Collection<DataSet> dsets) {
        int num = dsets.size();
        kmeans = new double[num][0];
        int i = 0;
        DataSet dkset;
        Iterator<DataSet> it = dsets.iterator();
        while (i < num) {
            dkset = it.next();
            kmeans[i] = dkset.getMean();
            i++;
        }
        //Now perform a kmeans analysis on the data
        System.out.println("Performing K-Means analysis...");
        System.out.println("\tCreating HSV data set...");
        hsvset = new HSVImageDataSet(cimg);
        dset = hsvset.getDataSet();
        System.out.println("\tInitializing kmeans analysis...");
        DataSetKMeans dkm = new DataSetKMeans(dset);
        System.out.println("\tPartitioning...");
        dkm.partition(kmeans);
        System.out.println("Finished with K-Means analysis...");
        kmeans = dkm.getMeans();

    }

    private void makeCovk(Collection<DataSet> dsets) {
        int num = dsets.size();
        covk = new double[num][0][0];
        for (int i=0;i<num;i++) {
            covk[i] = dset.getCovarianceMatrix(i);
        }
    }

    private void storeResults(String sdir) throws IOException {
        BufferedImage sampleimage = new BufferedImage(ipanel.getWidth(), ipanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sampleimage.createGraphics();
        ipanel.paint(g);
        ImageIO.write(sampleimage,"png", new File(sdir+File.separator+"sampling.png"));
        ImageIO.write(cimg, "png", new File(sdir+File.separator+"original.png"));
        ImageIO.write(outline,"png",new File(sdir+File.separator+"outlined.png"));
        ImageIO.write(colored, "png", new File(sdir+File.separator+"labeled.png"));
        for (int j=0;j<segmented.length;j++) {
            String name = "segment-"+Integer.toString(j)+".png";
            ImageIO.write(segmented[j],"png",new File(sdir+File.separator+name));
        }
    }

}
