package com.tencent.wechatjump.helper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xushanmeng on 2018/1/1.
 */

public class FileUtil {

    public static final void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles) {
                deleteFile(subFile);
            }
        } else {
            file.delete();
        }
    }

    public static boolean copyFile(File fromFile, File toFile) {
        try {
            FileInputStream fis = new FileInputStream(fromFile);
            FileOutputStream fos = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();
            fis.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
