/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * KMeansPanel.java
 *
 * Created on Aug 8, 2010, 10:47:13 PM
 */

package gcgui.panel;

import edu.ohio.graphcuts.analysis.KMeans;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author david
 */
public class KMeansPanel extends javax.swing.JPanel {

    private KMeans kmeans;
    private Vector<String> columns;
    private Vector<Vector> rowData;

    /** Creates new form KMeansPanel */
    public KMeansPanel() {
        initComponents();
        columns = new Vector<String>();
        columns.add("Datum");
        columns.add("Value");
        clear();
    }

    private void clear() {
        rowData = new Vector<Vector>();
        table.setModel(new DefaultTableModel(rowData,columns));
    }

    public void setKMeans(KMeans kmeans) {
        this.kmeans = kmeans;
    }

    private void display() {
        clear();
        double[][] averages = kmeans.getAverages();
        Integer groups = averages.length;
        addDatum("K",groups);
        Integer epoch = kmeans.getEpoch();
        addDatum("Epoch", epoch);
        Integer count = kmeans.getCount();
        addDatum("Iterations",count);
        //Display the averages
        String prefix = "Group-";
        for (int i=0;i<averages.length;i++) {
            String group = prefix+Integer.toString(i);
            double[] avg = averages[i];
            for (int j=0;j<avg.length;j++) {
                String rname = group + " Feature-"+Integer.toString(j);
                Double val = avg[j];
                addDatum(rname,val);
            }
        }
        table.setModel(new DefaultTableModel(rowData,columns));
    }

    private void addDatum(String datum, Object val) {
        Vector row = new Vector();
        row.add(datum);
        row.add(val);
        rowData.add(row);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(table);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}
