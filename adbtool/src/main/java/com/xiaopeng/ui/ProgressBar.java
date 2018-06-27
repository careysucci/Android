package com.xiaopeng.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Xpeng on 2018-6-26.
 */
public class ProgressBar {
    private JProgressBar jProgressBar;
    private JPanel jPanel;
    private static final int MINIMUM = 0;
    private static final int MAXIMUM = 100;

    public ProgressBar(JPanel jPanel,JProgressBar jProgressBar) {
        this.jProgressBar = jProgressBar;
        this.jPanel = jPanel;

        this.jProgressBar.setMinimum(MINIMUM);
        this.jProgressBar.setMaximum(MAXIMUM);
        this.jPanel.add(jProgressBar);
    }

    public void update(int value){
        this.jProgressBar.setValue(value);
    }
}
