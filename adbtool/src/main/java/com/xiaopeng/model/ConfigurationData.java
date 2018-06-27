package com.xiaopeng.model;

import java.io.FileInputStream;
import java.io.InputStream;

public interface ConfigurationData {
    public Object parseYamlToBean(String yamlPath, Class clazz);

}
