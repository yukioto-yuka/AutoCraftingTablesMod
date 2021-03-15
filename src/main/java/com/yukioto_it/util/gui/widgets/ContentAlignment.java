package com.yukioto_it.util.gui.widgets;

public enum ContentAlignment {
    TopLeft(ContentDirectionHorizontal.Left, ContentDirectionVertical.Top),
    TopCenter(ContentDirectionHorizontal.Center, ContentDirectionVertical.Top),
    TopRight(ContentDirectionHorizontal.Right, ContentDirectionVertical.Top),
    MiddleLeft(ContentDirectionHorizontal.Left, ContentDirectionVertical.Middle),
    MiddleCenter(ContentDirectionHorizontal.Center, ContentDirectionVertical.Middle),
    MiddleRight(ContentDirectionHorizontal.Right, ContentDirectionVertical.Middle),
    BottomLeft(ContentDirectionHorizontal.Left, ContentDirectionVertical.Bottom),
    BottomCenter(ContentDirectionHorizontal.Center, ContentDirectionVertical.Bottom),
    BottomRight(ContentDirectionHorizontal.Right, ContentDirectionVertical.Bottom);

    private final ContentDirectionHorizontal xDirection;
    private final ContentDirectionVertical yDirection;

    ContentAlignment(ContentDirectionHorizontal x, ContentDirectionVertical y) {
        this.xDirection = x;
        this.yDirection = y;
    }

    public ContentDirectionHorizontal getXDirection() {
        return this.xDirection;
    }

    public ContentDirectionVertical getYDirection() {
        return this.yDirection;
    }
}
