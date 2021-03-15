package com.yukioto_it.auto_crafting_tables.inventory.container;

import com.yukioto_it.auto_crafting_tables.inventory.slot.SlotAutoReverseWorkbenchInput;
import com.yukioto_it.auto_crafting_tables.inventory.slot.SlotReversingIngredient;
import com.yukioto_it.auto_crafting_tables.tileentity.TileEntityAutoReverseWorkbench;
import com.yukioto_it.util.gui.ContainerInventory;
import com.yukioto_it.util.inventory.container.IGuiEventContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ContainerAutoReverseWorkbench extends ContainerInventory implements IGuiEventContainer {
    private final TileEntityAutoReverseWorkbench tileWorkbench;
    public ContainerAutoReverseWorkbench(TileEntityAutoReverseWorkbench tileEntity, EntityPlayer player) {
        super(player.inventory, tileEntity);

        tileWorkbench = tileEntity;
    }

    @Override
    protected void initSlot(IInventory[] inventories) {
        TileEntityAutoReverseWorkbench tile = (TileEntityAutoReverseWorkbench)inventories[0];

        addSlotToContainer(new SlotAutoReverseWorkbenchInput(tile, 0, 124, 35));

        int matrixSize = 3;
        for (int y = 0; y < matrixSize; y++) {
            for (int x = 0; x < matrixSize; x++) {
                addSlotToContainer(new SlotReversingIngredient(tile.inventoryIngredient, x + y * matrixSize, 30 + x * slotSize, 17 + y * slotSize));
            }
        }

        int numColumns = 9;
        int numRows = tile.outputSlotSize / numColumns;
        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < numColumns; x++) {
                addSlotToContainer(new Slot(
                        tile, x + y * numColumns + 1, 8 + x * slotSize, 17 + (matrixSize * slotSize) + 20 + y * slotSize
                ));
            }
        }
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (0 <= slotId && slotId < inventorySlots.size()) {
            Slot slot = inventorySlots.get(slotId);
            if (slot != null && slot.inventory == tileWorkbench.inventoryIngredient) {
                ItemStack playerItem = player.inventory.getItemStack();
                if (clickTypeIn == ClickType.PICKUP) {
                    tileWorkbench.inventoryIngredient.setRecipe(playerItem);
                }
                else if (clickTypeIn == ClickType.QUICK_MOVE) {
                    tileWorkbench.inventoryIngredient.setItemStack(slot.getSlotIndex(), playerItem);
                }
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setAll(List<ItemStack> itemStacks) {
        super.setAll(itemStacks);
        tileWorkbench.inventoryIngredient.onInventoryChanged();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack())
            return ItemStack.EMPTY;

        ItemStack itemStack = slot.getStack();
        ItemStack result = itemStack.copy();

        if (index == 0) {
            if (!mergeItemStackToPlayerInventory(itemStack, false))
                return ItemStack.EMPTY;
        }
        else if (index <= 9) {
            return ItemStack.EMPTY;
        }
        else if (index < playerInventorySlotStart) {
            if (!mergeItemStackToPlayerInventory(itemStack, true))
                return ItemStack.EMPTY;
        }
        else {
            if (inventorySlots.get(0).isItemValid(itemStack)) {
                if (!mergeItemStack(itemStack, 0, 1, false))
                    return ItemStack.EMPTY;
            }
            else if (playerInventorySlotStart <= index && index < playerInventoryHotbarSlotStart) {
                if (!mergeItemStack(itemStack, playerInventoryHotbarSlotStart, inventorySlots.size(), false))
                    return ItemStack.EMPTY;
            }
            else if (playerInventoryHotbarSlotStart <= index && index < inventorySlots.size()) {
                if (!mergeItemStack(itemStack, playerInventorySlotStart, playerInventoryHotbarSlotStart, false))
                    return ItemStack.EMPTY;
            }
        }

        if (itemStack.isEmpty())
            slot.putStack(ItemStack.EMPTY);
        else
            slot.onSlotChanged();

        if (itemStack.getCount() == result.getCount())
            return ItemStack.EMPTY;

        return result;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileWorkbench.isUsableByPlayer(playerIn);
    }

    @Override
    public void onEventRaised(int eventID) {
        switch (eventID) {
            case 1:
                tileWorkbench.inventoryIngredient.nextRecipe();
                break;
            case 2:
                tileWorkbench.inventoryIngredient.prevRecipe();
                break;
            case 3:
                tileWorkbench.inventoryIngredient.nextIngredient();
                break;
            case 4:
                tileWorkbench.inventoryIngredient.prevIngredient();
                break;
        }
    }
}
