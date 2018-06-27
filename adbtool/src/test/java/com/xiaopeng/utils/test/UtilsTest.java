package com.xiaopeng.utils.test;

import com.xiaopeng.model.BinaryFolderModel;
import com.xiaopeng.utils.Utils;
import org.junit.Test;

public class UtilsTest {
    @Test
    public void testParseConfig() {
        Utils utils = new Utils();
        BinaryFolderModel bin = null;
        bin = (BinaryFolderModel) utils.parseYamlToBean("E:\\Program\\open sources\\adbtool\\src\\main\\java\\com\\xiaopeng\\config\\binaryHostConfig.yaml", BinaryFolderModel.class);
        System.out.println(bin);
    }
}
