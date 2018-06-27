package com.xiaopeng.adb.test;

import com.xiaopeng.adb.Adb;
import com.xiaopeng.model.AdbModel;
import com.xiaopeng.utils.Utils;
import javafx.beans.property.Property;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdbTest {
    private Utils utils = null;

    @Before
    public void setUp() throws Exception {
        utils = new Utils();
    }

    @Test
    public void testAdbModel() {
        AdbModel adb = (AdbModel) utils.parseYamlToBean(Paths.get(System.getProperty("user.dir"),"src\\main\\java\\com\\xiaopeng\\config\\adbConfig.yaml").toString(),AdbModel.class);
        System.out.println(adb.toString());
    }

    @Test
    public void testExec() {
        List<String> list = Adb.exec("adb shell ls /system/app");
        System.out.println(Arrays.toString(list.toArray()));
        System.out.println(list.size());
    }

    @Test
    public void testTempExec() {
        for (Map.Entry<Object,Object> entry : System.getProperties().entrySet()){
            System.out.println(entry);
        }
    }

    @Test
    public void testExecNot() throws Exception {
        Pattern pattern = Pattern.compile("^\\[([ 0-9]{1,3}).+$|.+(pulled).+");
        Matcher matcher = pattern.matcher("[90%]/sdcard/Log/log0/logcat.txt: 1 file pulled. 3.7 MB/s (4951300 bytes in 1.278s)");
        System.out.println(matcher.groupCount());
        System.out.println(matcher.matches());
    }

    void add(Integer a){
        a++;
    }
}
