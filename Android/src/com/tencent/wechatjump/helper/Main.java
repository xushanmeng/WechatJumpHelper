package com.tencent.wechatjump.helper;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by xushanmeng on 2017/12/30.
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("目前已适配手机分辨率\n" +
                "  a) 1600x2560，跳跃系数0.92\n" +
                "  b) 1440x2560，跳跃系数1.039\n" +
                "  c) 1080x1920，跳跃系数1.392\n" +
                "  d) 720x1080，跳跃系数2.078\n");
        double jumpParam = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入机型序号(a-d)，或者直接输入跳跃系数：");
        while (jumpParam == 0) {
            String line = scanner.nextLine();
            if (line == null) {
                continue;
            }
            switch (line) {
                case "a":
                    jumpParam = Constants.JUMP_PARAM_1600x2560;
                    break;
                case "b":
                    jumpParam = Constants.JUMP_PARAM_1440x2560;
                    break;
                case "c":
                    jumpParam = Constants.JUMP_PARAM_1080x1920;
                    break;
                case "d":
                    jumpParam = Constants.JUMP_PARAM_720x1080;
                    break;
                default:
                    try {
                        jumpParam = Double.valueOf(line);
                    } catch (Exception e) {
                    }
                    break;
            }
            if (jumpParam == 0) {
                System.out.print("输入格式错误，请重新输入：");
            }
        }
        new Helper(jumpParam).start();
    }
}
