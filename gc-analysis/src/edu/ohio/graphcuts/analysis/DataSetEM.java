/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

import edu.ohio.graphcuts.analysis.features.DataSet;
import edu.ohio.graphcuts.analysis.features.Instance;
import java.util.Arrays;

/**
 * <p>Implementation of the Expectation-Maximization Algorithm
 * for the DataSet class.</p>
 * <p>Given the initial parameters of the DataSet and the original
 * means, this class iteratively performs an Expectation-Maximization
 * refinement of the classifications.</p>
 * <p>When finished, the new mean vectors and covariance matrices are
 * available through the appropriate method calls.</p>
 * @author David Days <david.c.days@gmail.com>
 */
public class DataSetEM {

    /**
     * Number of classes
     */
    protected int classNum;
    /**
     * Mixing coefficient for each class (pi sub-k).  Length
     * is classNum (number of classes).
     */
    protected double[] pik;

    /**
     * New mixing coefficients for each class.
     */
    protected double[] newpik;

    /**
     * The average responsibility for each class.
     */
    protected double[] nk;

    /**
     * Per-instance gamma values (responsibility).  Size is NxclassNum.
     */
    protected double[][] gammak;
    /**
     * Per-instance gaussian-distribution values.  Size is NxclassNum.
     */
    protected double[][] gaussk;

    /**
     * Initial means.  Size is classNum X numFeatures.
     */
    protected double[][] kmeans;

    /**
     * Current (and final) covariance matrices.
     */
    protected double[][][] newcovk;

    /**
     * The old covariance matrices in the calculations
     */
    protected double[][][] oldcovk;

    /**
     * Complete DataSet instance.
     */
    protected DataSet dset;

    /**
     * The current (and final, once complete) means
     * for the various classifications.
     */
    protected double[][] newmeans;

    /**
     * The old means in the calculations
     */
    protected double[][] oldmeans;

    /**
     * Total number of instances.
     */
    protected int N;

    /**
     * VectorMath instance to perform vector and matrix
     * calculations.
     */
    protected VectorMath vmath;

    /**
     * Epsilon for log-likelihood.  This is the stopping parameter
     * for the change in log-likelihood calculation.
     */
    protected double epsilonll = 0.1;

    /**
     * Epsilon for the determinants.  This is the stopping parameter
     * for the change in the determinants.
     */
    protected double epsilondet = 0.1;
    
    /**
     * Iteration limit for the EM algorithm.
     */
    protected int epoch = 100;

    /**
     * Tracking of progress towards epoch.
     */
    protected int count;

    /**
     * Gaussian values calculated from each mean and covariance.
     */
    protected double[][] gaussVals;

    protected Instance[] iset;

    protected double deltall;
    protected double oldll;

    /**
     * Initialize the EM algorithm with the given DataSet.
     * @param dset DataSet to be used.
     */
    public DataSetEM(DataSet dset) {
        this.dset = dset;
        this.N = dset.getData().length;
        vmath = new VectorMath();
    }

    public int getCount() {
        return count;
    }

    public void setDeterminantEpsilon(double epsilondet) {
        this.epsilondet = epsilondet;
    }

