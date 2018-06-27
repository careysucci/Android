package com.xiaopeng.ui;


import com.xiaopeng.adb.Adb;
import com.xiaopeng.utils.UpdateAdbProgressBar;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

public class MainGUI {

    private JPanel panel2;
    private JTextField srcPath;                     // 源文件目录
    private JButton btnSrcSelect;                      // 选择源文件
    private JButton btnExecute;                  // 一键安装
    private JComboBox destApkNameSelect;            // 目标apk安装名 -- 自动提供选择
    private JTextField destApkNameManual;           // 目标apk安装名 -- 手工输入
    private JPanel panel1;
    private JTextArea textArea1;
    private JButton btnRefresh;
    private JRadioButton radoOneKeyInstall;
    private JRadioButton radoInstallRom;
    private JButton btnClearTxtLog;
    private JButton btnPullLog;
    private JButton btnClearAdbLog;
    private ButtonGroup radioGroup;
    private JProgressBar progressBarAdb;
    private JPanel jpanel;

    // 提示msg
    private String apkNameInfo = "输入apk被安装到系统中的名称，包含后缀";

    // Adb 实例
    private Adb adbObj = new Adb();
    private UpdateAdbProgressBar updateAdbProgressBar;

    //
    private static final int EXEC_ONEKEY_INSTALL_APK = 0,EXEC_ONEKEY_FLASH_ROM = 1;


