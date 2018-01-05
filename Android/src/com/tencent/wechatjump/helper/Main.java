package com.tencent.wechatjump.helper;

import com.tencent.wechatjump.helper.util.FileUtil;
import com.tencent.wechatjump.helper.util.HelperUtil;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * Created by xushanmeng on 2017/12/30.
 */

public class Main {

    public static void main(String[] args) {

        if (!Constants.BASE_DIR.exists()) {
            Constants.BASE_DIR.mkdirs();
        }
        try {
            String adb = null;
            String system = System.getProperty("os.name");
            if (system.contains("Windows")) {
                adb = "resource/adb_windows";
            } else if (system.contains("Mac")) {
                adb = "resource/adb_mac";
            } else {
                System.out.println("该程序暂不支持" + system);
                return;
            }
            InputStream is = Main.class.getClassLoader().getResourceAsStream(adb);
            boolean result = FileUtil.copy(is, new FileOutputStream(Constants.ADB_PATH));
            if (!result) {
                System.out.println("adb解压失败");
                return;
            }
            Constants.ADB_PATH.setExecutable(true);
        } catch (Exception e) {
            System.out.println("adb解压失败");
            return;
        }
        ArrayList<String> devices = getConnectedDevices();
        if (devices.size() == 1) {
            System.out.println("检测到您连接的手机为：" + devices.get(0) + "\n");
        } else {
            if (devices.size() == 0) {
                System.out.println("未检测到已连接的手机，等待手机连接...");
            } else {
                System.out.println("检测到已连接多部手机，请移除多余手机，只留下一台...");
            }
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                devices = getConnectedDevices();
                if (devices.size() == 1) {
                    break;
                }
            }
            System.out.println("\n检测到您连接的手机为：" + devices.get(0) + "\n");
        }
        System.out.println("目前已适配手机分辨率\n"
                + "  a) 1600x2560，跳跃系数0.92\n"
                + "  b) 1440x2560，跳跃系数1.039\n"
                + "  c) 1080x1920，跳跃系数1.392\n"
                + "  d) 720x1280，跳跃系数2.078\n");
        System.out.println("正在检测手机分辨率，请稍候...");
        File imageFile = new File(Constants.BASE_DIR, "screen.png");
        if (imageFile.exists()) {
            imageFile.delete();
        }
        boolean result = HelperUtil.execute(Constants.ADB_PATH.getAbsolutePath(), "shell", "screencap", "-p", "/sdcard/screen.png");
        if (!result) {
            System.out.println("手机分辨率检测失败，请检查电脑与手机连接和手机设置。");
            return;
        }
        result = HelperUtil.execute(Constants.ADB_PATH.getAbsolutePath(), "pull", "/sdcard/screen.png", Constants.BASE_DIR.getAbsolutePath());
        if (!result) {
            System.out.println("手机分辨率检测失败，请检查电脑与手机连接和手机设置。");
            return;
        }
        int screenWidth;
        int screenHeight;
        try {
            BufferedImage image = ImageIO.read(imageFile);
            screenWidth = image.getWidth();
            screenHeight = image.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("手机分辨率检测失败，请检查电脑与手机连接和手机设置。");
            return;
        }
        if (screenWidth == 0 || screenHeight == 0) {
            System.out.println("手机分辨率检测失败，请检查电脑与手机连接和手机设置。");
            return;
        }
        System.out.println("检测到您的手机分辨率为" + screenWidth + "x" + screenHeight);
        String screenStr = screenWidth + "x" + screenHeight;
        double jumpParam = 0;
        switch (screenStr) {
            case "1600x2560":
                jumpParam = Constants.JUMP_PARAM_1600x2560;
                break;
            case "1440x2560":
                jumpParam = Constants.JUMP_PARAM_1440x2560;
                break;
            case "1080x1920":
                jumpParam = Constants.JUMP_PARAM_1080x1920;
                break;
            case "720x1280":
                jumpParam = Constants.JUMP_PARAM_720x1280;
                break;
        }
        Scanner scanner = new Scanner(System.in);
        if (jumpParam == 0) {
            System.out.print("暂时不兼容您的手机分辨率，请直接输入您的跳跃系数：");
        } else {
            System.out.print("推荐跳跃系数为" + jumpParam + "，是否使用系统推荐跳跃系数？(y/n)");
            while (true) {
                String line = scanner.nextLine();
                if ("y".equalsIgnoreCase(line.trim()) || "yes".equalsIgnoreCase(line.trim())) {
                    break;
                } else if ("n".equalsIgnoreCase(line.trim()) || "no".equalsIgnoreCase(line.trim())) {
                    jumpParam = 0;
                    System.out.print("请输入您的跳跃系数：");
                    break;
                } else {
                    System.out.print("命令输入错误，请重新输入：");
                }
            }
        }
        while (jumpParam == 0) {
            String line = scanner.nextLine();
            if (line == null) {
                continue;
            }
            try {
                jumpParam = Double.valueOf(line);
            } catch (Exception e) {
            }
            if (jumpParam == 0) {
                System.out.print("输入格式错误，请重新输入：");
            }
        }
        System.out.println("正在开始自动跳一跳...");
        new Helper(jumpParam).start();
    }

    private static ArrayList<String> getConnectedDevices() {
        ArrayList<String> devices = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec(new String[]{Constants.ADB_PATH.getAbsolutePath(), "devices"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean startCount = false;
            String deviceName;
            while ((line = reader.readLine()) != null) {
                if (line.contains("List of devices attached")) {
                    startCount = true;
                } else if (startCount && !line.trim().isEmpty()
                        && !line.contains("unauthorized")
                        && !line.contains("offline")) {
                    String seg[] = line.split("\t");
                    deviceName = seg[0];
                    devices.add(deviceName);
                }
            }
        } catch (Exception e) {
        }
        return devices;
    }
}
