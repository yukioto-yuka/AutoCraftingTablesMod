package com.yukioto_it.util.gui.widgets;

import java.awt.*;

public class MouseEventArgs {
    public int x;
    public int y;
    public int button;
    public int clickCount;
    public int deltaWheel;
    public long time;

    public boolean passEventToBack = false;
    public boolean passEventToParent = false;

    public MouseEventArgs() {

    }

    public MouseEventArgs(int x, int y, int button, int clickCount, int deltaWheel, long time) {
        this.x = x;
        this.y = y;
        this.button = button;
        this.clickCount = clickCount;
        this.deltaWheel = deltaWheel;
        this.time = time;
    }

    public Point Location() {
        return new Point(this.x, this.y);
    }
}
