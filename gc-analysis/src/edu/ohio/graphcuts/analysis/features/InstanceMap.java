/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis.features;

import edu.ohio.graphcuts.analysis.Neighbors;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public abstract class InstanceMap {

    protected Vector<Instance> instances;
    protected Vector<Integer> positions;
    protected Neighbors.CoordinateTranslator ct;


    protected InstanceMap(int w, int h) {
        ct = new Neighbors.CoordinateTranslator(w,h);
        instances = new Vector<Instance>();
        positions = new Vector<Integer>();
    }

    protected InstanceMap(int w, int h, int zht) {
        ct = new Neighbors.CoordinateTranslator(w,h,zht);
        instances = new Vector<Instance>();
        positions = new Vector<Integer>();
    }

    public void add(Collection<Instance> c) {
        Iterator<Instance> it = c.iterator();
        while (it.hasNext()) {
            Instance i = it.next();
            int pos = getPosition(i);
            instances.add(i);
            positions.add(pos);
        }
    }

    public void add(Instance i) {
        int pos = getPosition(i);
        instances.add(i);
        positions.add(pos);
    }
    
    public int get(Instance i) {
        if (instances.contains(i)) {
            return positions.elementAt(instances.indexOf(i));
        } else {
            return -1;
        }
    }

    public Instance get(int pos) {
        if (positions.contains(pos)) {
            return instances.elementAt(positions.indexOf(pos));
        }
        return null;
    }

    protected abstract int getPosition(Instance i);

    public static class TwoD extends InstanceMap {

        public TwoD(int w, int h) {
            super(w,h);
        }

        public TwoD(Collection<Instance> c, int w, int h) {
            super(w,h);
            add(c);
        }

        @Override
        protected int getPosition(Instance i) {
            return ct.getPos((int)i.getVal(0), (int)i.getVal(1));
        }


    }

    public static class ThreeD extends InstanceMap {

        public ThreeD(int w, int h, int zht) {
            super(w,h,zht);
        }

        public ThreeD(Collection<Instance> c, int w, int h, int zht) {
            super(w,h,zht);
            add(c);
        }

        @Override
        protected int getPosition(Instance i) {
            return ct.getPos((int)i.getVal(0), (int)i.getVal(1), (int)i.getVal(2));
        }
    }

}
