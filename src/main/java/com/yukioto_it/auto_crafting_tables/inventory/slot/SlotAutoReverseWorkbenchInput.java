package com.yukioto_it.auto_crafting_tables.inventory.slot;

import com.yukioto_it.auto_crafting_tables.tileentity.TileEntityAutoReverseWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotAutoReverseWorkbenchInput extends Slot {
    private final TileEntityAutoReverseWorkbench tileWorkbench;

    public SlotAutoReverseWorkbenchInput(TileEntityAutoReverseWorkbench inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);

        this.tileWorkbench = inventoryIn;
    }

    public boolean isItemValid(ItemStack stack) {
        return tileWorkbench.isItemValidForSlot(getSlotIndex(), stack);
    }
}
