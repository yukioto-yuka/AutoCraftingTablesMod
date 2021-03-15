package com.yukioto_it.auto_crafting_tables.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotReversingIngredient extends Slot {

    public SlotReversingIngredient(IInventory inventory, int index, int xPos, int yPos) {
        super(inventory, index, xPos, yPos);
    }

    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return false;
    }
}
