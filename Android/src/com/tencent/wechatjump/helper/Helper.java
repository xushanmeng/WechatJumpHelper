package com.tencent.wechatjump.helper;

import com.tencent.wechatjump.helper.bean.DesType;
import com.tencent.wechatjump.helper.bean.Pixel;
import com.tencent.wechatjump.helper.util.Color;
import com.tencent.wechatjump.helper.util.DesTypeChecker;
import com.tencent.wechatjump.helper.util.FileUtil;
import com.tencent.wechatjump.helper.util.HelperUtil;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import static com.tencent.wechatjump.helper.util.DebugUtil.*;

/**
 * Created by xushanmeng on 2018/1/2.
 */

public class Helper {


    private double jumpParam;
    private StringBuilder debugInfo;
    private int cacheIndex = 0;
    private File cacheDir = new File("imageCaches");
    private File markDir = new File("imageMarks");

    public Helper(double jumpParam) {
        this.jumpParam = jumpParam;
    }

    public void start() {
        if (!DEBUG) {
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            if(!markDir.exists()){
                markDir.mkdirs();
            }
            String[] filenames = cacheDir.list();
            for (String filename : filenames) {
                String[] seg = filename.split("\\.");
                try {
                    int index = Integer.valueOf(seg[0]);
                    if(index > cacheIndex){
                        cacheIndex = index;
                    }
                }catch (Exception e){
                    continue;
                }
            }
        }
        while (true) {
            try {
                debugInfo = new StringBuilder();
                long startTime = System.currentTimeMillis();
                long screencapTime = 0;
                long downloadTime = 0;
                long calculateTime = 0;
                long cacheTime = 0;
                long exeJumpTime = 0;
                long recacheTime = 0;
                long tempTime = startTime;
                File cacheFile;
                if (!DEBUG) {
                    //获取截图
                    boolean result;
                    result = HelperUtil.execute("adb shell screencap -p /sdcard/screen.png");
                    if (!result) {
                        System.out.println("截屏失败");
                        Thread.sleep(5000);
                        continue;
                    } else {
                        screencapTime = System.currentTimeMillis() - tempTime;
                    }
                    tempTime += screencapTime;
                    result = HelperUtil.execute("adb pull /sdcard/screen.png .");
                    if (!result) {
                        System.out.println("下载截屏失败");
                        Thread.sleep(5000);
                        continue;
                    } else {
                        downloadTime = System.currentTimeMillis() - tempTime;
                    }
                    tempTime += downloadTime;
                    cacheFile = new File(cacheDir, (++cacheIndex) + ".png");
                    if (cacheIndex > Constants.CACHE_FILE_MAX) {
                        //最多缓存1000张图片
                        File oldFile = new File(cacheDir, (cacheIndex - Constants.CACHE_FILE_MAX) + ".png");
                        oldFile.delete();
                    }
                    //保存缓存图片
                    File imageFile = new File("screen.png");
                    result = FileUtil.copyFile(imageFile,cacheFile);
                    imageFile.deleteOnExit();
                    if (!result) {
                        System.out.println(cacheIndex + ". 截屏缓存保存失败");
                    }
                    cacheTime = System.currentTimeMillis() - tempTime;
                    tempTime += cacheTime;
                } else {
                    cacheIndex = DEBUG_IMAGE;
                    cacheFile = new File(cacheDir, DEBUG_IMAGE + ".png");
                    tempTime = System.currentTimeMillis();
                }

                //读取图片像素
                BufferedImage image = ImageIO.read(cacheFile);
                final int screenWidth = image.getWidth();
                final int screenHeight = image.getHeight();
                debugInfo.append("屏幕参数 ").append(screenWidth).append("x").append(screenHeight).append("\n");
                if (screenWidth == 0 || screenHeight == 0) {
                    System.out.println(cacheIndex + ". 截屏图片读取错误");
                    Thread.sleep(5000);
                    continue;
                }
                Pixel[][] pixels = new Pixel[screenHeight][screenWidth];
                for (int i = 0; i < screenHeight; i++) {
                    for (int j = 0; j < screenWidth; j++) {
                        pixels[i][j] = new Pixel();
                        pixels[i][j].color = image.getRGB(j, i);
                        pixels[i][j].x = j;
                        pixels[i][j].y = i;

                    }
                }

                final int TOP_BORDER = HelperUtil.transH(screenHeight, Constants.TOP_BORDER);
                final int BOTTOM_BORDER = HelperUtil.transH(screenHeight, Constants.BOTTOM_BORDER);
                final int LEFT_BORDER = HelperUtil.transW(screenWidth, Constants.LEFT_BORDER);
                final int RIGHT_BORDER = HelperUtil.transW(screenWidth, Constants.RIGHT_BORDER);

                final int PIECE_WIDTH_PIXELS = HelperUtil.transW(screenWidth, Constants.PIECE_WIDTH_PIXELS);
                final int PIECE_TOP_PIXELS = HelperUtil.transW(screenWidth, Constants.PIECE_TOP_PIXELS);
                final int PIECE_BOTTOM_CENTER_SHIFT = HelperUtil.transH(screenHeight, Constants.PIECE_BOTTOM_CENTER_SHIFT);

                final int DES_MIN_SHIFT = HelperUtil.transH(screenHeight, Constants.DES_MIN_SHIFT);

                /* 计算棋子位置 */
                Pixel piece = new Pixel();
                for (int i = TOP_BORDER; i < screenHeight - BOTTOM_BORDER; i++) {
                    int startX = 0;
                    int endX = 0;
                    for (int j = LEFT_BORDER; j < screenWidth - RIGHT_BORDER; j++) {
                        int red = Color.red(pixels[i][j].color);
                        int green = Color.green(pixels[i][j].color);
                        int blue = Color.blue(pixels[i][j].color);
                        if (50 < red && red < 55
                                && 50 < green && green < 55
                                && 55 < blue && blue < 65) {//棋子顶部颜色
                            //如果侦测到棋子相似颜色，记录下开始点
                            if (startX == 0) {
                                startX = j;
                                endX = 0;
                            }
                        } else if (endX == 0) {
                            //记录下结束点
                            endX = j;

                            if (endX - startX < PIECE_TOP_PIXELS) {
                                //规避井盖的BUG，像素点不够长，则重新计算
                                startX = 0;
                                endX = 0;
                            }
                        }
                        if (50 < red && red < 60
                                && 55 < green && green < 65
                                && 95 < blue && blue < 105) {//棋子底部的颜色
                            //最后探测到的颜色就是棋子的底部像素
                            piece.y = i;
                        }
                    }
                    if (startX != 0 && piece.x == 0) {
                        piece.x = (startX + endX) / 2;
                    }
                }
                if(piece.x == 0 || piece.y == 0){
                    System.out.println(cacheIndex + ". 未找到棋子坐标");
                    final int PRESS_Y = HelperUtil.transH(screenHeight, Constants.RESTART_PRESS_Y);
                    final int PRESS_X = HelperUtil.transW(screenWidth, Constants.RESTART_PRESS_X);
                    HelperUtil.execute("adb shell input swipe "
                            + PRESS_X + " "
                            + PRESS_Y + " "
                            + PRESS_X + " "
                            + PRESS_Y + " "
                            + 100);
                    Thread.sleep(5000);
                    continue;
                }

                //棋子纵坐标从底部边缘调整到底部中心
                piece.y -= PIECE_BOTTOM_CENTER_SHIFT;

                System.out.print(cacheIndex + ". 棋子坐标点[" + piece.x + ", " + piece.y + "], ");

                //计算目标位置
                Pixel des = new Pixel();

                Pixel firstPixcel = null;//目标落点最顶部首个像素位置

                //双重循环寻找落点第一个像素
                for (int i = TOP_BORDER; i < screenHeight - BOTTOM_BORDER; i++) {
                    for (int j = LEFT_BORDER; j < screenWidth - RIGHT_BORDER; j++) {
                        if (piece.x - PIECE_WIDTH_PIXELS / 2 < j && j < piece.x + PIECE_WIDTH_PIXELS / 2) {
                            //忽略和棋子处于同一垂直线的这些像素
                            continue;
                        }
                        if (!Color.compareColor(pixels[i][j].color, pixels[i][0].color, 12)) {
                            //发现色差，记录下落点顶部第一个像素
                            firstPixcel = pixels[i][j];
                            break;
                        }
                    }
                    if (firstPixcel != null) {
                        break;
                    }
                }
                if(firstPixcel == null){
                    System.out.println(cacheIndex + ". 未找到目标落点坐标");
                    Thread.sleep(5000);
                    continue;
                }
                debugInfo.append("落点首个像素 firstPixel").append(firstPixcel).append("\n");

                //计算目标点类型


                Pixel whitePointCenter = findWhitePointCenter(pixels, firstPixcel);
                Pixel puerCenter = findPuerCenter(pixels, firstPixcel);

                DesType desType = DesTypeChecker.getDesType(pixels, firstPixcel, puerCenter);

                String hasWhitePoint;
                if (whitePointCenter != null) {
                    //如果有白点，那目标落点为白点中心
                    des = whitePointCenter;
                    hasWhitePoint = "有靶点";
                } else {
                    hasWhitePoint = "无靶点";
                    if (puerCenter != null) {
                        des = puerCenter;
                    } else {
                        //如果是其他类型且没有靶点，通过斜率计算落点
                        int count = 0;
                        for (int i = firstPixcel.x; i < screenWidth - RIGHT_BORDER; i++) {
                            if (Color.compareColor(pixels[firstPixcel.y][i].color, pixels[firstPixcel.y][firstPixcel.x].color, 10)) {
                                ++count;
                            } else {
                                break;
                            }
                        }

                        des.x = firstPixcel.x + count / 2;
                        float k;//斜率
                        if ((firstPixcel.y - piece.y) * 1.0f / (firstPixcel.x - piece.x) < 0) {
                            k = Constants.K1;
                        } else {
                            k = Constants.K2;
                        }
                        des.y = Math.round(k * (des.x - piece.x) + piece.y);
                        if (des.y < firstPixcel.y + DES_MIN_SHIFT) {
                            des.y = firstPixcel.y + DES_MIN_SHIFT;
                        }
                    }
                }

                /* 计算距离 */
                double distance = HelperUtil.calculateDistance(piece, des);
                System.out.print("目标落点[" + des.x + ", " + des.y + "], " + desType.getName() + ", " + hasWhitePoint + ", 距离" + Math.round(distance) + "px, ");
                calculateTime = System.currentTimeMillis() - tempTime;
                tempTime += calculateTime;
                if (!DEBUG) {
                    //执行跳跃
                    long pressTime = (long) (distance * jumpParam);
                    System.out.println("模拟按压" + pressTime + "ms.");
                    final int PRESS_Y = HelperUtil.transH(screenHeight, screenHeight/2 -250 + (int)(Math.random() * 500));
                    final int PRESS_X = HelperUtil.transW(screenWidth, screenWidth/2 - 150 + (int)(Math.random() * 300));
                    HelperUtil.execute("adb shell input swipe "
                            + PRESS_X + " "
                            + PRESS_Y + " "
                            + PRESS_X + " "
                            + PRESS_Y + " "
                            + pressTime);
                    exeJumpTime = System.currentTimeMillis() - tempTime;
                    tempTime += exeJumpTime;
                    //保存带标记的图像
                    Graphics graphics = image.getGraphics();
                    graphics.setColor(java.awt.Color.RED);
                    graphics.drawLine(des.x, des.y, piece.x, piece.y);
                    graphics.setFont(new Font("宋体", Font.BOLD, HelperUtil.transW(screenWidth,60)));
                    graphics.drawString(desType.getName() + "，" + hasWhitePoint, HelperUtil.transW(screenWidth,10), HelperUtil.transW(screenWidth,70));
                    graphics.drawString("跳跃距离" + Math.round(distance) + "px", HelperUtil.transW(screenWidth,10), HelperUtil.transW(screenWidth,135));
                    graphics.drawString("模拟按压" + pressTime + "ms", HelperUtil.transW(screenWidth,10), HelperUtil.transW(screenWidth,200));
                    ImageIO.write(image, "png", new File(markDir,cacheIndex+"_mark.png"));
                    recacheTime = System.currentTimeMillis() - tempTime;
                } else {
                    System.out.println("DEBUG");
                    System.out.print(debugInfo);
                }

                System.out.print("截图耗时" + screencapTime + "ms, ");
                System.out.print("下载耗时" + downloadTime + "ms, ");
                System.out.print("缓存耗时" + cacheTime + "ms, ");
                System.out.print("计算耗时" + calculateTime + "ms, ");
                System.out.print("跳跃耗时" + exeJumpTime + "ms, ");
                System.out.print("标记耗时" + recacheTime + "ms, ");
                System.out.println("总耗时" + (System.currentTimeMillis() - startTime) + "ms\n");

                /*停留一会，保证棋子落稳*/
                if (desType == DesType.COVER
                        || desType == DesType.CUBE
                        || desType == DesType.SHOP
                        || desType == DesType.DISC) {
                    //特殊类型的落点，停留时间长了会加分
                    Thread.sleep(2000);
                } else {
                    Thread.sleep(1500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取纯平像素点中心，如果检测到不是纯平面，则返回null
     */
    public Pixel findPuerCenter(Pixel[][] pixels, Pixel firstPixcel) {

        Pixel[] vertexs = HelperUtil.findVertexs(pixels, firstPixcel);
        Pixel puerLeft = vertexs[0];
        Pixel puerTop = vertexs[1];
        Pixel puerRight = vertexs[2];
        Pixel puerBottom = vertexs[3];
        Pixel puerCenter;

        debugInfo.append("PuerBorder[")
                .append(puerLeft.x).append(", ")
                .append(puerTop.y).append(", ")
                .append(puerRight.x).append(", ")
                .append(puerBottom.y).append("]\n");
        int screenWidth = pixels[0].length;
        int screenHeight = pixels.length;
        int puerWidth = puerRight.x - puerLeft.x + 1;
        int puerHeight = puerBottom.y - puerTop.y + 1;
        int distanceMin = HelperUtil.transW(screenWidth, 30);
        if (HelperUtil.transW(screenWidth, 110) <= puerWidth && puerWidth <= HelperUtil.transW(screenWidth, 610)
                && HelperUtil.transH(screenHeight, 64) <= puerHeight && puerHeight <= HelperUtil.transH(screenHeight, 350)
                && HelperUtil.calculateDistance(puerTop, puerLeft) > distanceMin
                && HelperUtil.calculateDistance(puerTop, puerRight) > distanceMin
                && HelperUtil.calculateDistance(puerBottom, puerLeft) > distanceMin
                && HelperUtil.calculateDistance(puerBottom, puerRight) > distanceMin) {
            puerCenter = pixels[(puerTop.y + puerBottom.y) / 2][(puerLeft.x + puerRight.x) / 2];
        } else {
            puerCenter = null;
        }
        return puerCenter;
    }

    /**
     * 获取靶点(落点中心的白点)中心，如果检测到没有靶点，则返回null
     */
    private Pixel findWhitePointCenter(Pixel[][] pixels, Pixel firstPixcel) {
        int screenWidth = pixels[0].length;
        int screenHeight = pixels.length;
        //尝试寻找白点
        Pixel whitePointBase = null;
        final int WHITE_POINT_COLOR = 0xf5f5f5;
        int widthShift = HelperUtil.transW(screenWidth, 15);
        int heightShift = HelperUtil.transH(screenHeight, 200);
        for (int i = firstPixcel.y; i < firstPixcel.y + heightShift; i++) {
            for (int j = firstPixcel.x - widthShift; j < firstPixcel.x + widthShift; j++) {
                if (Color.compareColor(pixels[i][j].color, WHITE_POINT_COLOR)) {
                    //发现白点相似颜色
                    whitePointBase = pixels[i][j];
                    break;
                }
            }
            if (whitePointBase != null) {
                break;
            }
        }
        if (whitePointBase == null) {
            return null;
        }
        if (DesTypeChecker.checkBumf(pixels, firstPixcel)) {
            //如果是卫生纸,修正白色点的基准
            whitePointBase = pixels[firstPixcel.y + HelperUtil.transH(screenHeight, 62)][firstPixcel.x];
        }

        Pixel[] vertexs = HelperUtil.findVertexs(pixels, whitePointBase);
        Pixel whitePointLeft = vertexs[0];
        Pixel whitePointTop = vertexs[1];
        Pixel whitePointRight = vertexs[2];
        Pixel whitePointBottom = vertexs[3];
        Pixel whitePointCenter;

        debugInfo.append("WhitePointBorder[")
                .append(whitePointLeft.x).append(", ")
                .append(whitePointTop.y).append(", ")
                .append(whitePointRight.x).append(", ")
                .append(whitePointBottom.y).append("]\n");
        int whitePointWidth = whitePointRight.x - whitePointLeft.x + 1;
        int whitePointHeight = whitePointBottom.y - whitePointTop.y + 1;
        if (HelperUtil.transW(screenWidth, Constants.WHITE_POINT_MIM_WIDTH) <= whitePointWidth
                && whitePointWidth <= HelperUtil.transW(screenWidth, Constants.WHITE_POINT_MAX_WIDTH)
                && HelperUtil.transH(screenHeight, Constants.WHITE_POINT_MIM_HEIGHT) <= whitePointHeight
                && whitePointHeight <= HelperUtil.transH(screenHeight, Constants.WHITE_POINT_MAX_HEIGHT)) {
            whitePointCenter = pixels[(whitePointTop.y + whitePointBottom.y) / 2][(whitePointLeft.x + whitePointRight.x) / 2];
        } else {
            whitePointCenter = null;
        }
        return whitePointCenter;
    }

}
