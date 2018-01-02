# 微信跳一跳游戏助手

 [GitHub 项目地址](https://github.com/xushanmeng/WechatJumpHelper)

## 功能简介
>用JAVA自动控制手机玩跳一跳
* 自动识别图像计算距离
* 自动帮你点击屏幕
* 自动缓存图片，并在图片上标记一些识别结果，如下图![示例图片](Samples/172_mark.png)


## 运行环境
1. JAVA，最低版本为7.0，[官网下载](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
2. adb驱动，[官网下载(需要翻墙)](https://developer.android.com/studio/releases/platform-tools.html)，或者到[这里](http://www.android-studio.org)下载SDK-tools，其中就包含adb
3. 安卓手机，目前已适配分辨率
    * 1600x2560
    * 1440x2560
    * 1080x1920
    * 720x1280

## 使用方法
>有JAVA开发工具的同学可以直接运行java代码，便于代码调试，下面主要介绍运行已经打包好的jar包的方法
1. 手机打开USB调试，并连接电脑
    * 打开USB调试方法，进入`设置`，找到`开发者选项`，打开并勾选`USB调试`；
    * 如果没有`开发者选项`，进入`关于手机`，连续点击`版本号`7次，即可开启`开发者选项`。   
2. 通过下面的命令，运行[Android.jar](Android/build/libs/Android.jar)
    ```
    java -jar Android.jar
    ```   
3. 根据手机分辨率选择跳跃系数，目前已适配机型：

    * 1600x2560机型推荐0.92
    * 1440x2560机型推荐1.039
    * 1080x1920机型推荐1.392
    * 720x1080机型推荐2.078 
    
    其他分辨率请自己微调。
    
## 原理说明
1. 通过adb命令控制手机截图，并取回到本地

    ```
    adb shell screencap -p /sdcard/screen.png
    adb pull /sdcard/screen.png .
    ```
    
2. 图片分析
    * 根据棋子的颜色，取顶部和底部的特征像素点，在截图中进行匹配，找到棋子坐标
    * 由于目标物体不是在左上就是在右上，可以从上往下扫描，根据色差判断目标物体位置，其中又分为以下几种类型
        * 有靶点，即目标物体中心的白色圆点，则靶点中心为目标落点
        
        ![](Samples/412_mark.png)
        * 无靶点，但是纯色平面，或者规则平面，则平面中心为目标落点
        
        ![](Samples/444_mark.png)
        * 无靶点，又无纯色规则平面，但是左上和右上位置的斜率是固定的，可根据固定斜率的斜线和目标物体中心线的焦点计算落点
        
        ![](Samples/544_mark.png)
    * 计算棋子坐标和目标落点的距离
    * 距离×跳跃系数=按压屏幕的时间，不同分辨率的手机，跳跃系数也有所不同
    
3. 通过adb命令，给手机模拟按压事件
    ```
    adb shell input swipe x y x y time
    ```
    其中`x`和`y`是屏幕坐标，`time`是触摸时间，单位ms
    
## 小窍门
1. 连续的落到物体中心位置，是有分数加成的，最多跳一次可以得几十分
2. 井盖、商店、唱片、魔方，多停留一会，有音乐响起后也是有分数加成的
>那么看一下程序员的朋友圈有多残酷吧

![](Samples/rank.jpeg)
