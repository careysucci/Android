package com.xiaopeng.download;

import com.xiaopeng.model.BinaryFolderModel;
import com.xiaopeng.utils.Utils;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

public class Downloads {
    private String dailyDateRegex = "^\\d{1-4}\\d{1-2}\\d{1-2}$";
    private static Utils utils = new Utils();
    private static String rootPath = System.getProperty("user.dir");

    public boolean downloadFile(String url){

        return true;
    }

    public String getFileUrl(String rootUrl){
        List<String> visitResult = sortDirectory(generateDailyBinaryPath());
        return null;
    }

    private List<String> sortDirectory(String url){
        List<String> visitResult = new LinkedList<>();
        File[] files = new File(url).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory())
                    return true;
                return false;
            }
        });

        for (File file : files){
            visitResult.add(file.getName());
        }

        return visitResult;
    }

    private String generateDailyBinaryPath(){
        BinaryFolderModel binaryFolderModelObj = new BinaryFolderModel();
        binaryFolderModelObj = (BinaryFolderModel) utils.parseYamlToBean(Paths.get(rootPath,"src/main/java/com/xiaopeng/config/binaryHostConfig.yaml").toString(),BinaryFolderModel.class);

        String host = "";
        String protocol = "";
        String firstDir = "";
        String secondaryDir = "";
        String binaryPath = "";
        try{
            host = binaryFolderModelObj.getHost();
            protocol = binaryFolderModelObj.getProtocol();
            firstDir = binaryFolderModelObj.getSecondaryDirectory().get(0);
            secondaryDir = binaryFolderModelObj.getSecondaryDirectory().get(1);
        }catch(IllegalArgumentException e){

        }

        if (protocol.toLowerCase().equals("ftp") || protocol.toLowerCase().equals("smb")){
            binaryPath = Paths.get("\\",host,firstDir,secondaryDir).toString();
        }else {
            binaryPath = Paths.get(protocol,host,firstDir,secondaryDir).toString();
        }
        return binaryPath;
    }

    private List<String> sort(List<String> list,int low,int high){
        int mid = (low + high)/2;
        if (low > high){
            sort(list,low,mid);
            sort(list,mid+1,high);
            return mergeSort(list,low,mid,high);
        }
        return null;
    }

    private List<String> mergeSort(List<String> stringList,int low,int mid, int high){
        List<String> temp = new LinkedList<>();
        int i = low;
        int y = mid + 1;
        while (i<=mid && y <= high ){
            if (stringList.get(i).compareToIgnoreCase(stringList.get(y)) > 0){
                temp.add(stringList.get(i));
            }else {
                temp.add(stringList.get(y));
            }
        }

        while (i<=mid){
            temp.add(stringList.get(i));
        }

        while (y<=high){
            temp.add(stringList.get(y));
        }

        for (int k=0;k<temp.size();k++){
            stringList.add(k+low,temp.get(k+low));
        }
        return stringList;
    }
}
