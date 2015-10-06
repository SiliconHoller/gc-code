/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ohio.graphcuts.gui.msg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Queue;
import java.util.Vector;

/**
 *
 * @author david
 */
public class StatusMessage {

    protected static StatusMessage instance;
    protected Vector<MessageBoard> boards;

    public StatusMessage() {
        instance = this;
        boards = new Vector<MessageBoard>();
    }

    public static StatusMessage getInstance() {
        if (instance == null) {
            instance = new StatusMessage();
        }
        return instance;
    }

    public void addBoard(MessageBoard board) {
        boards.add(board);
    }

    public void removeBoard(MessageBoard board) {
        boards.remove(board);
    }

    public void post(String txt) {
        Iterator<MessageBoard> it = boards.iterator();
        while (it.hasNext()) {
            it.next().post(txt);
        }
    }

    public void post(String txt, int severity) {
        Iterator<MessageBoard> it = boards.iterator();
        while (it.hasNext()) {
            it.next().post(txt,severity);
        }
    }


}
