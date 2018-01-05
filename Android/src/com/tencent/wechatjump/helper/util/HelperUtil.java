package com.tencent.wechatjump.helper.util;

import com.tencent.wechatjump.helper.Constants;
import com.tencent.wechatjump.helper.bean.Pixel;

/**
 * Created by xushanmeng on 2018/1/1.
 */

public class HelperUtil {

    /**
     * 执行Shell命令，同步耗时操作
     */
    public static final boolean execute(String... cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            int result = process.waitFor();
            if (result != 0) {
                System.out.println("Failed to execute \"" + cmd + "\", result code is " + result);
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 计算像素点的距离
     */
    public static final double calculateDistance(Pixel pixel1, Pixel pixel2) {
        return Math.sqrt(Math.pow((pixel1.y - pixel2.y), 2) + Math.pow((pixel1.x - pixel2.x), 2));
    }

    /**
     * 寻找色块顶点像素
     */
    public static final Pixel[] findVertexs(Pixel[][] pixels, Pixel firstPixcel) {
        Pixel[] vertexs = new Pixel[4];
        Pixel topPixel = firstPixcel;
        Pixel leftPixel = firstPixcel;
        Pixel rightPixel = firstPixcel;
        Pixel bottomPixel = firstPixcel;
        Pixel currentPixcel = firstPixcel;
        while (checkBorder(pixels, currentPixcel)
                && Color.compareColor(pixels[currentPixcel.y - 1][currentPixcel.x], firstPixcel)) {
            currentPixcel = pixels[currentPixcel.y - 1][currentPixcel.x];
        }
        while (checkBorder(pixels, currentPixcel)
                && Color.compareColor(pixels[currentPixcel.y][currentPixcel.x - 1], firstPixcel)) {
            currentPixcel = pixels[currentPixcel.y][currentPixcel.x - 1];
        }
        //寻找上顶点像素
        while (checkBorder(pixels, currentPixcel)) {
            if (Color.compareColor(pixels[currentPixcel.y - 1][currentPixcel.x], firstPixcel)) {
                currentPixcel = pixels[currentPixcel.y - 1][currentPixcel.x];
            } else if (Color.compareColor(pixels[currentPixcel.y][currentPixcel.x + 1], firstPixcel)) {
                currentPixcel = pixels[currentPixcel.y][currentPixcel.x + 1];
            } else {
                topPixel = findCenterPixcelHorizontal(pixels, currentPixcel);
                break;
            }
        }
        //寻找右顶点像素
        while (checkBorder(pixels, currentPixcel)) {
            if (Color.compareColor(pixels[currentPixcel.y][currentPixcel.x + 1], firstPixcel)) {
                currentPixcel = pixels[currentPixcel.y][currentPixcel.x + 1];
            } else if (Color.compareColor(pixels[currentPixcel.y + 1][currentPixcel.x], firstPixcel)) {
                currentPixcel = pixels[currentPixcel.y + 1][currentPixcel.x];
            } else {
                rightPixel = findCenterPixcelVertial(pixels, currentPixcel);
                break;
            }
        }
        //寻找下顶点像素
        while (checkBorder(pixels, currentPixcel)) {
            if (Color.compareColor(pixels[currentPixcel.y + 1][currentPixcel.x], firstPixcel)) {
                currentPixcel = pixels[currentPixcel.y + 1][currentPixcel.x];
            } else if (Color.compareColor(pixels[currentPixcel.y][currentPixcel.x - 1], firstPixcel)) {
                currentPixcel = pixels[currentPixcel.y][currentPixcel.x - 1];
            } else {
                bottomPixel = findCenterPixcelHorizontal(pixels, currentPixcel);
                break;
            }
        }
        //寻找左顶点像素
        while (checkBorder(pixels, currentPixcel)) {
            if (Color.compareColor(pixels[currentPixcel.y][currentPixcel.x - 1], firstPixcel)) {
                currentPixcel = pixels[currentPixcel.y][currentPixcel.x - 1];
            } else if (Color.compareColor(pixels[currentPixcel.y - 1][currentPixcel.x], firstPixcel)) {
                currentPixcel = pixels[currentPixcel.y - 1][currentPixcel.x];
            } else {
                leftPixel = findCenterPixcelVertial(pixels, currentPixcel);
                break;
            }
        }
        vertexs[0] = leftPixel;
        vertexs[1] = topPixel;
        vertexs[2] = rightPixel;
        vertexs[3] = bottomPixel;
        return vertexs;
    }

    /**
     * 获取水平同色像素中心点
     */
    public static final Pixel findCenterPixcelHorizontal(Pixel[][] pixels, Pixel base) {
        Pixel first = base;
        Pixel last = base;
        while (checkBorder(pixels, first)) {
            if (Color.compareColor(pixels[first.y][first.x - 1], base)) {
                first = pixels[first.y][first.x - 1];
            } else {
                break;
            }
        }
        while (checkBorder(pixels, last)) {
            if (Color.compareColor(pixels[last.y][last.x + 1], base)) {
                last = pixels[last.y][last.x + 1];
            } else {
                break;
            }
        }
        return pixels[base.y][(first.x + last.x) / 2];
    }

    /**
     * 获取垂直同色像素中心点
     */
    public static final Pixel findCenterPixcelVertial(Pixel[][] pixels, Pixel base) {
        Pixel first = base;
        Pixel last = base;
        while (checkBorder(pixels, first)) {
            if (Color.compareColor(pixels[first.y - 1][first.x], base)) {
                first = pixels[first.y - 1][first.x];
            } else {
                break;
            }
        }
        while (checkBorder(pixels, last)) {
            if (Color.compareColor(pixels[last.y + 1][last.x], base)) {
                last = pixels[last.y + 1][last.x];
            } else {
                break;
            }
        }
        return pixels[(first.y + last.y) / 2][base.x];
    }

    /**
     * 检测是否超出可处理图像的边缘
     */
    public static final boolean checkBorder(Pixel[][] pixels, Pixel pixel) {
        int width = pixels[0].length;
        int height = pixels.length;
        if (pixel.x < transW(width, Constants.LEFT_BORDER)
                || pixel.x > width - transW(width, Constants.RIGHT_BORDER)
                || pixel.y < transH(height, adjustTopBorder(width,height))
                || pixel.y > height - transH(height, adjustBottomBorder(width,height))) {
            return false;
        }
        return true;
    }

    /**
     * 转换水平数值
     */
    public static final int transW(int screenWidth, int sourceValue) {
        return sourceValue * screenWidth / Constants.BASE_SCREEN_WIDTH;
    }

    /**
     * 转换垂直数值
     */
    public static final int transH(int screenHeight, int sourceValue) {
        return sourceValue * screenHeight / Constants.BASE_SCREEN_HEIGHT;
    }

    public static final int adjustTopBorder(int screenWidth, int screenHeight) {
        return Constants.TOP_BORDER + (screenHeight * Constants.BASE_SCREEN_WIDTH / screenWidth - Constants.BASE_SCREEN_HEIGHT) / 2;
    }

    public static final int adjustBottomBorder(int screenWidth, int screenHeight) {
        return Constants.BOTTOM_BORDER + (screenHeight * Constants.BASE_SCREEN_WIDTH / screenWidth - Constants.BASE_SCREEN_HEIGHT) / 2;
    }

}
