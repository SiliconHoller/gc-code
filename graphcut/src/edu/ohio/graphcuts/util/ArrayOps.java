/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.util;

import java.util.Vector;

/**
 *
 * @author david
 */
public class ArrayOps {

    public static int[] vectorToArray(Vector<Integer> v) {
        int[] array = new int[v.size()];
        for (int i=0;i<array.length;i++) {
            array[i] = v.elementAt(i);
        }
        return array;
    }

    public static int getTotal(int[] array) {
        int count = 0;
        for (int i=0;i<array.length;i++) {
            count = count + array[i];
        }
        return count;
    }

    public static void printArray(int[] array) {
        for (int i=0;i<array.length;i++) {
            System.out.print(array[i]);
            System.out.print(" ");
        }
        System.out.println("");
    }

    public static void printArray(double[] array) {
        for (int i=0;i<array.length;i++) {
            System.out.print(array[i]);
            System.out.print(" ");
        }
        System.out.println("");
    }

    public static double[][] cloneArray(double[][] array) {
        int x = array.length;
        int y = 0;
        double[][] clone = new double[x][0];
        for (int i=0;i<x;i++) {
            y = array[i].length;
            double[] row = new double[y];
            for (int j=0;j<y;j++) {
                row[j] = array[i][j];
            }
            clone[i] = row;
        }

        return clone;
    }

}
