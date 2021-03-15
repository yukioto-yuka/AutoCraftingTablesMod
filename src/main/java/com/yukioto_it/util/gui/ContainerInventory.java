package com.yukioto_it.util.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

public abstract class ContainerInventory extends Container {
    public static final int slotSize = GUIPainter.slotSize;
    protected static final int numColumns = 9;

    protected final InventoryPlayer playerInventory;
    public int playerInventorySlotStart;
    public int playerInventoryHotbarSlotStart;

    private final IInventory[] inventories;
    private final int[] fields;

    public ContainerInventory(InventoryPlayer playerInventory, IInventory... inventories) {
        this.playerInventory = playerInventory;

        this.inventories = inventories;
        int fieldCount = 0;
        for (IInventory inventory : inventories) {
            fieldCount += inventory.getFieldCount();
        }
        fields = new int[fieldCount];

        initSlot(inventories);
        addPlayerInventory();
    }

    protected void initSlot(IInventory[] inventories) {

    }

    protected Point getPlayerInventoryPos() {
        int bottom = 0;
        for (Slot slot : inventorySlots) {
            if (slot.yPos > bottom)
                bottom = slot.yPos;
        }
        bottom += slotSize + 14;
        return new Point(8, bottom);
    }

    private void addPlayerInventory() {
        playerInventorySlotStart = inventorySlots.size();
        playerInventoryHotbarSlotStart = playerInventorySlotStart + (numColumns * 3);

        Point pos = getPlayerInventoryPos();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < numColumns; x++) {
                addSlotToContainer(
                    new Slot(
                        playerInventory,
                        numColumns + (y * numColumns) + x,
                        pos.x + (x * slotSize),
                        pos.y + (y * slotSize)
                    )
                );
            }
        }

        int hotbarSlotY = pos.y + (slotSize * 3) + 4;

        for (int x = 0; x < numColumns; x++) {
            addSlotToContainer(
                new Slot(
                        playerInventory,
                        x,
                        pos.x + (x * slotSize),
                        hotbarSlotY
                )
            );
        }
    }

    public void addListener(IContainerListener listener) {
        super.addListener(listener);

        int index = 0;
        for (IInventory inventory : inventories) {
            for (int i = 0; i < inventory.getFieldCount(); i++) {
                listener.sendWindowProperty(this, index, inventory.getField(i));
                index++;
            }
        }
    }

    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        int index = 0;
        for (IInventory inventory : inventories) {
            for (int i = 0; i < inventory.getFieldCount(); i++) {
                int newField = inventory.getField(i);
                if (fields[index] != newField) {
                    for (IContainerListener listener : listeners) {
                        listener.sendWindowProperty(this, index, newField);
                    }
                }
                fields[index] = newField;
                index++;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        int index = id;
        for (IInventory inventory : inventories) {
            if (index >= inventory.getFieldCount()) {
                index -= inventory.getFieldCount();
                continue;
            }
            inventory.setField(index, data);
        }
    }

    public InventoryPlayer getPlayerInventory() {
        return playerInventory;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack())
            return ItemStack.EMPTY;

        ItemStack itemStack = slot.getStack();
        ItemStack result = itemStack.copy();

        if (index < playerInventorySlotStart) {
            if (!mergeItemStackToPlayerInventory(itemStack, true))
                return ItemStack.EMPTY;
        }
        else if (!mergeItemStack(itemStack, 0, playerInventorySlotStart, false))
            return ItemStack.EMPTY;

        if (itemStack.isEmpty())
            slot.putStack(ItemStack.EMPTY);
        else
            slot.onSlotChanged();

        if (itemStack.getCount() == result.getCount())
            return ItemStack.EMPTY;

        return result;
    }

    protected boolean mergeItemStackToPlayerInventory(ItemStack stack, boolean reverseDirection) {
        return mergeItemStack(stack, playerInventorySlotStart, inventorySlots.size(), reverseDirection);
    }
}
