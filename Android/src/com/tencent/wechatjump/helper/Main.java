package com.tencent.wechatjump.helper;

import com.tencent.wechatjump.helper.util.HelperUtil;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * Created by xushanmeng on 2017/12/30.
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("目前已适配手机分辨率\n" +
                "  a) 1600x2560，跳跃系数0.92\n" +
                "  b) 1440x2560，跳跃系数1.039\n" +
                "  c) 1080x1920，跳跃系数1.392\n" +
                "  d) 720x1280，跳跃系数2.078\n");
        System.out.println("正在检测手机分辨率，请稍候...");
        File imageFile = new File("screen.png");
        if (imageFile.exists()) {
            imageFile.delete();
        }
        boolean result = HelperUtil.execute("adb shell screencap -p /sdcard/screen.png");
        if (!result) {
            System.out.println("手机分辨率检测失败，请检查电脑与手机连接和手机设置。");
            return;
        }
        result = HelperUtil.execute("adb pull /sdcard/screen.png .");
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
            case "720x1080":
                jumpParam = Constants.JUMP_PARAM_720x1080;
                break;
        }
        if (jumpParam == 0) {
            System.out.print("暂时不兼容您的手机分辨率，请直接输入跳跃系数：");
        }
        Scanner scanner = new Scanner(System.in);
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
}
