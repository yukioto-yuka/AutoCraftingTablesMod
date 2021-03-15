package com.yukioto_it.util.gui;

import net.minecraft.util.ResourceLocation;

public class FrameGuiTexture extends GuiTexture {
    public final int faceSize;
    public final int borderSize;

    public FrameGuiTexture(ResourceLocation texture, int faceSize, int borderSize) {
        super(texture);
        this.faceSize = faceSize;
        this.borderSize = borderSize;
    }
}
