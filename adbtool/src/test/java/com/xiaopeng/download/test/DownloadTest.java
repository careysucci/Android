package com.xiaopeng.download.test;

import com.xiaopeng.download.Downloads;
import org.junit.Before;
import org.junit.Test;


public class DownloadTest {
    private Downloads downloads = null;
    @Before
    public void setUp() throws Exception {
        downloads = new Downloads();
    }

    @Test
    public void testGenerateBinaryPath() {
        System.out.println();
        // \\192.168.0.188\android\binary
    }


    public void testGetFileUrl() {
        downloads.getFileUrl("");
    }


}
