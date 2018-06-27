package com.xiaopeng.model;

import java.util.List;

public class AdbModel {
    private List<String> adb;
    private List<String> am;
    private List<String> apps;
    private List<String> commons;

    public List<String> getAdb() {
        return adb;
    }

    public void setAdb(List<String> adb) {
        this.adb = adb;
    }

    public List<String> getAm() {
        return am;
    }

    public void setAm(List<String> am) {
        this.am = am;
    }

    public List<String> getApps() {
        return apps;
    }

    public void setApps(List<String> apps) {
        this.apps = apps;
    }

    public List<String> getCommons() {
        return commons;
    }

    public void setCommons(List<String> commons) {
        this.commons = commons;
    }

    @Override
    public String toString() {
        return "AdbModel{" +
                "adb=" + adb +
                ", am=" + am +
                ", apps=" + apps +
                ", commons=" + commons +
                '}';
    }
}
