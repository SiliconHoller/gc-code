/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis.features;

import java.util.Collection;
import java.util.Vector;

/**
 *<p>Contains sets of data instances and performs some basic
 * vector/matrix and statistical calculations on the data.</p>
 * <p>Matrix code based largely on BSD-licensed code from
 * Marcus Kazmierczak from http://mkaz.com/math/linear_algebra/src/MatrixCalculator.java
 * </p>
 * @author David Days <david.c.days@gmail.com>
 */
public class DataSet {

    protected Instance[] dset;
    protected int numFeatures = -1;

    public DataSet(Instance[] dset) {
        this.dset = dset;
        nullClasses();
        numFeatures = dset[0].getNumFeatures();
    }

    public DataSet(Collection<Instance> c) {
        dset = new Instance[0];
        dset = c.toArray(dset);
        nullClasses();
        numFeatures = dset[0].getNumFeatures();
    }

    public void resetClasses() {
        nullClasses();
    }

    private void nullClasses() {
        for (Instance i:dset) {
            i.setClazz(-1);
        }
    }

    public int getNumFeatures() {
        return numFeatures;
    }

    public Instance[] getData() {
        return dset;
    }

    public Collection<Instance> getInstancesOf(int clazz) {
        Vector<Instance> v = new Vector<Instance>();
        for (Instance i : dset) {
            if (i.getClazz() == clazz) v.add(i);
        }

        return v;
    }

    public Collection<Instance> getInstancesWithout(int clazz) {
        Vector<Instance> v = new Vector<Instance>();
        for (Instance i: dset) {
            if (i.getClazz() != clazz) v.add(i);
        }
        return v;
    }

    public int getCount() {
        return dset.length;
    }

    public int getCount(int clazz) {
        int count = 0;
        for (Instance i: dset) {
            if (i.getClazz() == clazz) count++;
        }
        return count;
    }

    public double getAverage(int clazz, int fnum) {
        double avg = 0.0;
        int count = getCount(clazz);

        double sum = 0.0;

        for (Instance i: dset) {
            if (i.getClazz() == clazz) sum += i.getVal(fnum);
        }

        avg = sum/(double)count;

        return avg;
    }

    public double getAverage(int fnum) {
        double avg = 0;
        double count = getCount();
        double sum = 0.0;
        for (Instance i:dset) {
            sum += i.getVal(fnum);
        }
        avg = sum/count;
        return avg;
    }

    public double[] getMean() {
        double[] mean = new double[getNumFeatures()];
        double count = getCount();
        if (count == 0) return mean;
        for (int i=0;i<mean.length;i++) {
            mean[i] = getAverage(i);
        }
        return mean;
    }

    public double[] getClassAverage(int clazz) {
        double[] avg = new double[getNumFeatures()];
        double count = getCount(clazz);
        if (count == 0) return avg;
        double[] sum = new double[avg.length];
        double[] vals;
        for (Instance i:dset) {
            if (i.getClazz() == clazz) {
                vals = i.getVals();
                for (int j=0;j<sum.length;j++) {
                    sum[j] += vals[j];
                }
            }

        }
        for (int k=0;k<sum.length;k++) {
            avg[k] = sum[k]/count;
        }
        return avg;
    }

    public double getVariance(int clazz, int fnum) {
        double avg = getAverage(clazz, fnum);
        double var = 0.0;
        
        double sum = 0.0;
        double diff = 0;
        for (Instance i:dset) {
            if (i.getClazz() == clazz) {
                diff = i.getVal(fnum) - avg;
                sum += (diff*diff);
            }
        }

        var = sum/(double)getCount(clazz);
        return var;
    }

    public double getVariance(int fnum) {
        double avg = getAverage(fnum);
        double var = 0.0;
        double sum = 0.0;
        double diff;
        for (Instance i:dset) {
            diff = i.getVal(fnum) - avg;
            sum += diff*diff;
        }
        var = sum/(double)getCount();
        return var;
    }

    public double getMax(int clazz, int fnum) {
        double max = 0.0;
        for (Instance i : dset) {
            if (i.getClazz() == clazz) {
                if (i.getVal(fnum) > max) max = i.getVal(fnum);
            }
        }
        return max;
    }

    public double getMax(int fnum) {
        double max = 0.0;
        for (Instance i:dset) {
            if (i.getVal(fnum) > max) max = i.getVal(fnum);
        }
        return max;
    }

    public int[] getHistogram(int clazz, int fnum) {
        int max = (int) getMax(clazz, fnum);
        int[] hist = new int[max+1];
        for (Instance i: dset) {
            if (i.getClazz() == clazz) hist[(int)i.getVal(fnum)]++;
        }
        return hist;
    }

