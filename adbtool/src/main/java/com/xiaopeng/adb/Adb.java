package com.xiaopeng.adb;

import com.xiaopeng.model.AdbModel;
import com.xiaopeng.ui.ProgressBar;
import com.xiaopeng.utils.UpdateAdbProgressBar;
import com.xiaopeng.utils.Utils;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Adb {
    private static AdbModel adbModel = null;
    private static String proRootDir = System.getProperty("user.dir");

    public Adb() {
        adbModel = (AdbModel) new Utils().parseYamlToBean(
                Paths.get(proRootDir,"src/main/java/com/xiaopeng/config/adbConfig.yaml").toString(),
                AdbModel.class
        );
    }

    public static void push(String src,String dest){
        String adbPush = adbModel.getAdb().get(3);
        adbExec(adbPush,src,dest);
    }

    public static void pull(String src,String dest){
        String adbPull = adbModel.getAdb().get(4);
        adbExec(adbPull,src,dest);
    }

    public static void oneKeyInstall(String src,String dest){
        String cmd = adbModel.getAdb().get(2);                                              // adb remount

        StringBuilder rowCmd = new StringBuilder();
        rowCmd.append(adbModel.getAdb().get(1));                                      // adb shell
        rowCmd.append(" ");
        rowCmd.append(adbModel.getCommons().get(0));                                  // rm -rf
        rowCmd.append(" ");
        rowCmd.append(dest);         // /system/app
        List<String> ret = exec(cmd, rowCmd.toString().replace("\\","/"));
        if (ret != null)
            Utils.printlog("----- 操作完成：移除旧版apk成功 -----" + dest);
        else
            Utils.printlog("----- 操作失败：移除旧版apk失败 -----" + dest);

        rowCmd.setLength(0);
        rowCmd.append(adbModel.getAdb().get(3));            // adb push
        rowCmd.append(" ");
        rowCmd.append(src);
        rowCmd.append(" ");
        rowCmd.append(dest);
        ret = exec(rowCmd.toString().replace("\\","/"));
        if (ret != null)
            Utils.printlog("----- 操作完成：安装apk成功 -----" + dest);
        else
            Utils.printlog("----- 操作失败：安装apk失败！ -----" + dest);
    }

    public static List<String> getInstalledApp(){
        String cmd = "adb shell ls /system/app";
        String cmd1 = "adb shell ls /data/app";

        List<String> retOut = exec(cmd,cmd1);
        retOut = convertApkData(retOut,Pattern.compile(".*(/system/app|/data/app).*"),1);
        return retOut;
    }

    private static void adbExec(String adbCmd){
        adbExec(adbCmd,"","");
    }

    private static void adbExec(String adbCmd, String src,String dest){
        String cmd = adbCmd + " " + src +" " + dest;
        Utils.exec(cmd);
    }

    private static List<String> convertApkData(List<String> src, Pattern filter,int groupIndex){
        List<String> temp = new LinkedList<>();
        String tmpContent = "";
        for (String line : src){
            Matcher matcher = filter.matcher(line);
            if (matcher.find()){
                tmpContent = matcher.group(groupIndex);
                continue;
            }
            temp.add(line + " - " + tmpContent);
        }
        return temp;
    }

    public static void clearAdbLog(){
        StringBuilder cmd = new StringBuilder();
        cmd.append(adbModel.getAdb().get(1));
        cmd.append(" ");
        cmd.append(adbModel.getCommons().get(0));
        cmd.append(" ");
        cmd.append("/sdcard/Log");
        exec(cmd.toString());
    }

    public static void pullLogcatLog(UpdateAdbProgressBar updateAdbProgressBar){
        StringBuilder cmd = new StringBuilder();
        cmd.append(adbModel.getAdb().get(4));       // adb pull
        cmd.append(" ");
        cmd.append("/sdcard/Log");
        cmd.append(" ");
        cmd.append(System.getProperty("user.home") + "/Desktop");           // user/Desktop
//        exec(cmd.toString().replace("\\","/"));
        MyThread myThread = new MyThread(cmd.toString().replace("\\","/"),updateAdbProgressBar);
        Thread thread = new Thread(myThread);
        thread.start();
    }

    public static List<String> exec(String... cmds){
        BufferedWriter bufferedWriter = null;
        List<String> retOutput = null;

        String osName = Utils.getOSName().toLowerCase();
        Path shFilePath = null;
        if (osName.contains("windows")){
            shFilePath = Paths.get(System.getenv("temp"),System.currentTimeMillis() + ".bat");
        }else{
            if (!Files.exists(Paths.get("/tmp"))) {
                try {
                    Files.createDirectory(Paths.get("/tmp"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            shFilePath = Paths.get("/tmp",System.currentTimeMillis() + ".sh");
        }

        try {
            bufferedWriter = Files.newBufferedWriter(
                    shFilePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW
            );

            for (String cmd : cmds){
                bufferedWriter.write(cmd);
                bufferedWriter.newLine();
            }
            Utils.closeWriter(bufferedWriter);

            retOutput = exec(shFilePath.toString());
            Files.deleteIfExists(shFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retOutput;
    }

    public static List<String> exec(String cmd){
        Runtime runtime = Runtime.getRuntime();
        List<String> list = new LinkedList<>();
        String line = null;
        try {
            Process process = runtime.exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            while ((line = bufferedReader.readLine()) != null){
                if (!line.isEmpty()) {
                    list.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void  getAdbExecProgress(String cmd,UpdateAdbProgressBar updateAdbProgressBar){
        Runtime runtime = Runtime.getRuntime();
        String line = "";
        try {
            Process process = runtime.exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            while ((line = bufferedReader.readLine()) != null && updateAdbProgressBar.getValue() != 100){
                Pattern pattern = Pattern.compile("^[\\[ ]{1,2}([0-9]{1,4}).+$|.+(pulled).+");
                Matcher matcher = pattern.matcher(line.trim());
                if (matcher.find()){
                    String matchStr = "";
                    for (int i=0;i<matcher.groupCount();i++){
                        if (i > 0) {
                            matchStr = matcher.group(i);
                        if (matchStr == null)
                            matchStr = matcher.group(i + 1);

                        }
                    }
                    updateAdbProgressBar.getProgress(Integer.valueOf(
                            matchStr.equals("pulled") ? "100" : matchStr.trim()
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class MyThread implements Runnable{
    private String cmd;
    private  UpdateAdbProgressBar updateAdbProgressBar;

    public MyThread(String cmd, UpdateAdbProgressBar updateAdbProgressBar) {
        this.cmd = cmd;
        this.updateAdbProgressBar = updateAdbProgressBar;
    }

    @Override
    public void run() {
        Adb.getAdbExecProgress(cmd,updateAdbProgressBar);
    }
}


