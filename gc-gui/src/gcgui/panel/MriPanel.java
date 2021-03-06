/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MriPanel.java
 *
 * Created on Jan 29, 2011, 6:29:35 PM
 */

package gcgui.panel;

import edu.ohio.graphcuts.util.nii.NiftiData;
import edu.ohio.graphcuts.util.nii.NiftiHeader;
import gcgui.data.Volume;
import gcgui.event.CoordinateListener;
import gcgui.img.SubVolRenderer;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class MriPanel extends javax.swing.JPanel implements ChangeListener, CoordinateListener {

    private XYSlicePanel xys;
    private XZSlicePanel xzs;
    private YZSlicePanel yzs;
    private CoordToolBar coordbar;
    private NiftiData ndata;
    private Volume volume;
    private SubVolRenderer svr;


    private int ox;
    private int oy;
    private int oz;
    private int dx;
    private int dy;
    private int dz;
    

    /** Creates new form MriPanel */
    public MriPanel() {
        initComponents();

        

        xys = new XYSlicePanel();
        xzs = new XZSlicePanel();
        yzs = new YZSlicePanel();

        xys.setFocusable(true);
        xzs.setFocusable(true);
        yzs.setFocusable(true);

        svr = new SubVolRenderer();
        xys.setSubVolRenderer(svr);
        xzs.setSubVolRenderer(svr);
        yzs.setSubVolRenderer(svr);

        svr.addChangeListener(xys);
        svr.addChangeListener(xzs);
        svr.addChangeListener(xzs);

        coordbar = new CoordToolBar();
        xys.addCoordinateListener(coordbar);
        xzs.addCoordinateListener(coordbar);
        yzs.addCoordinateListener(coordbar);

        xys.addCoordinateListener(this);
        xzs.addCoordinateListener(this);
        yzs.addCoordinateListener(this);

        xys.addCoordinateListener(svr);
        xzs.addCoordinateListener(svr);
        yzs.addCoordinateListener(svr);

        add(coordbar, BorderLayout.NORTH);

        xyPanel.add(xys, BorderLayout.CENTER);
        xzPanel.add(xzs, BorderLayout.CENTER);
        yzPanel.add(yzs, BorderLayout.CENTER);

        
    }

    public void setNiftiData(NiftiData ndata) throws IOException {
        this.ndata = ndata;
        loadData();
        setMaxes();
    }

    private void loadData() throws IOException {
        NiftiHeader header = ndata.getHeader();
        int t = 0;
        volume = new Volume();
        double[][][] voldat = ndata.readDoubleVol((short) t);
        volume.setVolume(voldat);
        xys.setVolume(volume);
        xzs.setVolume(volume);
        yzs.setVolume(volume);
    }

    private void setMaxes() {
        int xmax = volume.getWidth();
        int ymax = volume.getHeight();
        int zmax = volume.getZHeight();
        xSlider.setMinimum(-xmax);
        xSlider.setMaximum(xmax);
        xSlider.setValue(10);
        ySlider.setMaximum(ymax);
        ySlider.setMinimum(-ymax);
        ySlider.setValue(10);
        zSlider.setMaximum(zmax);
        zSlider.setMinimum(-zmax);
        zSlider.setValue(10);
    }

    public void setPlanes(int x, int y, int z) {
        yzs.setXVal(x);
        xzs.setYVal(y);
        xys.setZVal(z);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        xyPanel = new javax.swing.JPanel();
        controlPanel1 = new javax.swing.JPanel();
        decZButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        zLabel = new javax.swing.JLabel();
        incZButton = new javax.swing.JButton();
        xzPanel = new javax.swing.JPanel();
        controlPanel2 = new javax.swing.JPanel();
        decYButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        yLabel = new javax.swing.JLabel();
        incYButton = new javax.swing.JButton();
        yzPanel = new javax.swing.JPanel();
        controlPanel3 = new javax.swing.JPanel();
        decXButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        xLabel = new javax.swing.JLabel();
        incXButton = new javax.swing.JButton();
        subToolBar = new javax.swing.JToolBar();
        subVolCheckBox = new javax.swing.JCheckBox();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jLabel9 = new javax.swing.JLabel();
        originLabel = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jPanel1 = new javax.swing.JPanel();
        xSlider = new javax.swing.JSlider();
        ySlider = new javax.swing.JSlider();
        zSlider = new javax.swing.JSlider();
        jLabel13 = new javax.swing.JLabel();
        volToggleButton = new javax.swing.JToggleButton();

        setLayout(new java.awt.BorderLayout());

        xyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("XY-Slice"));
        xyPanel.setLayout(new java.awt.BorderLayout());

        decZButton.setText("<");
        decZButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decZButtonActionPerformed(evt);
            }
        });
        controlPanel1.add(decZButton);

        jLabel1.setText("Z:");
        controlPanel1.add(jLabel1);

        zLabel.setText("0");
        controlPanel1.add(zLabel);

        incZButton.setText(">");
        incZButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incZButtonActionPerformed(evt);
            }
        });
        controlPanel1.add(incZButton);

        xyPanel.add(controlPanel1, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab("XY", xyPanel);

        xzPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("XZ-Slice"));
        xzPanel.setLayout(new java.awt.BorderLayout());

        decYButton.setText("<");
        decYButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decYButtonActionPerformed(evt);
            }
        });
        controlPanel2.add(decYButton);

        jLabel2.setText("Y:");
        controlPanel2.add(jLabel2);

        yLabel.setText("0");
        controlPanel2.add(yLabel);

        incYButton.setText(">");
        incYButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incYButtonActionPerformed(evt);
            }
        });
        controlPanel2.add(incYButton);

        xzPanel.add(controlPanel2, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab("XZ", xzPanel);

        yzPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("YZ-Slice"));
        yzPanel.setLayout(new java.awt.BorderLayout());

        decXButton.setText("<");
        decXButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decXButtonActionPerformed(evt);
            }
        });
        controlPanel3.add(decXButton);

        jLabel3.setText("X:");
        controlPanel3.add(jLabel3);

        xLabel.setText("0");
        controlPanel3.add(xLabel);

        incXButton.setText(">");
        incXButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incXButtonActionPerformed(evt);
            }
        });
        controlPanel3.add(incXButton);

        yzPanel.add(controlPanel3, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab("YZ", yzPanel);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        subToolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        subToolBar.setRollover(true);

        subVolCheckBox.setFont(new java.awt.Font("DejaVu Sans", 1, 10));
        subVolCheckBox.setText("Subvolume");
        subVolCheckBox.setFocusable(false);
        subVolCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        subVolCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                subVolCheckBoxStateChanged(evt);
            }
        });
        subToolBar.add(subVolCheckBox);
        subToolBar.add(jSeparator5);

        jLabel9.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        jLabel9.setText("Origin:  ");
        subToolBar.add(jLabel9);

        originLabel.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        originLabel.setText("0,0,0");
        subToolBar.add(originLabel);
        subToolBar.add(jSeparator6);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        xSlider.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        xSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        xSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xSliderStateChanged(evt);
            }
        });
        jPanel1.add(xSlider);

        ySlider.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        ySlider.setOrientation(javax.swing.JSlider.VERTICAL);
        ySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ySliderStateChanged(evt);
            }
        });
        jPanel1.add(ySlider);

        zSlider.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        zSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        zSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zSliderStateChanged(evt);
            }
        });
        jPanel1.add(zSlider);

        subToolBar.add(jPanel1);

        jLabel13.setText("    ");
        subToolBar.add(jLabel13);

        volToggleButton.setText("Use Subvolume");
        volToggleButton.setFocusable(false);
        volToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        volToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        volToggleButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                volToggleButtonStateChanged(evt);
            }
        });
        volToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volToggleButtonActionPerformed(evt);
            }
        });
        subToolBar.add(volToggleButton);

        add(subToolBar, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void decZButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decZButtonActionPerformed
        xys.adjustZ(-1);
    }//GEN-LAST:event_decZButtonActionPerformed

    private void incZButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_incZButtonActionPerformed
        xys.adjustZ(1);
    }//GEN-LAST:event_incZButtonActionPerformed

    private void decYButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decYButtonActionPerformed
        xzs.adjustY(-1);
    }//GEN-LAST:event_decYButtonActionPerformed

    private void incYButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_incYButtonActionPerformed
        xzs.adjustY(1);
    }//GEN-LAST:event_incYButtonActionPerformed

    private void decXButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decXButtonActionPerformed
        yzs.adjustX(-1);
    }//GEN-LAST:event_decXButtonActionPerformed

    private void incXButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_incXButtonActionPerformed
        yzs.adjustX(1);
    }//GEN-LAST:event_incXButtonActionPerformed

    private void subVolCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_subVolCheckBoxStateChanged
        svr.setDrawing(subVolCheckBox.isSelected());
    }//GEN-LAST:event_subVolCheckBoxStateChanged

    private void xSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xSliderStateChanged
        dx = xSlider.getValue();
        svr.setDx(dx);
    }//GEN-LAST:event_xSliderStateChanged

    private void ySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ySliderStateChanged
        dy = ySlider.getValue();
        svr.setDy(dy);
    }//GEN-LAST:event_ySliderStateChanged

    private void zSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zSliderStateChanged
        dz = zSlider.getValue();
        svr.setDz(dz);
    }//GEN-LAST:event_zSliderStateChanged

    private void volToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volToggleButtonActionPerformed

    }//GEN-LAST:event_volToggleButtonActionPerformed

    private void volToggleButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_volToggleButtonStateChanged
        if (volToggleButton.isSelected()) {
            volToggleButton.setText("Use Whole Volume");
            volume.setSubVolume(ox, oy, oz, dx, dy, dz);
            setPlanes(0,0,0);
            svr.setOrigin(0, 0, 0);
            ox = svr.getOx();
            oy = svr.getOy();
            oz = svr.getOz();
            displayOrigin();

        } else if (!volToggleButton.isSelected()) {
            volToggleButton.setText("Use SubVolume");
            try {
                loadData();
                setMaxes();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }//GEN-LAST:event_volToggleButtonStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel controlPanel1;
    private javax.swing.JPanel controlPanel2;
    private javax.swing.JPanel controlPanel3;
    private javax.swing.JButton decXButton;
    private javax.swing.JButton decYButton;
    private javax.swing.JButton decZButton;
    private javax.swing.JButton incXButton;
    private javax.swing.JButton incYButton;
    private javax.swing.JButton incZButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel originLabel;
    private javax.swing.JToolBar subToolBar;
    private javax.swing.JCheckBox subVolCheckBox;
    private javax.swing.JToggleButton volToggleButton;
    private javax.swing.JLabel xLabel;
    private javax.swing.JSlider xSlider;
    private javax.swing.JPanel xyPanel;
    private javax.swing.JPanel xzPanel;
    private javax.swing.JLabel yLabel;
    private javax.swing.JSlider ySlider;
    private javax.swing.JPanel yzPanel;
    private javax.swing.JLabel zLabel;
    private javax.swing.JSlider zSlider;
    // End of variables declaration//GEN-END:variables

    private void displayOrigin() {
        originLabel.setText("("+Integer.toString(svr.getOx())+","+Integer.toString(svr.getOy())+","+Integer.toString(svr.getOz())+")");
    }


    public static void main(String[] args) {
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            System.out.println("Enter the path to the .nii data.");
            String path = br.readLine();
            System.out.println("Getting NiftiData instance...");
            NiftiData ndata = NiftiData.getMemoryInstance();
            System.out.println("Loading data...");
            ndata.load(path);
            System.out.println("Creating GUI...");
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600,600);
            frame.setLayout(new BorderLayout());
            MriPanel mp = new MriPanel();

            frame.add(mp,BorderLayout.CENTER);
            mp.setNiftiData(ndata);
            frame.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void stateChanged(ChangeEvent e) {
        setPlanes(svr.getOx(), svr.getOy(), svr.getOz());
        displayOrigin();
    }

    public void setX(int x) {
        xLabel.setText(Integer.toString(x));
    }

    public void setY(int y) {
        yLabel.setText(Integer.toString(y));
    }

    public void setZ(int z) {
        zLabel.setText(Integer.toString(z));
    }

    public void setOrigin(int x, int y, int z) {
        setPlanes(x,y,z);
    }
}
