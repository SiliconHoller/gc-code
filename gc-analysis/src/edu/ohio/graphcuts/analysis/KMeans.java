/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.analysis;

/**
 *
 * @author david
 */
public interface KMeans {


    public void setEpoch(int epoch);

    public void setEpsilon(double epsilon);

    public int getEpoch();

    public double getEpsilon();

    public int getCount();

    public int[] getClassifications();

    public double[][] getFeatures();

    public double[][] getAverages();

    public int getNumFeatures();

    public boolean analyze();


}
