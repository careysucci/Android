package com.xiaopeng.utils;

import javax.swing.*;
import java.util.LinkedList;

/**
 * Created by Xpeng on 2018-6-26.
 */
public class UpdateAdbProgressBar{
    private LinkedList<Integer> percentQueue = new LinkedList<>();
    private JProgressBar tmpProgressBar = null;
    private static int value = 0;

    public UpdateAdbProgressBar(JProgressBar tmpProgressBar) {
        this.tmpProgressBar = tmpProgressBar;
        this.tmpProgressBar.setMinimum(0);
        this.tmpProgressBar.setMaximum(100);
    }

    public synchronized void updateProgress(){
        if (percentQueue.size() <= 0){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int value = percentQueue.removeFirst();
        tmpProgressBar.setValue(value);
        tmpProgressBar.setString(String.valueOf(value) + "%");
        this.notifyAll();
    }

    public synchronized void getProgress(int value){
        if (percentQueue.size() > 0){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        percentQueue.addLast(value);
        this.notifyAll();
    }

    public void setValue(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
