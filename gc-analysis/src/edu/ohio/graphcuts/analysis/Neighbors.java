/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

/**
 *
 * @author David Days <david.c.days@gmail.com>
 */
public class Neighbors {

    private VectorMath vmath;
    public Neighbors() {
        vmath = new VectorMath();
    }

    public Collection<Integer> getVonNeumannNeighbors(int pos, int w, int h, int radius) {
        Vector<Integer> v = new Vector<Integer>();
        CoordinateTranslator ct = new CoordinateTranslator(w,h);
        int ox = ct.getX(pos);
        int oy = ct.getY(pos);
        int min = -radius;
        int max = radius+1;
        int npos;
        int nx;
        int ny;
        double[] nvect = new double[2];
        double[] ovect = {ox,oy};
        for (int i=min;i<max;i++) {
            nx = i+ox;
            for (int j=min;j<max;j++) {
                ny = i+oy;
                if (ct.inSpace(nx,ny)) {
                    npos = ct.getPos(nx, ny);
                    if (npos != pos) {
                        nvect[0] = nx;
                        nvect[1] = ny;
                        if (vmath.manhattanDistance(nvect, ovect)<= radius) v.add(npos);
                    }
                }
            }
        }
        return v;
    }

    public Collection<Integer> getVonNeumannNeighbors(int pos, int w, int h, int zht, int radius) {
        Vector<Integer> v = new Vector<Integer>();
        CoordinateTranslator ct = new CoordinateTranslator(w,h,zht);
        int ox = ct.getX(pos);
        int oy = ct.getY(pos);
        int oz = ct.getZ(pos);
        int min = -radius;
        int max = radius+1;
        int npos;
        int nx;
        int ny;
        int nz;
        double[] nvect = new double[3];
        double[] ovect = {ox,oy,oz};
        for (int i=min;i<max;i++) {
            nx = i+ox;
            for (int j=min;j<max;j++) {
                ny = i+oy;
                for (int k=min;k<max;k++) {
                    nz = k+oz;
                    if (ct.inSpace(nx,ny,nz)) {
                        npos = ct.getPos(nx, ny, nz);
                        if (npos != pos) {
                            nvect[0] = nx;
                            nvect[1] = ny;
                            nvect[2] = nz;
                            if (vmath.manhattanDistance(nvect, ovect)<= radius) v.add(npos);
                        }
                    }
                }
            }
        }
        return v;
    }

    public Collection<Integer> getCubicNeighbors(int pos, int w, int h, int radius) {
        Vector<Integer> v = new Vector<Integer>();
        CoordinateTranslator ct = new CoordinateTranslator(w,h);
        int ox = ct.getX(pos);
        int oy = ct.getY(pos);
        int min = -radius;
        int max = radius+1;
        int npos;
        int nx;
        int ny;
        for (int i=min;i<max;i++) {
            nx = i+ox;
            for (int j=min;j<max;j++) {
                ny = i+oy;
                if (ct.inSpace(nx,ny)) {
                    npos = ct.getPos(nx, ny);
                    if (npos != pos) {
                        v.add(npos);
                    }
                }
            }
        }
        return v;
    }

    public Collection<Integer> getCubicNeighbors(int pos, int w, int h, int zht, int radius) {
        Vector<Integer> v = new Vector<Integer>();
        CoordinateTranslator ct = new CoordinateTranslator(w,h,zht);
        int ox = ct.getX(pos);
        int oy = ct.getY(pos);
        int oz = ct.getZ(pos);
        int min = -radius;
        int max = radius+1;
        int npos;
        int nx;
        int ny;
        int nz;
        for (int i=min;i<max;i++) {
            nx = i+ox;
            for (int j=min;j<max;j++) {
                ny = i+oy;
                for (int k=min;k<max;k++) {
                    nz = k+oz;
                    if (ct.inSpace(nx,ny,nz)) {
                        npos = ct.getPos(nx, ny, nz);
                        if (npos != pos) {
                            v.add(npos);
                        }
                    }
                }
            }
        }
        return v;
    }

