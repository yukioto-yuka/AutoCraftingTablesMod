package com.yukioto_it.util.gui.widgets;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.StringUtils;

import java.awt.*;

public class GuiWidgetButton extends GuiWidget {
    public boolean hovered;
    public String text = "";

    public Color foreColor = new Color(50, 50, 50);
    public Color backColor = new Color(111, 111, 111);
    public Color mouseOverBackColor = new Color(126, 136, 191);
    public Color mouseDownBackColor = null;
    public Color borderColor = new Color(0, 0, 0);
    public int borderSize = 1;

    public ContentAlignment textAlign = ContentAlignment.MiddleCenter;
    public boolean autoSize = false;

    public Point hoverTexturePos = null;

    public GuiWidgetButton(int x, int y, int width, int height, String buttonText) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = buttonText;
    }

    public void setHoverTexture(Point texturePos) {
        this.hoverTexturePos = texturePos;
    }

    @Override
    protected void drawWidget(int mouseX, int mouseY, float partialTicks) {
        if (this.autoSize) {
            setSizeFromText(screen.mc.fontRenderer);
        }

        this.hovered = isHovered(mouseX, mouseY);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (texture == null) {
            Color color = backColor;
            if (hovered) color = mouseOverBackColor;
            drawRect(getLeft(), getTop(), getRight(), getBottom(), to32Color(borderColor));
            if (width > borderSize * 2 && height > borderSize * 2)
                drawRect(getLeft() + borderSize, getTop() + borderSize, getRight() - borderSize, getBottom() - borderSize, to32Color(color));
        }
        else {
            Point pos = texturePos;
            if (this.hovered && this.hoverTexturePos != null) {
                pos = this.hoverTexturePos;
            }

            super.drawTexture(this.texture, pos, this.textureSize);
        }

        drawText(screen.mc.fontRenderer);
    }

    private void drawText(FontRenderer fontRenderer) {
        if (StringUtils.isNullOrEmpty(this.text))
            return;

        int textWidth = fontRenderer.getStringWidth(this.text);
        int textHeight = fontRenderer.FONT_HEIGHT;

        int textX = -1;
        int textY = -1;

        switch (textAlign.getXDirection()) {
            case Left:
                textX = padding.left;
                if (texture == null)
                    textX += borderSize;
                break;
            case Center:
                textX = (this.width - textWidth) / 2;
                break;
            case Right:
                textX = this.width - padding.right - textWidth;
                if (texture == null)
                    textX -= borderSize;
                break;
        }

        switch (textAlign.getYDirection()) {
            case Top:
                textY = padding.top;
                if (texture == null)
                    textY += borderSize;
                break;
            case Middle:
                textY = (this.height - textHeight) / 2;
                break;
            case Bottom:
                textY = this.height - padding.bottom - textHeight;
                if (texture == null)
                    textY -= borderSize;
                break;
        }

        fontRenderer.drawStringWithShadow(this.text, this.x + textX, this.y + textY, to32Color(foreColor));
    }

    protected void setSizeFromText(FontRenderer fontRenderer) {
        int textWidth = 0;
        int textHeight = 0;
        if (!StringUtils.isNullOrEmpty(this.text)) {
            textWidth = fontRenderer.getStringWidth(this.text);
            textHeight = fontRenderer.FONT_HEIGHT;
        }

        int w = textWidth + padding.left + padding.right + (borderSize * 2);
        int h = textHeight + padding.top + padding.bottom + (borderSize * 2);
        setSize(w, h);
    }

    protected boolean isHovered(int mouseX, int mouseY) {
        return getRectangle().contains(mouseX, mouseY);
    }

    public void resetText() {
        this.text = "";
    }

    @Override
    protected void mouseDown(MouseEventArgs args) {
        super.mouseDown(args);
        screen.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
