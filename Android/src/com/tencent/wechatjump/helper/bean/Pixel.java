package com.tencent.wechatjump.helper.bean;

import com.tencent.wechatjump.helper.util.Color;

/**
 * 像素点
 */

public class Pixel {
    public int x;
    public int y;
    public int color;

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]-(" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Pixel) {
            Pixel op = (Pixel) o;
            if (this.x == op.x
                    && this.y == op.y
                    && this.color == op.color) {
                return true;
            }
        }
        return false;
    }
}
