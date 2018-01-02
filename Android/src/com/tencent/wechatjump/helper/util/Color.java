package com.tencent.wechatjump.helper.util;

import com.tencent.wechatjump.helper.bean.Pixel;

/**
 * Created by xushanmeng on 2017/12/30.
 */

public class Color {

    /**
     * 提取红色值，范围0-255
     */
    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    /**
     * 提取绿色值，范围0-255
     */
    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    /**
     * 提取蓝色值，范围0-255
     */
    public static int blue(int color) {
        return color & 0xFF;
    }

    /**
     * 比较颜色是否相近
     * @param shift 容差
     */
    public static boolean compareColor(int color1, int color2, int shift) {
        int red1 = Color.red(color1);
        int green1 = Color.green(color1);
        int blue1 = Color.blue(color1);
        int red2 = Color.red(color2);
        int green2 = Color.green(color2);
        int blue2 = Color.blue(color2);
        if (Math.abs(red1 - red2) + Math.abs(green1 - green2) + Math.abs(blue1 - blue2) > shift) {
            return false;
        }
        return true;
    }

    /**
     * 比较颜色是否相同
     */
    public static boolean compareColor(int color1, int color2) {
        return compareColor(color1, color2, 0);
    }

    /**
     * 比较颜色是否相同
     */
    public static boolean compareColor(Pixel pixel1, Pixel pixel2) {
        return compareColor(pixel1.color, pixel2.color);
    }

}
