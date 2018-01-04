package com.tencent.wechatjump.helper;

/**
 * Created by xushanmeng on 2017/12/30.
 */

public class Constants {

    /*
     * 距离和时间的换算系数
     * 1600x2560机型推荐0.92
     * 1440x2560机型推荐1.039
     * 1080x1920机型推荐1.392
     * 720x1280机型推荐 2.078
     */
    public static final double JUMP_PARAM_1600x2560 = 0.92;
    public static final double JUMP_PARAM_1440x2560 = 1.0392;
    public static final double JUMP_PARAM_1080x1920 = 1.39;
    public static final double JUMP_PARAM_720x1280 = 2.078;


    /* *************************************************************************************
     *
     *  下面是一些数值是基于2560*1440分辨率的手机计算的位置，代码里会按比例换算，如果有偏差，请自行矫正
     *
     * *************************************************************************************/
    //基准屏幕分辨率
    public static final int BASE_SCREEN_WIDTH = 1440;
    public static final int BASE_SCREEN_HEIGHT = 2560;

    //空白边缘可以不用检测，减少计算量
    public static final int TOP_BORDER = 400;//Top的值要在游戏得分的数字下面
    public static final int LEFT_BORDER = 50;
    public static final int RIGHT_BORDER = 50;
    public static final int BOTTOM_BORDER = 200;

    //预置一些点用来计算斜率
    private static final float anchor1X = 1052;
    private static final float anchor1Y = 1132;
    private static final float anchor2X = 448;
    private static final float anchor2Y = 1481;
    private static final float anchor3X = 1407;
    private static final float anchor3Y = 1687;
    private static final float anchor4X = 564;
    private static final float anchor4Y = 1199;
    public static final float K1 = (anchor2Y - anchor1Y) / (anchor2X - anchor1X);//往左上方跳的斜率
    public static final float K2 = (anchor4Y - anchor3Y) / (anchor4X - anchor3X);//往右上方跳的斜率


    public static final int PIECE_BOTTOM_CENTER_SHIFT = 25;//棋子底部椭圆中心到棋子最底部像素的偏移量
    public static final int PIECE_TOP_PIXELS = 8;//棋子最顶部的最少像素个数
    public static final int PIECE_WIDTH_PIXELS = 100;//棋子宽度，不同机型需要修改数值

    public static final int WHITE_POINT_MAX_HEIGHT = 31;//落点中心靶点最大高度
    public static final int WHITE_POINT_MIM_HEIGHT = 29;//落点中心靶点最小高度
    public static final int WHITE_POINT_MAX_WIDTH = 52;//落点中心靶点最大宽度
    public static final int WHITE_POINT_MIM_WIDTH = 50;//落点中心靶点最小宽度

    public static final int DES_MIN_SHIFT = 33;//目标落点物体上顶点到中心的最小距离，如果计算的落点距离上顶点小于这个数值，将无法落稳

    //模拟按压位置，放到重新开始的按钮上，可以自动重新开始
    public static final int RESTART_PRESS_X = 720;
    public static final int RESTART_PRESS_Y = 2116;

    //缓存图片最大数量
    public static final int CACHE_FILE_MAX = 1000;

}