    public MainGUI() {
        radioGroup = new ButtonGroup();
        radioGroup.add(radoOneKeyInstall);
        radioGroup.add(radoInstallRom);
        radoOneKeyInstall.setSelected(true);

        progressBarAdb.setStringPainted(true);
        updateAdbProgressBar = new UpdateAdbProgressBar(progressBarAdb);

        textArea1.setLineWrap(true);            // 激活换行
        textArea1.setWrapStyleWord(true);       // 换行不断字

        outputUI();

        srcPath.setTransferHandler(new TransferHandler(){
            @Override
            public boolean importData(JComponent comp, Transferable t) {
                try{
                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
                    String filepath = o.toString();

                    if (filepath.startsWith("[")){
                        filepath = filepath.substring(1);
                    }
                    if (filepath.endsWith("]")){
                        filepath = filepath.substring(0,filepath.length() - 1);
                    }
                    if (!filepath.isEmpty()){
                        srcPath.setText(filepath);
                    }
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
                for (int i=0;i<transferFlavors.length;i++){
                    if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])){
                        return true;
                    }
                }
                return false;
            }

        });

        btnSrcSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser("apk");
            }
        });

        destApkNameSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reSetDestApkNameTxt();
            }
        });

        destApkNameManual.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (destApkNameManual.getText().equals(apkNameInfo)){
                    destApkNameManual.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (destApkNameManual.getText().equals("")){
                    destApkNameManual.setText(apkNameInfo);
                }
            }
        });

        btnExecute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int type = getExecType();
                switch (type){
                    case EXEC_ONEKEY_INSTALL_APK:
                        oneKeyInstallApk();
                        break;
                    case EXEC_ONEKEY_FLASH_ROM:
                        showMessage("功能暂未开放");
                        break;
                }
            }
        });

        destApkNameSelect.setModel(getInstalledApk());  // 更新下拉选择框内容
        destApkNameManual.setText(apkNameInfo);

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                destApkNameSelect.setModel(getInstalledApk());  // 更新下拉选择框内容
                reSetDestApkNameTxt();
            }
        });

        btnClearAdbLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Adb.clearAdbLog();
            }
        });

        btnPullLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Adb.pullLogcatLog(updateAdbProgressBar);

                Thread thread = new Thread(new MyProgressBar(updateAdbProgressBar));
                thread.start();
            }
        });

        btnClearTxtLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea1.setText("");
            }
        });

    }

    public void fileChooser(String... type){
        JFileChooser fileChooser = new JFileChooser(srcPath.getText());

        if (!isEmpty(type)){
            StringBuilder suffix = new StringBuilder();
            for (String s : type){
                suffix.append("." + s);
            }

            // 过滤文件类型
            FileNameExtensionFilter filter = new FileNameExtensionFilter(suffix.toString(),type);
            fileChooser.setFileFilter(filter);
        }else{
            // 选择文件夹
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        // 手工选择保存文件
        int returnVal = fileChooser.showOpenDialog(new JPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION){
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            srcPath.setText(path);
        }
    }

    private boolean isEmpty(String str){
        return str == null || "".equals(str);
    }

    private boolean isEmpty(String... strs){
        return strs == null || strs.length == 0 || isEmpty(strs[0]);
    }

    private void showMessage(String msg){
        JOptionPane.showMessageDialog(null, msg,"提示",JOptionPane.PLAIN_MESSAGE);
    }

    private int getExecType(){
        if (radoOneKeyInstall.isSelected())
            return EXEC_ONEKEY_INSTALL_APK;
        else if (radoInstallRom.isSelected())
            return EXEC_ONEKEY_FLASH_ROM;
        else
            return EXEC_ONEKEY_INSTALL_APK;
    }

    private void oneKeyInstallApk(){
        if (isEmpty(srcPath.getText())){
            showMessage("请选择源文件！");
            return;
        }

        String dest = "";
        String src = srcPath.getText();
        String destApkNameSelectedTxt = destApkNameSelect.getSelectedItem().toString();
        if (!isEmpty(destApkNameSelectedTxt) && !destApkNameSelectedTxt.equalsIgnoreCase("none")){
            dest = destApkNameSelectedTxt;
        }else {
            dest = destApkNameManual.getText();
        }
        Adb.oneKeyInstall(src,convertApkNameToCompletePath(dest));
    }

    private ComboBoxModel<String> generateComboBoxModel(String... items){
        DefaultComboBoxModel<String> selectedItems = new DefaultComboBoxModel<>();
        for (String item : items){
            selectedItems.addElement(item);
        }
        return selectedItems;
    }

    private ComboBoxModel<String> getInstalledApk(){
        List<String> apkItems = Adb.getInstalledApp();
        apkItems.add(0,"None");
        return generateComboBoxModel(apkItems.toArray(new String[0]));
    }

    private void reSetDestApkNameTxt(){
        // 系统无目标apk时，提供手工输入apk名
        String destApkNameSelectedTxt = (String) destApkNameSelect.getSelectedItem();
        if (isEmpty(destApkNameSelectedTxt) || destApkNameSelectedTxt.equalsIgnoreCase("none")){
            // 激活手工输入apk名
            destApkNameManual.setEnabled(true);
            destApkNameManual.setEditable(true);
            destApkNameManual.addFocusListener(new JTextFieldListener(apkNameInfo,destApkNameManual));
        }else{
            destApkNameManual.addFocusListener(new JTextFieldListener("",destApkNameManual));
            destApkNameManual.setEnabled(false);
            destApkNameManual.setEditable(false);
        }
    }

    protected void outputUI(){
        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                textArea1.append(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b) throws IOException {
                textArea1.append(new String(b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                textArea1.append(new String(b,off,len));
            }
        };

        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);
        System.setErr(printStream);
    }

    private String convertApkNameToCompletePath(String apkName){
        String[] temp = apkName.split(" - ");
        if (temp.length >1){
            return Paths.get(temp[1].trim(),temp[0].trim()).toString();
        }
        return apkName;
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Adb便捷工具");
        JPanel rootPanel = new MainGUI().panel1;
        jFrame.setContentPane(rootPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setSize(900,900);
        jFrame.setResizable(false);
        jFrame.setLocationRelativeTo(rootPanel);
        jFrame.setVisible(true);
    }
}

class JTextFieldListener implements FocusListener {
    private String info;
    private JTextField jtf;

    public JTextFieldListener(String info, JTextField jtf) {
        this.info = info;
        this.jtf = jtf;
    }

    @Override
    public void focusGained(FocusEvent e) {
        String temp = jtf.getText();
        if (temp.equals(info)){
            jtf.setText("");
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        String temp = jtf.getText();
        if (temp.equals("")){
            jtf.setText(info);
        }
    }
}

class MyProgressBar implements Runnable {
    private UpdateAdbProgressBar updateAdbProgressBar;

    public MyProgressBar(UpdateAdbProgressBar updateAdbProgressBar) {
        this.updateAdbProgressBar = updateAdbProgressBar;
    }

    @Override
    public void run() {
        while (updateAdbProgressBar.getValue() != 100){
            updateAdbProgressBar.updateProgress();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
