/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.gui.tree;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Convenience methods for handling data in and out of
 * a TreeNode (DefaultMutableTreeNode, in this case).
 * @author david
 */
public class NodeHandler {

    public NodeHandler() {
        //empty constructor for now
    }

    public boolean isSegmented(DefaultMutableTreeNode node) {
        return !node.isLeaf();
    }

    public int getNodeType(DefaultMutableTreeNode node) {
        return ((CutImage)node.getUserObject()).getType();
    }

    public CutImage getCutsImage(DefaultMutableTreeNode node) {
        CutImage ci = null;
        if (node.isLeaf()) return ci;
        Enumeration en = node.children();
        while (en.hasMoreElements()) {
            CutImage curr = (CutImage) ((DefaultMutableTreeNode)en.nextElement()).getUserObject();
            if (curr.getType() == CutImage.CUT_IMAGE) {
                ci = curr;
            }
        }
        return ci;
    }

    public void addCutsTo(Collection<CutImage> cuts, DefaultMutableTreeNode node) {
        Iterator<CutImage> it = cuts.iterator();
        while (it.hasNext()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(it.next(),true);
            node.add(child);
        }
    }

}
