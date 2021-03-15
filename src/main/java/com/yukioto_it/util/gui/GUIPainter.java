package com.yukioto_it.util.gui;

import com.yukioto_it.util.gui.widgets.GuiWidget;
import com.yukioto_it.util.gui.widgets.KeyEventArgs;
import com.yukioto_it.util.gui.widgets.MouseEventArgs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUIPainter extends GuiContainer {
    public static final int slotSize = 18;
    public static final int doubleClickInterval = 500;
    private static final ResourceLocation FRAME_GUI_TEXTURE  = new ResourceLocation("util", "textures/gui/container/frame.png");
    private static final ResourceLocation WIDGET_GUI_TEXTURE = new ResourceLocation("util", "textures/gui/container/widgets.png");

    protected String displayName;
    protected FrameGuiTexture frameTexture;
    protected WidgetGuiTexture widgetTexture;
    protected List<GuiWidget> widgets = new ArrayList<>();
    protected GuiWidget selectedWidget = null;

    protected List<Integer> pressingKeys = new ArrayList<>();
    protected boolean isEventButtonDown = true;
    protected int eventButton = 1;
    protected long lastMouseClick = -1;
    protected int lastMouseButton = 0;
    protected int clickCount = 0;

    private final List<Integer> splitFaceWidth = new ArrayList<>();
    private final List<Integer> splitFaceHeight = new ArrayList<>();

    public GUIPainter(Container container) {
        this(container, null);
    }

    public GUIPainter(Container container, String displayName) {
        super(container);
        this.displayName = displayName;

        initialize();
    }

    protected void initialize() {
        frameTexture = new FrameGuiTexture(FRAME_GUI_TEXTURE, 200, 4);
        widgetTexture = new WidgetGuiTexture(WIDGET_GUI_TEXTURE, slotSize);

        setGUISize(inventorySlots);
        setSplitFaceSize();
    }

    public void initGui()
    {
        super.initGui();
        this.widgets.clear();
    }

    protected void setGUISize(Container container) {
        List<Slot> slots = container.inventorySlots;
        xSize = 0;
        ySize = 0;
        for (Slot slot : slots) {
            if (slot.xPos > xSize)
                xSize = slot.xPos;
            if (slot.yPos > ySize)
                ySize = slot.yPos;
        }
        xSize = (xSize - 1) + slotSize + 7;
        ySize = (ySize - 1) + slotSize + 7;
    }

    public void addWidget(GuiWidget widget) {
        widget.screen = this;
        this.widgets.add(widget);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        for (GuiWidget widget : widgets) {
            widget.previousDrawWidget(mouseX, mouseY, partialTicks);
        }
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();

        for (GuiWidget widget : widgets) {
            widget.previousDrawWidgetForegroundLayer(mouseX, mouseY);
        }

        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        double xScale = (double)(this.width) / (double)(this.mc.displayWidth);
        double yScale = (double)(this.height) / (double)(this.mc.displayHeight);
        int x = (int)(Mouse.getEventX() * xScale);
        int y = (int)(Mouse.getEventY() * yScale);
        y = (this.height - 1) - y;  // Invert Y
        int button = Mouse.getEventButton();
        boolean isButtonDown = Mouse.getEventButtonState();
        int deltaWheel = Mouse.getEventDWheel();
        long time = Mouse.getEventNanoseconds();

        boolean isButtonEvent;
        if (button == -1) {
            isButtonEvent = false;
            button = this.eventButton;
            isButtonDown = this.isEventButtonDown;
        }
        else {
            isButtonEvent = true;
            this.eventButton = button;
            this.isEventButtonDown = isButtonDown;
        }


        boolean isClick = isButtonEvent && !isButtonDown;
        int nextClickCount = isClick ? 1 : 0;
        if (this.lastMouseClick > 0) {
            long interval = Minecraft.getSystemTime() - this.lastMouseClick;

            if (interval <= doubleClickInterval) {
                nextClickCount = this.clickCount;
                if (isClick && button == lastMouseButton) {
                    nextClickCount++;
                }
            }
        }
        if (isClick) {
            this.lastMouseButton = button;
            this.lastMouseClick = Minecraft.getSystemTime();
        }
        this.clickCount = nextClickCount;


        MouseEventArgs args = new MouseEventArgs(x, y, button, this.clickCount, deltaWheel, time);
        if (deltaWheel != 0) {
            mouseWheel(args);
        }
        if (isButtonEvent) {
            if (isButtonDown) {
                mouseDown(args);
            }
            else {
                if (this.clickCount >= 2) {
                    doubleClick();
                    mouseDoubleClick(args);
                }
                else {
                    click();
                    mouseClick(args);
                }
                mouseUp(args);
            }
        }
        else {
            if (isButtonDown) {
                mouseClickMove(args);
            }
            mouseMove(args);
        }


        for (int i = widgets.size() - 1; i >= 0; i--) {
            GuiWidget widget = widgets.get(i);
            if (!widget.getRectangle().contains(x, y))
                continue;

            if (deltaWheel != 0) {
                widget.previousMouseWheel(args);
            }
            if (isButtonEvent) {
                if (isButtonDown) {
                    this.selectedWidget = widget.previousMouseDown(args);
                }
                else {
                    if (this.clickCount >= 2) {
                        widget.previousMouseDoubleClick(args);
                    }
                    else {
                        widget.previousMouseClick(args);
                    }
                    widget.previousMouseUp(args);
                }
            }
            else {
                if (isButtonDown) {
                    widget.previousMouseClickMove(args);
                }
                widget.previousMouseMove(args);
            }

            if (!args.passEventToBack)
                break;
        }
    }

    public void mouseDown(MouseEventArgs args) {

    }

    public void mouseUp(MouseEventArgs args) {

    }

    public void mouseWheel(MouseEventArgs args) {

    }

    public void mouseClick(MouseEventArgs args) {

    }

    public void mouseDoubleClick(MouseEventArgs args) {

    }

    public void mouseMove(MouseEventArgs args) {

    }

    public void mouseClickMove(MouseEventArgs args) {

    }

    protected void doubleClick() {

    }

    protected void click() {

    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();

        int keyCode = Keyboard.getEventKey();
        char keyChar = Keyboard.getEventCharacter();
        long time = Keyboard.getEventNanoseconds();
        boolean isKeyDown = Keyboard.getEventKeyState();
        KeyEventArgs args = new KeyEventArgs(keyCode, keyChar, time, pressingKeys);

        if (isKeyDown) {
            if (!pressingKeys.contains(keyCode)) {
                pressingKeys.add(keyCode);
                if (selectedWidget != null)
                    selectedWidget.previousKeyDown(args);
                this.keyDown(args);
            }

            if (selectedWidget != null)
                selectedWidget.previousKeyPress(args);
            this.KeyPress(args);
        }
        else {
            pressingKeys.remove((Integer)keyCode);
            if (selectedWidget != null)
                selectedWidget.previousKeyUp(args);
            this.keyUp(args);
        }
    }

    public void keyDown(KeyEventArgs args) {

    }

    public void keyUp(KeyEventArgs args) {

    }

    public void KeyPress(KeyEventArgs args) {

    }

    @Override
    public void updateScreen() {
        for (GuiWidget widget : widgets) {
            widget.previousUpdateScreen();
        }
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        for (GuiWidget widget : widgets) {
            widget.previousOnGuiClosed();
        }
        super.onGuiClosed();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (displayName == null)
            return;

        fontRenderer.drawString(displayName, 8, 6, 4210752);
        if (inventorySlots instanceof ContainerInventory) {
            ContainerInventory inventory = (ContainerInventory)inventorySlots;
            String s = inventory.getPlayerInventory().getDisplayName().getUnformattedText();
            Slot slot = inventory.inventorySlots.get(inventory.playerInventorySlotStart);
            fontRenderer.drawString(s, slot.xPos,  slot.yPos - 1 - 14 + 3, 4210752);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        drawGuiFrame();
        drawGuiWidget();
    }

    protected void drawGuiWidget() {
        this.mc.getTextureManager().bindTexture(widgetTexture.texture);
        int slotSize = widgetTexture.slotSize;

        List<Slot> slots = this.inventorySlots.inventorySlots;
        for (Slot slot : slots) {
            if (!slot.isEnabled())
                continue;

            drawTextureOnGui(slot.xPos - 1, slot.yPos - 1, 0, 0, slotSize, slotSize);
        }
    }

    private void drawGuiFrame() {
        this.mc.getTextureManager().bindTexture(frameTexture.texture);
        int borderSize = frameTexture.borderSize;

        int endX = xSize - borderSize;
        int endY = ySize - borderSize;

        // Draw Corner
        drawTextureOnGui(0, 0, borderSize, borderSize, borderSize, borderSize);
        drawTextureOnGui(endX, 0, 0, borderSize, borderSize, borderSize);
        drawTextureOnGui(0, endY, borderSize, 0, borderSize, borderSize);
        drawTextureOnGui(endX, endY, 0, 0, borderSize, borderSize);

        // Draw Border
        int borderX = borderSize;
        for (int width : splitFaceWidth) {
            drawTextureOnGui(borderX, 0, (borderSize * 2), borderSize, width, borderSize);
            drawTextureOnGui(borderX, endY, (borderSize * 2), 0, width, borderSize);
            borderX += width;
        }
        int borderY = borderSize;
        for (int height : splitFaceHeight) {
            drawTextureOnGui(0, borderY, borderSize, borderSize * 2, borderSize, height);
            drawTextureOnGui(endX, borderY, 0, borderSize * 2, borderSize, height);
            borderY += height;
        }

        // Draw Face
        int faceTexture = borderSize * 2;
        int faceY = borderSize;
        for (int height : splitFaceHeight) {
            int faceX = borderSize;
            for (int width : splitFaceWidth) {
                drawTextureOnGui(faceX, faceY, faceTexture, faceTexture, width, height);
                faceX += width;
            }
            faceY += height;
        }
    }

    private void setSplitFaceSize() {
        int borderSize = frameTexture.borderSize;
        int faceWidth = xSize - (borderSize * 2);
        int faceHeight = ySize - (borderSize * 2);
        int splitSize = 200;

        // set splitFaceWidth
        for (int i = 0; i < (faceWidth / splitSize); i++) {
            splitFaceWidth.add(splitSize);
        }
        if (faceWidth % splitSize != 0)
            splitFaceWidth.add(faceWidth % splitSize);

        // set splitFaceHeight
        for (int i = 0; i < (faceHeight / splitSize); i++) {
            splitFaceHeight.add(splitSize);
        }
        if (faceHeight % splitSize != 0)
            splitFaceHeight.add(faceHeight % splitSize);
    }

    public void drawTextureOnGui(int x, int y, int textureX, int textureY, int width, int height) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, textureX, textureY, width, height);
    }
}
