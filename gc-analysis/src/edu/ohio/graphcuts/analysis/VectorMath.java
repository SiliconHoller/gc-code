/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

/**
 * <p>Class to carry out feature-vector math functions.
 * @author David Days <david.c.days@gmail.com>
 */
public class VectorMath {

    private static double PI = Math.PI;

    public VectorMath() {
        //empty constructor--standard.
    }

    /**
     * Calculates the full vector Mahalanobis distance sqared of
     * (fvect-muvect)*(inverse covariance matrix)*(fvect-muvect transpose).
     * @param fvect Feature vector under investigation.
     * @param muvect Average (mu) feature vector for gaussian calculation
     * @param invcov Inverse of the covariance matrix, to be used in calculation.
     * @throws RuntimeException if fvect and muvect are not equal length, or if covariance matrix is
     * not square and of equal dimension to fvect.
     * @return The value of the resulting calculation.
     */
    public double mahalanobisDistanceSquared(double[] fvect, double[] muvect, double[][] invcov) {
        int len = fvect.length;
        if (muvect.length != len) throw new RuntimeException("Feature and Mu vectors unequal.");
        if (invcov.length != len || invcov[0].length != len) throw new RuntimeException("Covariance vector is not square or not of unequal size to feature vector.");
        double[] placeholder = new double[len];
        double[] diffvect = new double[len];
        //create the difference vector
        for (int i=0;i<len;i++) {
            diffvect[i] = fvect[i] - muvect[i];
        }
        //multiply difference vector by covariance.
        double tmp;
        for (int i=0;i<len;i++) {
            tmp = 0.0;
            for (int j=0;j<len;j++) {
                tmp += diffvect[j]*invcov[i][j];
            }
            placeholder[i] = tmp;
        }

        tmp = 0.0;
        for (int i=0;i<len;i++) {
            tmp += placeholder[i]*diffvect[i];
        }
        return tmp;
    }

    public double euclideanDistanceSquared(double[] v1, double[] v2) {
        double val = 0.0;
        int len = v1.length;
        if (v2.length != len) throw new RuntimeException("Vectors not of same length.");
        double[] diff = new double[len];
        for (int i=0;i<len;i++) {
            diff[i] = v1[i]-v2[i];
        }
        for (int j=0;j<len;j++) {
            val += diff[j]*diff[j];
        }
        return val;
    }

    public double manhattanDistance(double[] v1, double[] v2) {
        double val = 0.0;
        int len = v1.length;
        if (v2.length != len) throw new RuntimeException("Vectors not of same length.");
        for (int i=0;i<len;i++) {
            val += Math.abs(v1[i]-v2[i]);
        }
        return val;
    }


    /**
     * Straight-forward calculation of the value (2*PI*sigma^2)^-D/2.
     * @param det Covariance determinant (or sigma^2 value)
     * @return Result of calculation.
     */
    public double normalizingFactor(double det, int dim) {
        double multpi = 2.0*PI;
        double multdet = Math.sqrt(det);
        double tmp = Math.pow(multpi, ((double)dim)/2.0);
        return 1.0/(tmp*multdet);
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
        VectorMath vmath = new VectorMath();
        double[] v1 = {1,2,3,4};
        double[] v2 = {4,3,2,1};
        System.out.println("Manhattan distance is "+vmath.manhattanDistance(v1, v2));
    }

}
