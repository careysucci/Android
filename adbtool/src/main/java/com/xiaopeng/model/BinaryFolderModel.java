package com.xiaopeng.model;

import java.util.List;

public class BinaryFolderModel {
    private String host;
    private String protocol;
    private List<String> secondaryDirectory;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public List<String> getSecondaryDirectory() {
        return secondaryDirectory;
    }

    public void setSecondaryDirectory(List<String> secondaryDirectory) {
        this.secondaryDirectory = secondaryDirectory;
    }

    @Override
    public String toString() {
        return "BinaryFolderModel{" +
                "host='" + host + '\'' +
                ", protocol='" + protocol + '\'' +
                ", secondaryDirectory=" + secondaryDirectory +
                '}';
    }
}
