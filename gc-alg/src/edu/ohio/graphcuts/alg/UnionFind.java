/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.alg;

import java.util.Arrays;
import java.util.Vector;

/**
 * <p>UnionFind implementation from Wikipedia and Cormen's
 * Introduction to Algorithms, pp 570-571, and slides from
 * http://www.cs.princeton.edu/~rs/AlgsDS07/01UnionFind.pdf.</p>
 * <p>This implementation has been geared towards graph-cut methods.  Some performance
 * enhancements have not been implemented (particularly path compression) in order to
 * preserve data integrity, while other methods have been added to give relevant information (
 * such as the path to the root.</p>
 * <p>This form of UnionFind is also biased toward the set of labels given
 * at instantiation.  In other words, if two trees are to be joined,
 * the tree rooted at a label will always adopt the tree that is not a
 * label.  In this way, the structure remains coherent for the purpose
 * of a graph cut.</p>
 *
 * @author David Days
 */
public class UnionFind {

    /**
     * Ultimate roots for this structure--preferred roots of
     * the given trees.
     */
    protected int[] labels;
    /**
     * Array to keep track of the parentage for the given nodes
     */
    protected int[] parents;


    protected int size;

    /**
     * Creates a new UnionFind object, initialized as a set
     * of individual forests (each node is its own parent) and
     * with a tree rank of zero.
     * @param size The number of nodes to initialize
     * @param labels The nodes that are classified as labels
     */
    public UnionFind(int size, int[] labels) {
        this.labels = labels;
        this.size = size;
        initArrays();
    }

    /**
     * <p>Creates new data arrays for the parents and rank.
     * The size of the array is the protected int <b>size</b>
     * passed into the constructor.</p>
     * <p>The new arrays are immediately initialized to the
     * default values for the parents and rank.</p>
     */
    private void initArrays() {
        parents = new int[size];
        resetTrees();
    }

    public void resetArrays() {
        for (int i=0;i<parents.length;i++) {
            parents[i] = i;
        }
        for (int j=0;j<labels.length;j++) {
            parents[labels[j]] = labels[j];
        }
    }

    /**
     * Method scans the parentage represented by the parents[] structure.
     * If the node is not in a label tree, it is reset as belonging in its
     * own tree.
     */
    public void resetTrees() {
        for (int i=0;i<size;i++) {
            if (!isLabel(find(i))) {
                parents[i] = i;
            }
        }
    }


    /**
     * <p>Method to find and return the root of the given node--that is,
     * the tree to which it belongs.</p>
     * <p>Path compression is <em>not</em> used in this method.  At this point, advantage
     * of moving entire subtrees is preserved, with the understanding that
     * the search time will suffer accordingly.</p>
     *
     * @param x The node for which we are finding the root.
     * @return The root node of that tree to which x belongs.
     */
    public int find(int x) {
        if (parents[x] == x) {
            return x;
        } else {
            return find(parents[x]);
        }
    }

    /**
     * Boolean function to test if both given nodes are in the same tree.
     * @param u First node.
     * @param v Second node.
     * @return true if both nodes have the same root; otherwise returns false.
     */
    public boolean find(int u, int v) {
        return find(u) == find(v);
    }

    /**
     * Returns the parent of a given node.
     * @param x
     * @return The node which has this node as a child.
     */
    public int getParent(int x) {
        return parents[x];
    }

    /**
     * Tests to see if the given value/node is one of the labels
     * designated at the initialization of this UnionFind.
     * @param z The int value to be tested
     * @return True if z is a value in the labels array; otherwise, false.
     */
    public boolean isLabel(int z) {
        boolean val = false;
        for (int i=0;i<labels.length;i++) {
            if (labels[i] == z) val = true;
        }
        return val;
    }

    /**
     * <p>Method that attempts to unify the two tree roots given, according to the
     * precedence described below.  If the two trees are not unified, then no alteration
     * of the data arrays is made, and the value of 0 is returned.</p>
     * <p>Precedence of operation:
     * <ul>
     * <li>If both u and v are in the same tree, nothing is done, and a value of
     * zero is returned.</li>
     * <li>If both u and v are in a label's tree, no union is performed, and
     * a value of zero is returned.</li>
     * <li>If u or v is in a label tree, but the other is not, then the label tree
     * node becomes the parent of the non-label tree node.  +1 or -1 is returned.</li>
     * <li>RANKS NOT USED AT THIS TIME:  If neither u nor v are labels, then the shorter tree is attached to the larger tree.
     * +1 or -1 is returned accordingly, and the ranks are adjusted accordingly.</li>
     * </ul>
     * </p>
     * @param u The first node to be considered.
     * @param v The second node to be considered.
     * @return 0 if no union was performed; +1 if u becomes the parent of v, -1 if
     * v becomes the parent of u.
     */
    public int union(int u, int v) {

        int uRoot = find(u);
        int vRoot = find(v);
        boolean uLabel = isLabel(uRoot);
        boolean vLabel = isLabel(vRoot);
        if (uRoot == vRoot) {
            //In the same tree, so no change needed
            return 0;
        }
        if (uLabel && vLabel) {
            //both are roots in a label...do nothing
            return 0;
        } else if (uLabel && !vLabel) {
            //add v to u
            parents[v] = u;
            //if (rank[v] > rank[u]) {
            //    rank[u] = rank[v] + 1;
            //}
            return 1;
        } else if (!uLabel && vLabel) {
            //add u to v
            parents[u] = v;
            //if (rank[u] >= rank[v]) {
            //    rank[v] = rank[u]+1;
            //}
            return -1;
        }


        //default action
        //if (rank[u] >= rank[v]) {
            parents[v] = u;
            //rank[u] = rank[v]+1;
            return 1;
        //} else {
        //    parents[u] = v;
        //    return -1;
        //}
    }

    /**
     * Counts and returns the number of roots in the UnionFind data
     * at the point that this method is called.
     * @return The number of unique roots found when find() is called for
     * every node.
     */
    public int countRoots() {
        int[] carray = new int[size];
        Arrays.fill(carray,0);
        int count = 0;
        int r;
        for (int i=0;i<carray.length;i++) {
            r = find(i);
            carray[r] += 1;
        }
        for (int j=0;j<carray.length;j++) {
            if (carray[j] != 0) count++;
        }
        carray = null;
        return count;
    }

    /**
     * Returns the path from the given child node n to the root node.
     * If the given node <em>is</em> a root, then that elements alone is return.
     * @param n The node from which a path to the tree root will be traced.
     * @return An array (including the node given) tracing the path back to the root.
     */
    public int[] pathToRoot(int n) {
        Vector<Integer> v = new Vector<Integer>();
        int child = n;
        int parent = parents[child];
        while (child != parent) {
            v.add(child);
            child = parent;
            parent = parents[child];
        }
        v.add(child);

        int[] rpath = new int[v.size()];
        for (int i=0;i<rpath.length;i++) {
            rpath[i] = v.elementAt(i);
        }
        return rpath;
    }

    /**
     * Returns a path from the root node to the given node as an
     * array of integers.  The method first calls the pathToRoot() method for the
     * given node, then reverses the array manually.
     * @param n The node to which a path from the root is needed.
     * @return An array in which the first element is the root node and the
     * last element is the given node.
     */
    public int[] pathFromRoot(int n) {
        int[] toRoot = pathToRoot(n);
        int[] rpath = new int[toRoot.length];
        int endpos = toRoot.length - 1;
        for (int i=0;i<rpath.length;i++) {
            rpath[i] = toRoot[endpos-i];
        }
        return rpath;
    }

}