    public int[] getHistogram(int fnum) {
        int max = (int) getMax(fnum);
        int[] hist = new int[max+1];
        for (Instance i:dset) {
            hist[(int)i.getVal(fnum)]++;
        }
        return hist;
    }

    public double[] getPDist(int clazz, int fnum) {
        double count = getCount(clazz);
        int[] hist = getHistogram(clazz, fnum);
        double[] pdist = new double[hist.length];
        for (int i=0;i<pdist.length;i++) {
            pdist[i] = ((double)hist[i])/count;
        }
        return pdist;
    }

    public double[] getPDist(int fnum) {
        double count = getCount();
        int[] hist = getHistogram(fnum);
        double[] pdist = new double[hist.length];
        for (int i=0;i<pdist.length;i++) {
            pdist[i] = ((double)hist[i])/count;
        }
        return pdist;
    }

    public double getDiscreteAverage(int clazz, int fnum) {
        double avg = 0.0;

        double[] pdist = getPDist(clazz, fnum);

        for (Instance i : dset) {
            int fint = (int)i.getVal(fnum);
            avg += pdist[fint]*i.getVal(fnum);
        }

        return avg;
    }

    public double getDiscreteAverage(int fnum) {
        double avg = 0.0;
        double[] pdist = getPDist(fnum);
        for (Instance i: dset) {
            int fint = (int)i.getVal(fnum);
            avg += pdist[fint]*i.getVal(fnum);
        }
        return avg;
    }

    public double getDiscreteVariance(int clazz, int fnum) {
        double davg = getDiscreteAverage(clazz, fnum);
        double dvar = 0.0;
        double[] pdist = getPDist(clazz,fnum);
        double diff;
        for (Instance i: dset) {
            int fint = (int)i.getVal(fnum);
            diff = davg - i.getVal(fnum);
            dvar += (diff*diff)*pdist[fint];
        }

        return dvar;

    }

    public double getDiscreteVariance(int fnum) {
        double davg = getDiscreteAverage(fnum);
        double dvar = 0.0;
        double[] pdist = getPDist(fnum);
        double diff;
        for (Instance i:dset) {
            int fint = (int)i.getVal(fnum);
            diff = davg - i.getVal(fnum);
            dvar += (diff*diff)*pdist[fint];
        }
        return dvar;
    }

    public double getEntropy(double[] pdist) {
        double ent = 0.0;
        double log2 = Math.log(2.0);
        for (int i=0;i<pdist.length;i++) {
            if (pdist[i] != 0) {
                ent += pdist[i]*Math.log(pdist[i])/log2;
            }
        }
        return -1.0*ent;
    }

    public double getEntropy(int clazz, int fnum) {
        double[] pdist = getPDist(clazz,fnum);
        return getEntropy(pdist);
    }

    public double getEntropy(int fnum) {
        double[] pdist = getPDist(fnum);
        return getEntropy(pdist);
    }

    public DataSet getSet(int clazz) {
        Vector<Instance> v = new Vector<Instance>();
        for (Instance i:dset) {
            if (i.getClazz() == clazz) v.add(i);
        }
        return new DataSet(v);
    }

    public DataSet getSetWithout(int clazz) {
        Vector<Instance> v = new Vector<Instance>();
        for (Instance i:dset) {
            if (i.getClazz() != clazz) v.add(i);
        }
        return new DataSet(v);
    }

    public double getCovariance(int f1, int f2) {
        double cov = 0.0;
        double avg1 = getAverage(f1);
        double avg2 = getAverage(f2);
        double sum = 0.0;
        for (Instance i:dset) {
            sum += (i.getVal(f1) - avg1)*(i.getVal(f2)-avg2);
        }
        cov = sum/(double)getCount();
        return cov;
    }

    public double getCovariance(int clazz, int f1, int f2) {
        double cov = 0.0;
        double count = getCount(clazz);
        if (count == 0) return cov;
        double avg1 = getAverage(clazz,f1);
        double avg2 = getAverage(clazz,f2);
        double sum = 0.0;
        for (Instance i: dset) {
            if (i.getClazz() == clazz) {
                sum += (i.getVal(f1)-avg1)*(i.getVal(f2)-avg2);
            }
        }
        cov = sum/(double)getCount(clazz);
        return cov;
    }

    public double[][] getCovarianceMatrix() {
        int features = this.getNumFeatures();
        double[][] cov = new double[features][features];
        for (int i=0;i<features;i++) {
            for (int j=0;j<features;j++) {
                cov[i][j] = getCovariance(i,j);
            }
        }
        //remove singularities--diagonal values that equal 0.0
        singularityCheck(cov);
        //diagonalize(cov);
        return cov;
    }

