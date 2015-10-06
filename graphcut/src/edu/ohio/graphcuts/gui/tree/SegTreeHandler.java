/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.gui.tree;

import java.io.File;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * Handles tree operations for manipulating tree model and
 * display
 * @author david
 */
public class SegTreeHandler {

    private JTree tree;
    private TreeModel model;

    public SegTreeHandler(JTree tree) {
        this.tree = tree;
        model = tree.getModel();
    }

    public void newRoot(CutImage rootImg) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootImg,true);
        tree.setModel(new DefaultTreeModel(root));
    }

    public CutImage getRootImage() {
        return (CutImage) ((DefaultMutableTreeNode)model.getRoot()).getUserObject();
    }


    public CutImage getSelected() {
        return (CutImage)((DefaultMutableTreeNode)tree.getSelectionModel().getSelectionPath().getLastPathComponent()).getUserObject();
    }

    public DefaultMutableTreeNode getSelectedNode() {
        return (DefaultMutableTreeNode) tree.getSelectionModel().getSelectionPath().getLastPathComponent();
    }

    public Queue<CutImage> queueUpSegmentation() {
        Queue<CutImage> q = new LinkedList<CutImage>();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        Queue<DefaultMutableTreeNode> srchq = new LinkedList<DefaultMutableTreeNode>();
        srchq.offer(root);
        while (!q.isEmpty()) {
            DefaultMutableTreeNode curr = srchq.remove();
            if (curr.isLeaf()) {
                CutImage ci = (CutImage) curr.getUserObject();
                if (ci.getType() != CutImage.CUT_IMAGE) {
                    q.offer(ci);
                }
            } else {
                Enumeration en = curr.children();
                while (en.hasMoreElements()) {
                    srchq.offer((DefaultMutableTreeNode)en.nextElement());
                }
            }
        }

        return q;
    }


}
