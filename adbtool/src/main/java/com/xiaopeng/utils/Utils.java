package com.xiaopeng.utils;


import com.xiaopeng.model.ConfigurationData;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;


public class Utils implements ConfigurationData {
    private static Yaml yaml = new Yaml();

    @Override
    public Object parseYamlToBean(String yamlPath, Class clazz) {
        Object t = null;
        BufferedInputStream bufferedInputStream = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(yamlPath);
            bufferedInputStream = new BufferedInputStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try{
            t = yaml.loadAs(bufferedInputStream,clazz);
        }catch (YAMLException e){
            System.out.println("解析yaml文件失败，msg：" + e.getMessage());
        }

        closeInputStream(inputStream);
        closeInputStream(bufferedInputStream);
        return t;
    }

    public static void closeInputStream(InputStream inputStream){
        if (inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void closeOutputStream(OutputStream outputStream){
        if (outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void closeWriter(Writer writer){
        if (writer != null){
            try {
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void closeReader(Reader reader){
        if (reader != null){
            try {
                reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static String getOSName(){
        return System.getProperty("os.name");
    }

    public static void exec(String command){
        exec("",command);
    }

    public static void exec(String dir,String command){
        exec(dir,command,true);
    }

    public static void exec(String dir,String command,boolean show){
        if (show){
            printlog(command);
        }
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(dir + command);
            process.waitFor();
            printlog("----- 操作完成 ----- message : ");
        } catch (IOException e) {
            e.printStackTrace();
            printlog("----- 啊哦，发生错误了~ ----- message : "+e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            printlog("----- 啊哦，操作中断了~ ----- message : "+e.getMessage());
        }
    }

    public static void printlog(String log){
        System.out.println(log);
    }
}
