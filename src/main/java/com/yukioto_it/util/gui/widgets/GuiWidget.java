package com.yukioto_it.util.gui.widgets;

import com.yukioto_it.util.gui.GUIPainter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiWidget extends Gui {
    public GUIPainter screen = null;
    public GuiWidget parent = null;
    protected List<GuiWidget> widgets = new ArrayList<>();

    public Insets padding = new Insets(0, 1, 0, 0);
    public Dimension maximumSize = null;
    public Dimension minimumSize = null;

    public boolean visible = true;
    public boolean enabled = true;
    public boolean canSelect = true;
    public String name = null;
    public String toolTipText = null;

    public int x;
    public int y;
    public int width;
    public int height;

    public ResourceLocation texture = null;
    public Point texturePos = null;
    public Dimension textureSize = null;

    public void addWidget(GuiWidget widget) {
        widget.screen = this.screen;
        widget.parent = this;
        this.widgets.add(widget);
    }

    protected static int to32Color(Color color) {
        int result = 0;
        result |= (color.getRed() & 255) << 16;
        result |= (color.getGreen() & 255) << 8;
        result |= (color.getBlue() & 255);
        result |= (color.getAlpha() & 255) << 24;
        return result;
    }

    public boolean hasChildren() {
        return widgets.size() > 0;
    }


    public void setTexture(ResourceLocation texture, Point texturePos) {
        this.texture = texture;
        this.texturePos = texturePos;
    }

    public void setTexture(ResourceLocation texture, Point texturePos, Dimension textureSize) {
        this.texture = texture;
        this.texturePos = texturePos;
        this.textureSize = textureSize;
    }


    protected void drawWidget(int mouseX, int mouseY, float partialTicks) {
        if (this.texture != null) {
            drawTexture(this.texture, this.texturePos, this.textureSize);
        }
    }

    protected void drawTexture(ResourceLocation texture, Point texturePos, Dimension textureSize) {
        screen.mc.getTextureManager().bindTexture(texture);
        GlStateManager.disableDepth();

        if (textureSize == null) {
            this.drawTexturedModalRect(this.x, this.y, texturePos.x, texturePos.y, this.width, this.height);
        }
        else {
            int half_width = this.width / 2;
            int half_height = this.height / 2;
            this.drawTexturedModalRect(this.x, this.y, texturePos.x, texturePos.y, half_width, half_height);
            this.drawTexturedModalRect(this.x + half_width, this.y, (texturePos.x + textureSize.width) - half_width, texturePos.y, half_width, half_height);
            this.drawTexturedModalRect(this.x, this.y + half_height, texturePos.x, (texturePos.y + textureSize.height) - half_height, half_width, half_height);
            this.drawTexturedModalRect(this.x + half_width, this.y + half_height, (texturePos.x + textureSize.width) - half_width, (texturePos.y + textureSize.height) - half_height, half_width, half_height);
        }

        GlStateManager.enableDepth();
    }

    protected void drawWidgetForegroundLayer(int mouseX, int mouseY) {
        if (!StringUtils.isNullOrEmpty(this.toolTipText)) {
            if (getRectangle().contains(mouseX, mouseY)) {
                this.screen.drawHoveringText(this.toolTipText, mouseX, mouseY);
            }
        }
    }


    protected void mouseDown(MouseEventArgs args) {

    }

    protected void mouseUp(MouseEventArgs args) {

    }

    protected void mouseWheel(MouseEventArgs args) {

    }

    protected void mouseMove(MouseEventArgs args) {

    }

    protected void mouseClickMove(MouseEventArgs args) {

    }

    protected void mouseClick(MouseEventArgs args) {

    }

    protected void mouseDoubleClick(MouseEventArgs args) {

    }

    protected void doubleClick() {

    }

    protected void click() {

    }

    protected void keyDown(KeyEventArgs args) {

    }

    protected void keyUp(KeyEventArgs args) {

    }

    protected void keyPress(KeyEventArgs args) {

    }

    protected void updateScreen() {

    }

    protected void onGuiClosed() {

    }

    public void previousDrawWidget(int mouseX, int mouseY, float partialTicks) {
        if (!this.visible)
            return;

        drawWidget(mouseX, mouseY, partialTicks);
        for (GuiWidget widget : widgets) {
            widget.previousDrawWidget(mouseX, mouseY, partialTicks);
        }
    }

    public void previousDrawWidgetForegroundLayer(int mouseX, int mouseY) {
        if (!this.visible)
            return;

        drawWidgetForegroundLayer(mouseX, mouseY);
        for (GuiWidget widget : widgets) {
            widget.previousDrawWidgetForegroundLayer(mouseX, mouseY);
        }
    }

    public GuiWidget previousMouseDown(MouseEventArgs args) {
        if (!this.enabled)
            return null;

        GuiWidget selectedWidget = null;
        for (int i = widgets.size() - 1; i >= 0; i--) {
            GuiWidget widget = widgets.get(i);
            if (!widget.getRectangle().contains(args.x, args.y))
                continue;

            GuiWidget result = widget.previousMouseDown(args);
            if (selectedWidget == null && result != null)
                selectedWidget = result;

            if (!args.passEventToBack)
                break;
        }
        if (!hasChildren() || args.passEventToParent)
            mouseDown(args);
        if (!hasChildren() && this.canSelect)
            return this;

        return selectedWidget;
    }

    public void previousMouseUp(MouseEventArgs args) {
        if (!this.enabled)
            return;

        for (int i = widgets.size() - 1; i >= 0; i--) {
            GuiWidget widget = widgets.get(i);
            if (!widget.getRectangle().contains(args.x, args.y))
                continue;

            widget.previousMouseUp(args);

            if (!args.passEventToBack)
                break;
        }
        if (!hasChildren() || args.passEventToParent)
            mouseUp(args);
    }

    public void previousMouseWheel(MouseEventArgs args) {
        if (!this.enabled)
            return;

        for (int i = widgets.size() - 1; i >= 0; i--) {
            GuiWidget widget = widgets.get(i);
            if (!widget.getRectangle().contains(args.x, args.y))
                continue;

            widget.previousMouseWheel(args);

            if (!args.passEventToBack)
                break;
        }
        if (!hasChildren() || args.passEventToParent)
            mouseWheel(args);
    }

    public void previousMouseMove(MouseEventArgs args) {
        if (!this.enabled)
            return;

        for (int i = widgets.size() - 1; i >= 0; i--) {
            GuiWidget widget = widgets.get(i);
            if (!widget.getRectangle().contains(args.x, args.y))
                continue;

            widget.previousMouseMove(args);

            if (!args.passEventToBack)
                break;
        }
        if (!hasChildren() || args.passEventToParent)
            mouseMove(args);
    }

    public void previousMouseClickMove(MouseEventArgs args) {
        if (!this.enabled)
            return;

        for (int i = widgets.size() - 1; i >= 0; i--) {
            GuiWidget widget = widgets.get(i);
            if (!widget.getRectangle().contains(args.x, args.y))
                continue;

            widget.previousMouseClickMove(args);

            if (!args.passEventToBack)
                break;
        }
        if (!hasChildren() || args.passEventToParent)
            mouseClickMove(args);
    }

    public void previousMouseClick(MouseEventArgs args) {
        if (!this.enabled)
            return;

        for (int i = widgets.size() - 1; i >= 0; i--) {
            GuiWidget widget = widgets.get(i);
            if (!widget.getRectangle().contains(args.x, args.y))
                continue;

            widget.previousMouseClick(args);

            if (!args.passEventToBack)
                break;
        }
        if (!hasChildren() || args.passEventToParent) {
            click();
            mouseClick(args);
        }
    }

    public void previousMouseDoubleClick(MouseEventArgs args) {
        if (!this.enabled)
            return;

        for (int i = widgets.size() - 1; i >= 0; i--) {
            GuiWidget widget = widgets.get(i);
            if (!widget.getRectangle().contains(args.x, args.y))
                continue;

            widget.previousMouseDoubleClick(args);

            if (!args.passEventToBack)
                break;
        }
        if (!hasChildren() || args.passEventToParent) {
            doubleClick();
            mouseDoubleClick(args);
        }
    }

    public void previousKeyDown(KeyEventArgs args) {
        if (!this.enabled)
            return;

        keyDown(args);
        if (args.passEventToParent && this.parent != null) {
            parent.previousKeyDown(args);
        }
    }

    public void previousKeyUp(KeyEventArgs args) {
        if (!this.enabled)
            return;

        keyUp(args);
        if (args.passEventToParent && this.parent != null) {
            parent.previousKeyUp(args);
        }
    }

    public void previousKeyPress(KeyEventArgs args) {
        if (!this.enabled)
            return;

        keyPress(args);
        if (args.passEventToParent && this.parent != null) {
            parent.previousKeyPress(args);
        }
    }

    public void previousUpdateScreen() {
        updateScreen();
        for (GuiWidget widget : widgets) {
            widget.previousUpdateScreen();
        }
    }

    public void previousOnGuiClosed() {
        onGuiClosed();
        for (GuiWidget widget : widgets) {
            widget.previousOnGuiClosed();
        }
    }

    public void drawStringWithSize(FontRenderer fontRenderer, String text, double size, int x, int y, int color) {
        GL11.glPushMatrix();
        GL11.glScaled(size, size, 0);
        fontRenderer.drawString(text, (int)(x / size), (int)(y / size), color);
        GL11.glPopMatrix();
    }

    public void drawStringWithFont(FontRenderer fontRenderer, String text, Font font, int x, int y, int color) {

    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }

    public void setSize(int width, int height) {
        setSize(new Dimension(width, height));
    }

    public void setSize(Dimension size) {
        this.width = size.width;
        this.height = size.height;

        if (this.minimumSize != null) {
            if (this.width < this.minimumSize.width)
                this.width = this.minimumSize.width;
            if (this.height < this.minimumSize.height)
                this.height = this.minimumSize.height;
        }

        if (this.maximumSize != null) {
            if (this.width > this.maximumSize.width)
                this.width = this.maximumSize.width;
            if (this.height > this.maximumSize.height)
                this.height = this.maximumSize.height;
        }
    }

    public Point getLocation() {
        return new Point(this.x, this.y);
    }

    public void setLocation(int x, int y) {
        setLocation(new Point(x, y));
    }

    public void setLocation(Point location) {
        this.x = location.x;
        this.y = location.y;
    }

    public Rectangle getRectangle() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    public void setRectangle(int x, int y, int width, int height) {
        setRectangle(new Rectangle(x, y, width, height));
    }

    public void setRectangle(Rectangle rectangle) {
        this.x = rectangle.x;
        this.y = rectangle.y;
        this.width = rectangle.width;
        this.height = rectangle.height;
    }

    public int getLeft() {
        return this.x;
    }

    public void setLeft(int left) {
        int right = getRight();
        this.x = left;
        setRight(right);
    }

    public int getTop() {
        return this.y;
    }

    public void setTop(int top) {
        int bottom = getBottom();
        this.y = top;
        setBottom(bottom);
    }

    public int getRight() {
        return this.x + this.width;
    }

    public void setRight(int right) {
        this.width = right - this.x;
    }

    public int getBottom() {
        return this.y + this.height;
    }

    public void setBottom(int bottom) {
        this.height = bottom - this.y;
    }
}