    public LinkedList<Integer>[] getVonNeumannNeighborListing(int w, int h, int radius) {
        int dmax = w*h;
        LinkedList<Integer>[] nlist = (LinkedList<Integer>[])Array.newInstance(LinkedList.class, dmax);
        CoordinateTranslator ct = new CoordinateTranslator(w,h);
        double[] nvect = new double[2];
        double[] ovect = new double[2];
        int min = -radius;
        int max = radius+1;
        double dubradius = radius;
        int npos;
        int nx;
        int ny;
        for (int pos=0;pos<dmax;pos++) {
            LinkedList<Integer> ns = new LinkedList<Integer>();
            int ox = ct.getX(pos);
            int oy = ct.getY(pos);
            ovect[0] = ox;
            ovect[1] = oy;
            for (int i=min;i<max;i++) {
                nx = i+ox;
                for (int j=min;j<max;j++) {
                    ny = i+oy;
                    if (ct.inSpace(nx,ny)) {
                        npos = ct.getPos(nx, ny);
                        if (npos != pos) {
                            nvect[0] = nx;
                            nvect[1] = ny;
                            if (vmath.manhattanDistance(nvect, ovect) <= dubradius) ns.add(npos);
                        }
                    }
                }
            }
            nlist[pos] = ns;
        }//End array iteration
        return nlist;
    }

    public LinkedList<Integer>[] getVonNeumannNeighborListing(int w, int h, int zht, int radius) {
        int dmax = w*h*zht;
        LinkedList<Integer>[] nlist = (LinkedList<Integer>[])Array.newInstance(LinkedList.class, dmax);
        CoordinateTranslator ct = new CoordinateTranslator(w,h,zht);
        double[] nvect = new double[3];
        double[] ovect = new double[3];
        int min = -radius;
        int max = radius+1;
        int npos;
        int nx;
        int ny;
        int nz;
        for (int pos=0;pos<dmax;pos++) {
            LinkedList<Integer> ns = new LinkedList<Integer>();
            int ox = ct.getX(pos);
            int oy = ct.getY(pos);
            int oz = ct.getZ(pos);
            ovect[0] = ox;
            ovect[1] = oy;
            ovect[2] = oz;
            for (int i=min;i<max;i++) {
                nx = i+ox;
                for (int j=min;j<max;j++) {
                    ny = i+oy;
                    for (int k=min;k<max;k++) {
                        nz = k+oz;
                        if (ct.inSpace(nx,ny,nz)) {
                            npos = ct.getPos(nx, ny, nz);
                            if (npos != pos) {
                                nvect[0] = nx;
                                nvect[1] = ny;
                                nvect[2] = nz;
                                if (vmath.manhattanDistance(nvect, ovect)<= radius) ns.add(npos);
                            }
                        }
                    }
                }
            }
            nlist[pos] = ns;
        }//End array iteration
        return nlist;
    }

    public int[][] getVonNeumannNeighborArray(int w, int h, int radius) {
        int dmax = w*h;
        int[][] nlist = new int[dmax][0];
        CoordinateTranslator ct = new CoordinateTranslator(w,h);
        double[] nvect = new double[2];
        double[] ovect = new double[2];
        int min = -radius;
        int max = radius+1;
        int npos;
        int nx;
        int ny;
        for (int pos=0;pos<dmax;pos++) {
            Vector<Integer> ns = new Vector<Integer>();
            int ox = ct.getX(pos);
            int oy = ct.getY(pos);
            ovect[0] = ox;
            ovect[1] = oy;
            for (int i=min;i<max;i++) {
                nx = i+ox;
                for (int j=min;j<max;j++) {
                    ny = i+oy;
                    if (ct.inSpace(nx,ny)) {
                        npos = ct.getPos(nx, ny);
                        if (npos != pos) {
                            nvect[0] = nx;
                            nvect[1] = ny;
                            if (vmath.manhattanDistance(nvect, ovect)<= radius) ns.add(npos);
                        }
                    }
                }
            }
            int[] narray = new int[ns.size()];
            for (int i=0;i<narray.length;i++) {
                narray[i] = ns.get(i);
            }
            nlist[pos] = narray;
        }//End array iteration
        return nlist;
    }