    public double[][] getCovarianceMatrix(int clazz) {
        int features = getNumFeatures();
        double[][] cov = new double[features][features];
        for (int i=0;i<features;i++) {
            for (int j=0;j<features;j++) {
                cov[i][j] = getCovariance(clazz,i,j);
            }
        }
        //remove singularities--diagonal values that equal 0.0
        singularityCheck(cov);
        //diagonalize(cov);
        return cov;
    }

    /**
     * <p>Singularities (diagonal values that equal 0.0) can really
     * flub up the entire calculation process.  So, we run through
     * those values and assign 1.0.</p>
     * <p>Mathematically, this is pretty inexcusable.  However, since
     * the poor Turing machine isn't smart enough to handle such situations
     * down the line without huge programmer and error-checking overhead, the
     * assumption is made that such a small variance will have a negligible
     * effect on the final calculation.</p>
     * <p>In fact, because 32-bit machines can't normally handle values smaller
     * than 2^-31, then we often come up with a situation where the calculated
     * results are 0.0 when there should actually <em>be</em> a real number.</p>
     * <p>(And yes, 64-bit machines have this limitation...different, but still
     * the same result.)</p>
     * <p>With this in mind, the real effect of changing a variance to such a small
     * number should have negligible effect on the results.  But it shouldn't be
     * forgotten, either.</p>
     * @param cov Double array whose diagonal values should be altered to 1.0 if 0.0
     */
    protected void singularityCheck(double[][] cov) {
        for (int i=0;i<cov.length;i++) {
            if (cov[i][i] == 0.0) cov[i][i] = 1.0;
        }
    }

    public double getDeterminant() {
        double[][] cov = getCovarianceMatrix();
        return getDeterminant(cov);
    }

    public double getDeterminant(int clazz) {
        double[][] cov = getCovarianceMatrix(clazz);
        return getDeterminant(cov);
    }

    public double getDeterminant(double[][] cov) {
        double det = 0.0;
        if (cov.length == 1) {
            det = cov[0][0];
        } else if (cov.length == 2) {
            det = cov[0][0]*cov[1][1]-cov[0][1]*cov[1][0];
        } else {
            for (int i=0;i<cov.length;i++) {
                double[][] temp = new double[cov.length-1][cov.length-1];
                for (int j=1;j<cov.length;j++) {
                    System.arraycopy(cov[j],0,temp[j-1],0,i);
                    System.arraycopy(cov[j],i+1,temp[j-1],i,cov.length-i-1);
                }
                det += cov[0][i]*Math.pow(-1,i)*getDeterminant(temp);
            }
        }
        return det;
    }

    public double[][] transpose(double[][] a) {
        double[][] t = new double[a.length][a.length];
        for (int i=0;i<t.length;i++) {
            for (int j=0;j<t.length;j++) {
                t[j][i] = a[i][j];
            }
        }
        return t;
    }

    public double[][] adjoint(double[][] a) {
        int len = a.length;
        double[][] m = new double[len][len];
        int ii,jj,ia,ja;
        double det;
        for (int i=0;i<len;i++) {
            for (int j=0;j<len;j++) {
                ia = ja = 0;
                double[][] ap = new double[len-1][len-1];
                for (ii=0;ii<len;ii++) {
                    for (jj=0;jj<len;jj++) {
                        if ((ii != i) && (jj != j)) {
                            ap[ia][ja] = a[ii][jj];
                            ja++;
                        }
                    }
                    if ((ii != i) && (jj != j)) {
                        ia++;
                    }
                    ja = 0;
                }
                det = getDeterminant(ap);
                m[i][j] = Math.pow(-1,i+j)*det;
            }
        }
        m = transpose(m);
        return m;
    }

    public double[][] inverse(double[][] a) {
        int len = a.length;
        double[][] m = new double[len][len];
        double[][] mm = adjoint(a);
        double det = getDeterminant(a);
        double dd = 1.0/det;
        for (int i=0;i<len;i++) {
            for (int j=0;j<len;j++) {
                m[i][j] = dd*mm[i][j];
            }
        }
        return m;
    }


    
    public static void main(String[] args) {
        Instance in = new Instance.GrayPixel();
        Instance[] s = {in};
        DataSet ds = new DataSet(s);
        System.out.println("Testing determinant calculation.");
        double[][] matrix = {{3,0,0,0,0},{2,-6,0,0,0},{17,14,2,0,0},{22,-2,15,8,0},{43,12,1,-1,5}};
        System.out.println("Calculating inverse...");
        double[][] inverse = ds.inverse(matrix);
        for (int i=0;i<inverse.length;i++) {
            for (int j=0;j<inverse.length;j++) {
                System.out.print(inverse[i][j]+" ");
            }
            System.out.println();
        }
    }

}
