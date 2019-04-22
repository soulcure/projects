package com.taku.safe.login;


import android.text.TextUtils;

import com.taku.safe.config.FileConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by colin on 2017/9/5
 */

public class UserInfo {

    private static final String CONFIG_FILE = "userInfo.properties";

    private File mFile;


    public UserInfo() {
        String path = FileConfig.getInfoPaths();
        mFile = new File(path, CONFIG_FILE);
        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasValue(String key) {
        boolean res = false;
        String value = readProperties(key);
        if (!TextUtils.isEmpty(value)) {
            res = Boolean.parseBoolean(value);
        }
        return res;
    }


    public void setValue(String key, String value) {
        writeProperties(key, value);
    }

    private void writeProperties(String key, String value) {
        //保存属性到 config.properties文件
        InputStream in = null;
        OutputStream os = null;
        try {
            in = new FileInputStream(mFile);
            Properties props = new Properties();
            props.load(in);
            in.close();

            os = new FileOutputStream(mFile);
            props.setProperty(key, value);
            props.store(os, null);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private String readProperties(String key) {
        String res = "";
        InputStream is = null;
        try {
            is = new FileInputStream(mFile);

            Properties props = new Properties();
            props.load(is);

            res = props.getProperty(key);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

}
