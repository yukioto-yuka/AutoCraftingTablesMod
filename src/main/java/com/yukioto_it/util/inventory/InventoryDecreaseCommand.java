package com.yukioto_it.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryDecreaseCommand extends InventoryCommandByItemStack {

    public InventoryDecreaseCommand(boolean useCopy, ItemStack... decreaseItemStack) {
        super(useCopy, decreaseItemStack);
    }

    public InventoryDecreaseCommand(boolean useCopy, NonNullList<ItemStack> decreaseItemStacks) {
        super(useCopy, decreaseItemStacks);
    }

    @Override
    protected boolean execute(ItemStack itemStack, IndexedItemStack[] targetStacks, IndexedItemStack[] inventoryStacks, IInventory inventory) {
        if (itemStack.isEmpty())
            return true;

        for (IndexedItemStack targetStack : targetStacks) {
            ItemStack inventoryStack = targetStack.itemStack;
            if (InventoryItemHelper.canMergeItems(itemStack, inventoryStack)) {
                int stackSize = inventoryStack.getCount() - itemStack.getCount();
                if (stackSize < 0) {
                    itemStack.shrink(inventoryStack.getCount());
                    targetStack.itemStack = ItemStack.EMPTY;
                }
                else {
                    inventoryStack.setCount(stackSize);
                    itemStack.setCount(0);
                    return true;
                }
            }
        }
        return false;
    }
}