    public int[][] getVonNeumannNeighborArray(int w, int h, int zht, int radius) {
        int dmax = w*h*zht;
        int[][] nlist = new int[dmax][0];
        CoordinateTranslator ct = new CoordinateTranslator(w,h,zht);
        double[] nvect = new double[3];
        double[] ovect = new double[3];
        int min = -radius;
        int max = radius+1;
        int npos;
        int nx;
        int ny;
        int nz;
        for (int pos=0;pos<dmax;pos++) {
            Vector<Integer> ns = new Vector<Integer>();
            int ox = ct.getX(pos);
            int oy = ct.getY(pos);
            int oz = ct.getZ(pos);
            ovect[0] = ox;
            ovect[1] = oy;
            ovect[2] = oz;
            for (int i=min;i<max;i++) {
                nx = i+ox;
                for (int j=min;j<max;j++) {
                    ny = i+oy;
                    for (int k=min;k<max;k++) {
                        nz = k+oz;
                        if (ct.inSpace(nx,ny,nz)) {
                            npos = ct.getPos(nx, ny, nz);
                            if (npos != pos) {
                                nvect[0] = nx;
                                nvect[1] = ny;
                                nvect[2] = nz;
                                if (vmath.manhattanDistance(nvect, ovect)<= radius) ns.add(npos);
                            }
                        }
                    }
                }
            }
            int[] narray = new int[ns.size()];
            for (int i=0;i<narray.length;i++) {
                narray[i] = ns.get(i);
            }
            nlist[pos] = narray;
        }//End array iteration
        return nlist;
    }

    public static class CoordinateTranslator {
        int w;
        int h;
        int zht;
        int xyMax;
        int xyzMax;

        public CoordinateTranslator(int w, int h, int zht) {
            this.w = w;
            this.h = h;
            this.zht = zht;
            xyMax = w*h;
            xyzMax = w*h*zht;
        }

        public CoordinateTranslator(int w, int h) {
            this.w = w;
            this.h = h;
            this.zht = 0;
            xyMax = w*h;
            xyzMax = xyMax;
        }

        public int getX(int pos) {
            return pos%w;
        }

        public int getY(int pos) {
            return (pos/w)%h;
        }

        public int getZ(int pos) {
            return pos/(w*h);
        }

        public int getPos(int x, int y, int z) {
            return (w*h*z+w*y+x);
        }

        public int getPos(int x, int y) {
            return w*y+x;
        }

        public boolean inSpace(int x, int y) {
            if (x < 0) return false;
            if (y < 0) return false;
            return getPos(x,y) < xyMax;
        }

        public boolean inSpace(int x, int y, int z) {
            if (x < 0) return false;
            if (y < 0) return false;
            if (z < 0) return false;
            return getPos(x,y,z) < xyzMax;
        }

        public boolean inSpace(int pos) {
            return pos < xyzMax;
        }
    }

    public static void main(String[] args) {
        //test code for speed
        int w = 64;
        int h = 64;
        int z = 64;
        int radius = 1;
        long start;
        long end;
        LinkedList<Integer>[] ledges;
        int[][] intedges;
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        Neighbors n = new Neighbors();
        start = bean.getCurrentThreadUserTime();
        ledges = n.getVonNeumannNeighborListing(w, h, radius);
        end = bean.getCurrentThreadUserTime();
        System.out.println("LinkedList<>[]\t"+w+"x"+h+"\tis\t"+(end-start));
        start = bean.getCurrentThreadUserTime();
        intedges = n.getVonNeumannNeighborArray(w,h,radius);
        end = bean.getCurrentThreadUserTime();
        System.out.println("int[][] array \t"+w+"x"+h+"\tis\t"+(end-start));
        start = bean.getCurrentThreadUserTime();
        ledges = n.getVonNeumannNeighborListing(w, h, z, radius);
        end = bean.getCurrentThreadUserTime();
        System.out.println("LinkedList<>[]\t"+w+"x"+h+"x"+z+"\t"+(end-start));
        start = bean.getCurrentThreadUserTime();
        intedges = n.getVonNeumannNeighborArray(w, h, z, radius);
        end = bean.getCurrentThreadUserTime();
        System.out.println("int[][] array \t"+w+"x"+h+"x"+z+"\t"+(end-start));
        System.out.println(intedges.length);
        System.out.println(ledges.length);
    }

}
