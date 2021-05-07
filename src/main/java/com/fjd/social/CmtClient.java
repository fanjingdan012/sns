package com.fjd.social;

import com.fjd.cli.BusinessException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class CmtClient {
    private Properties properties = new Properties();
    public String getGlobalSettings(String key) {
        return properties.getProperty(key);
    }

    private static CmtClient instance;

    private CmtClient() {
        try {
            InputStream in = new BufferedInputStream(new FileInputStream("configuration.properties"));
            properties.load(in);
        }catch (IOException e){
            throw new BusinessException("Fail to load configuration.properties, please check if it is there.");
        }
    }

    public static CmtClient getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new CmtClient();
            return instance;
        }

    }



}
