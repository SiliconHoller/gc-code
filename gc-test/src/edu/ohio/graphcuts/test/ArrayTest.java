/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.test;

import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author david
 */
public class ArrayTest {

    public static void main(String[] args) {
        System.out.print("nxn");
        System.out.print("\tStart");
        System.out.print("\tEnd");
        System.out.println("\tElapsed sec");
        Date start;
        Date end;
        int n = 0;
        int[][] narray;
        double[][] darray;
        for (int i=1;i<7;i++) {
            n = i*1000;
            start = new Date();
            narray = new int[n][n];
            darray = new double[n][n];
            for (int m=0;m<n;m++) {
                Arrays.fill(narray[m],-1);
                Arrays.fill(darray[m],-1.0);
            }
            for (int x=0;x<n;x++) {
                for (int y=0;y<n;y++) {
                    darray[x][y] += narray[x][y];
                }
            }
            end = new Date();
            System.out.print(n+"x"+n);
            System.out.print("\t"+start.getTime());
            System.out.print("\t"+end.getTime());
            System.out.println("\t"+((double)(end.getTime() - start.getTime())/1000.0));
        }
    }

}
