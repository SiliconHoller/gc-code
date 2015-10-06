/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.gui.msg;

/**
 *
 * @author david
 */
public interface MessageBoard {

    public static final int ROUTINE = 0;
    public static final int IMPORTANT = 1;
    public static final int SEVERE = 2;

    public void post(String txt);
    public void post(String txt, int severity);

}
