package com.tencent.wechatjump.helper.util;

import java.io.File;

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
}
