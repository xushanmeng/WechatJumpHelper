package com.tencent.wechatjump.helper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

	public static boolean copy(InputStream is, OutputStream os) {
		try {
			byte[] buffer = new byte[4096];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			os.flush();
			os.close();
			is.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean copyFile(File fromFile, File toFile) {
		try {
			FileInputStream fis = new FileInputStream(fromFile);
			FileOutputStream fos = new FileOutputStream(toFile);
			return copy(fis, fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
