package com.tencent.wechatjump.helper.bean;

/**
 * 目标落点的类型
 */

public enum DesType {

    CUBE("魔方"),
    COVER("井盖"),
    SHOP("商店"),
    DISC("唱片"),
    PUER("平面物体"),
    BUMF("卫生纸"),
    OTHER("其他物体");

    private String name;

    DesType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
