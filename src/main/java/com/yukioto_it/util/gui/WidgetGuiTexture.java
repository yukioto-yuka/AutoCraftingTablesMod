package com.yukioto_it.util.gui;

import net.minecraft.util.ResourceLocation;

public class WidgetGuiTexture extends GuiTexture{
    public final int slotSize;

    public WidgetGuiTexture(ResourceLocation texture, int slotSize) {
        super(texture);
        this.slotSize = slotSize;
    }
}