    public void setLogLikelihoodEpsilon(double epsilonll) {
        this.epsilonll = epsilonll;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public double[][] getMeans() {
        return oldmeans;
    }

    public double[][][] getCovs() {
        return oldcovk;
    }

    /**
     * Perform an uninitialized Expectation-Maximization algorithm
     * using randomly assigned parameters, resulting in the given
     * number of Gaussian distributions.
     * @param classNum Number of Gaussian classes to use.
     * @return True if the analysis converged in the given limits.  Otherwise,
     * false.
     */
    public boolean analyze(int classNum) {
        boolean val;
        this.classNum = classNum;
        //Randomly init the data
        zeroInit();
        initGammaK();
        initCovk();
        initMeans();
        val = em();
        return val;
    }

    public boolean analyze(double[][] kmeans, double[] pi, double[][][] covk) {
        boolean val = false;
        this.classNum = pi.length;
        initGammaK();
        this.oldmeans = kmeans;
        this.pik = pi;
        this.oldcovk = covk;
        val = em();
        return val;
    }

    protected boolean em() {
        boolean val = false;
        count = 0;
        gaussVals = new double[N][classNum];
        oldll = Double.POSITIVE_INFINITY;
        boolean finished = false;
        while (!finished) {
            fillGauss();
            fillGamma();
            nk = getNk();
            newmeans = getNewMeans();
            newcovk = getNewCovK();
            newpik = getNewPik();
            finished = meetLimits();
            if (!finished) {
                oldmeans = newmeans;
                oldcovk = newcovk;
                pik = newpik;
            }
        }
        return val;
    }

    protected boolean meetLimits() {
        boolean val = false;
        double detdiff = currentDeterminantDiff();
        double newll = currentLogLikelihood();
        val = (detdiff <= this.epsilondet || (oldll - newll) <= epsilonll);
        oldll = newll;
        return val;
    }

    protected double currentLogLikelihood() {
        double val = 0.0;
        double lval;
        double det;
        double[][] invcovk;
        double norm;
        double expVal;
        int numF = dset.getNumFeatures();
        for (int n=0;n<N;n++) {
            lval = 0.0;
            for (int k=0;k<classNum;k++) {
                det = vmath.getDeterminant(newcovk[k]);
                invcovk = vmath.inverse(newcovk[k]);
                if (det == 0) det = 1.0;
                norm = vmath.normalizingFactor(det, numF);
                expVal = vmath.mahalanobisDistanceSquared(iset[k].getVals(), newmeans[k], invcovk);
                lval = newpik[k]*norm*Math.exp(-0.5*expVal);
            }
            if (lval > 0) val += Math.log(lval);
        }
        return val;
    }

    protected double currentDeterminantDiff() {
        double olddet = 0.0;
        double newdet = 0.0;
        for (int i=0;i<classNum;i++) {
            olddet += olddet+vmath.getDeterminant(oldcovk[i]);
            newdet += newdet+vmath.getDeterminant(newcovk[i]);
        }
        return Math.abs(olddet-newdet);
    }

    protected double[][][] getNewCovK() {
        int numF = dset.getNumFeatures();
        double[][][] ncovk = new double[classNum][numF][numF];
        for (int k=0;k<classNum;k++) {
            ncovk[k] = getCovarianceMatrix(k);
        }
        return ncovk;
    }

    protected double getCovariance(int f1, int f2, int k) {
        double cov = 0.0;
        double avg1 = newmeans[k][f1];
        double avg2 = newmeans[k][f2];
        double gk;
        double sum = 0.0;
        for (int i=0;i<N;i++) {
            gk = gammak[i][k];
            sum += gk*(iset[i].getVal(f1) - avg1)*(iset[i].getVal(f2)-avg2);
        }
        return cov;
    }

    protected double[][] getCovarianceMatrix(int k) {
        int features = dset.getNumFeatures();
        double[][] cov = new double[features][features];
        for (int i=0;i<features;i++) {
            for (int j=0;j<features;j++) {
                cov[i][j] = getCovariance(i,j,k)/nk[k];
            }
        }
        return cov;
    }


    protected double[] getNewPik() {
        double[] npik = new double[classNum];
        double n = (double)N;
        for (int i=0;i<npik.length;i++) {
            npik[i] = nk[i]/n;
        }
        return npik;
    }

    protected double[][] getNewMeans() {
        int numF = dset.getNumFeatures();
        double[][] nmeans = new double[classNum][numF];
        double[] fset;
        double gk;
        //Weighted sum of features
        for (int i=0;i<N;i++) {
            fset = iset[i].getVals();
            for (int j=0;j<classNum;j++) {
                gk = gammak[i][j];
                for (int k=0;k<numF;k++) {
                    nmeans[j][k] += gk*fset[k];
                }
            }
        }
        //Divide each sum by Nk
        for (int i=0;i<classNum;i++) {
            for (int j=0;j<numF;j++) {
                nmeans[i][j] = nmeans[i][j]/nk[i];
            }
        }
        return nmeans;
    }

    protected double[] getNk() {
        double[] newnk = new double[classNum];
        for (int i=0;i<N;i++) {
            for (int j=0;j<classNum;j++) {
                newnk[j] += gaussVals[i][j];
            }
        }
        //divide by N
        double n = (double)N;
        for (int k=0;k<nk.length;k++) {
            newnk[k] = newnk[k]/n;
        }
        return newnk;
    }

    protected void fillGamma() {
        double rowsum;
        for (int i=0;i<N;i++) {
            //sum the rows
            rowsum = 0.0;
            for (int j=0;j<classNum;j++) {
                rowsum += pik[j]*gaussVals[i][j];
            }
            //fill the vals
            for (int k=0;k<classNum;k++) {
                gammak[i][k] = pik[k]*gaussVals[i][k]/rowsum;
            }
        }
    }

    protected void fillGauss() {
        int numF = dset.getNumFeatures();
        double det;
        double norm;
        double expVal;
        double[][] invcovk;
        Instance[] is = dset.getData();
        for (int i=0;i<N;i++) {
            for (int j=0;j<classNum;j++) {
                det = vmath.getDeterminant(oldcovk[j]);
                invcovk = vmath.inverse(oldcovk[j]);
                if (det == 0) det = 1.0;
                norm = vmath.normalizingFactor(det, numF);
                expVal = vmath.mahalanobisDistanceSquared(is[i].getVals(), oldmeans[j], invcovk);
                gaussVals[i][j] = norm*Math.exp(-0.5*expVal);
            }
        }
    }

    protected void initGammaK() {
        gammak = new double[N][classNum];
    }

    protected void zeroInit() {
        initPik();
        initCovk();
    }

    /**
     * Initializes pik to an equal distribution.
     */
    protected void initPik() {
        pik = new double[classNum];
        double pval = 1.0/(double)classNum;
        Arrays.fill(pik, pval);
    }

    /**
     * Initializes all covariance matrices to the identity
     * matrix.
     */
    protected void initCovk() {
        int numFeatures = dset.getNumFeatures();
        oldcovk = new double[classNum][0][0];
        for (int k=0;k<classNum;k++) {
            double[][] cov = new double[numFeatures][numFeatures];
            for (int i=0;i<numFeatures;i++) {
                cov[i][i] = 1.0;
            }
            oldcovk[k] = cov;
        }
    }

    /**
     * Initializes the new means values to random Instance measurements
     */
    protected void initMeans() {
        oldmeans = new double[classNum][0];
        for (int i=0;i<classNum;i++) {
            int meanpos = (int)(Math.random()*iset.length);
            oldmeans[i] = iset[meanpos].getVals();
        }
    }



}
