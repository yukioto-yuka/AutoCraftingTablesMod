package com.yukioto_it.auto_crafting_tables.gui;

import com.yukioto_it.auto_crafting_tables.tileentity.TileEntityAutoReverseWorkbench;
import com.yukioto_it.util.gui.GUIPainter;
import com.yukioto_it.util.gui.widgets.GuiWidgetButton;
import com.yukioto_it.util.gui.widgets.MouseEventArgs;
import com.yukioto_it.util.network.MainPacketHandler;
import com.yukioto_it.util.network.MessagePacketGuiEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiAutoReverseWorkbench extends GUIPainter {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("auto_crafting_tables", "textures/gui/container/workbench_widgets.png");

    private TileEntityAutoReverseWorkbench tileWorkbench;

    public GuiAutoReverseWorkbench(Container container, TileEntityAutoReverseWorkbench tileEntity) {
        super(container, tileEntity.getDisplayName().getUnformattedText());

        this.tileWorkbench = tileEntity;

    }

    public void initGui() {
        super.initGui();

        int x = guiLeft + 29;
        int y = guiTop + 71;
        int buttonHeight = 10;
        int textureY = 32;
        int hoverTextureY = 42;
        {
            GuiWidgetButton button = new GuiWidgetButton(x, y, 16, buttonHeight, "<<") {
                @Override
                protected void mouseDown(MouseEventArgs args) {
                    super.mouseDown(args);
                    tileWorkbench.inventoryIngredient.prevRecipe();
                    sendButtonEvent(2);
                }
            };
            button.setTexture(GUI_TEXTURE, new Point(0, textureY), new Dimension(16, 10));
            button.setHoverTexture(new Point(0, hoverTextureY));
            addWidget(button);
            x += 16;
        }
        {
            GuiWidgetButton button = new GuiWidgetButton(x, y, 10, buttonHeight, "<") {
                @Override
                protected void mouseDown(MouseEventArgs args) {
                    super.mouseDown(args);
                    tileWorkbench.inventoryIngredient.prevIngredient();
                    sendButtonEvent(4);
                }
            };
            button.setTexture(GUI_TEXTURE, new Point(0, textureY), new Dimension(17, 10));
            button.setHoverTexture(new Point(0, hoverTextureY));
            addWidget(button);
            x += 10 + 2;
        }
        {
            GuiWidgetButton button = new GuiWidgetButton(x, y, 10, buttonHeight, ">") {
                @Override
                protected void mouseDown(MouseEventArgs args) {
                    super.mouseDown(args);
                    tileWorkbench.inventoryIngredient.nextIngredient();
                    sendButtonEvent(3);
                }
            };
            button.setTexture(GUI_TEXTURE, new Point(0, textureY), new Dimension(17, 10));
            button.setHoverTexture(new Point(0, hoverTextureY));
            addWidget(button);
            x += 10;
        }
        {
            GuiWidgetButton button = new GuiWidgetButton(x, y, 16, buttonHeight, ">>") {
                @Override
                protected void mouseDown(MouseEventArgs args) {
                    super.mouseDown(args);
                    tileWorkbench.inventoryIngredient.nextRecipe();
                    sendButtonEvent(1);
                }
            };
            button.setTexture(GUI_TEXTURE, new Point(1, textureY), new Dimension(16, 10));
            button.setHoverTexture(new Point(1, hoverTextureY));
            addWidget(button);
        }
    }

    public void sendButtonEvent(int id) {
        IMessage message = new MessagePacketGuiEvent(id);
        MainPacketHandler.INSTANCE.sendToServer(message);
    }

    protected void drawGuiWidget() {
        super.drawGuiWidget();

        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int textureY = (tileWorkbench.getField(0) == 0) ? 0 : 16;
        drawTextureOnGui(89, 34, 0, textureY, 24, 16);

        drawItemStack();
    }

    private void drawItemStack() {
        ItemStack itemStack = tileWorkbench.inventoryIngredient.craftingResult;
        int x = guiLeft + 124;
        int y = guiTop + 35;

        if (itemStack.isEmpty())
            return;

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        RenderItem renderItem = mc.getRenderItem();
        renderItem.renderItemAndEffectIntoGUI(mc.player, itemStack, x, y);
        GlStateManager.depthFunc(GL11.GL_GREATER);
        Gui.drawRect(x, y, x + 16, y + 16, 822083583);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
    }
}