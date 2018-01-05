package com.tencent.wechatjump.helper.util;

import com.tencent.wechatjump.helper.bean.DesType;
import com.tencent.wechatjump.helper.bean.Pixel;

/**
 * Created by xushanmeng on 2018/1/1.
 */

public class DesTypeChecker {

    /**
     * 获取目标落点类型
     *
     * @param pixels      像素矩阵
     * @param firstPixcel 探测到的落点第一个像素
     * @param puerCenter  目标落点纯平面的中心，如果没有传null
     */
    public static final DesType getDesType(Pixel[][] pixels, Pixel firstPixcel, Pixel puerCenter) {
        DesType desType = DesType.OTHER;
        if (DesTypeChecker.checkCube(pixels, firstPixcel)) {
            desType = DesType.CUBE;
        } else if (DesTypeChecker.checkCover(pixels, firstPixcel)) {
            desType = DesType.COVER;
        } else if (DesTypeChecker.checkShop(pixels, firstPixcel)) {
            desType = DesType.SHOP;
        } else if (DesTypeChecker.checkDisc(pixels, firstPixcel)) {
            desType = DesType.DISC;
        } else if (DesTypeChecker.checkBumf(pixels, firstPixcel)) {
            desType = DesType.BUMF;
        } else if (puerCenter != null) {
            desType = DesType.PUER;
        }
        return desType;
    }

    /**
     * 检查是否是魔方
     */
    public static final boolean checkCube(Pixel[][] pixels, Pixel firstPixcel) {
        int width = pixels[0].length;
        int height = pixels.length;

        int x1 = firstPixcel.x;
        int y1 = firstPixcel.y + HelperUtil.transW(width, 30);
        int x2 = firstPixcel.x + HelperUtil.transW(width, 40);
        int y2 = firstPixcel.y + HelperUtil.transW(width, 260);
        int x3 = firstPixcel.x + HelperUtil.transW(width, 40);
        int y3 = firstPixcel.y + HelperUtil.transW(width, 320);
        int x4 = firstPixcel.x + HelperUtil.transW(width, -40);
        int y4 = firstPixcel.y + HelperUtil.transW(width, 320);

        if (!Color.compareColor(pixels[y1][x1].color, 0x6b9cf8)) {
            return false;
        }
        if (!Color.compareColor(pixels[y2][x2].color, 0xf3695d)) {
            return false;
        }
        if (!Color.compareColor(pixels[y3][x3].color, 0xf3b409)) {
            return false;
        }
        if (!Color.compareColor(pixels[y4][x4].color, 0xca6e07)) {
            return false;
        }
        return true;
    }

    /**
     * 检查是否是井盖
     */
    public static final boolean checkCover(Pixel[][] pixels, Pixel firstPixcel) {
        int width = pixels[0].length;
        int height = pixels.length;

        int x1 = firstPixcel.x;
        int y1 = firstPixcel.y + HelperUtil.transW(width, 30);
        int x2 = firstPixcel.x + HelperUtil.transW(width, 25);
        int y2 = firstPixcel.y + HelperUtil.transW(width, 280);
        int x3 = firstPixcel.x + HelperUtil.transW(width, -25);
        int y3 = firstPixcel.y + HelperUtil.transW(width, 280);
        int x4 = firstPixcel.x + HelperUtil.transW(width, 5);
        int y4 = firstPixcel.y + HelperUtil.transW(width, 320);

        if (!Color.compareColor(pixels[y1][x1].color, 0x757575)) {
            return false;
        }
        if (!Color.compareColor(pixels[y2][x2].color, 0x6c6c6c)) {
            return false;
        }
        if (!Color.compareColor(pixels[y3][x3].color, 0x5a5a5a)) {
            return false;
        }
        if (!Color.compareColor(pixels[y4][x4].color, 0xf3c30a)) {
            return false;
        }
        return true;
    }

    /**
     * 检查是否是商店
     */
    public static final boolean checkShop(Pixel[][] pixels, Pixel firstPixcel) {
        int width = pixels[0].length;
        int height = pixels.length;

        int x1 = firstPixcel.x;
        int y1 = firstPixcel.y + HelperUtil.transW(width, 20);
        int x2 = firstPixcel.x + HelperUtil.transW(width, 10);
        int y2 = firstPixcel.y + HelperUtil.transW(width, 315);
        int x3 = firstPixcel.x + HelperUtil.transW(width, -10);
        int y3 = firstPixcel.y + HelperUtil.transW(width, 320);
        int x4 = firstPixcel.x + HelperUtil.transW(width, 60);
        int y4 = firstPixcel.y + HelperUtil.transW(width, 360);

        if (!Color.compareColor(pixels[y1][x1].color, 0xe6e6e6)) {
            return false;
        }
        if (!Color.compareColor(pixels[y2][x2].color, 0xf4f4f4)) {
            return false;
        }
        if (!Color.compareColor(pixels[y3][x3].color, 0xcccccc)) {
            return false;
        }
        if (!Color.compareColor(pixels[y4][x4].color, 0xe1ddd3)) {
            return false;
        }
        return true;
    }

    /**
     * 检查是否是唱片
     */
    public static final boolean checkDisc(Pixel[][] pixels, Pixel firstPixcel) {
        int width = pixels[0].length;
        int height = pixels.length;

        int x1 = firstPixcel.x;
        int y1 = firstPixcel.y + HelperUtil.transW(width, 95);
        int x2 = firstPixcel.x ;
        int y2 = firstPixcel.y + HelperUtil.transW(width, 277);
        int x3 = firstPixcel.x ;
        int y3 = firstPixcel.y + HelperUtil.transW(width, 225);

        if (!Color.compareColor(pixels[y1][x1].color, 0x767676)) {
            return false;
        }
        if (!Color.compareColor(pixels[y2][x2].color, 0x6b6b6b)) {
            return false;
        }
        if (!Color.compareColor(pixels[y3][x3].color, 0x767676)) {
            return false;
        }
        return true;
    }

    /**
     * 检查是否是卫生纸
     */
    public static final boolean checkBumf(Pixel[][] pixels, Pixel firstPixcel) {
        int width = pixels[0].length;
        int height = pixels.length;

        int x1 = firstPixcel.x;
        int y1 = firstPixcel.y + HelperUtil.transW(width, 8);
        int x2 = firstPixcel.x + HelperUtil.transW(width, 35);
        int y2 = firstPixcel.y + HelperUtil.transW(width, 184);
        int x3 = firstPixcel.x + HelperUtil.transW(width, -3);
        int y3 = firstPixcel.y + HelperUtil.transW(width, 171);

        if (!Color.compareColor(pixels[y1][x1].color, 0xffffff)) {
            return false;
        }
        if (!Color.compareColor(pixels[y2][x2].color, 0xe7e7e7)) {
            return false;
        }
        if (!Color.compareColor(pixels[y3][x3].color, 0xe3e3e3,5)) {
            return false;
        }
        return true;
    }
}
