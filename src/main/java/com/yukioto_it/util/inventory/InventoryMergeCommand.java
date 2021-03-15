package com.yukioto_it.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryMergeCommand extends InventoryCommandByItemStack {

    public InventoryMergeCommand(boolean useCopy, ItemStack... mergeItemStack) {
        super(useCopy, mergeItemStack);
    }

    public InventoryMergeCommand(boolean useCopy, NonNullList<ItemStack> mergeItemStacks) {
        super(useCopy, mergeItemStacks);
    }

    @Override
    protected boolean execute(ItemStack itemStack, IndexedItemStack[] targetStacks, IndexedItemStack[] inventoryStacks, IInventory inventory) {
        if (itemStack.isEmpty())
            return true;

        if (growSlotItem(itemStack, targetStacks, inventory))
            return true;

        if (addToSlot(itemStack, targetStacks, inventory))
            return true;

        return false;
    }

    private boolean growSlotItem(ItemStack stack, IndexedItemStack[] targetStacks, IInventory inventory) {
        if (!stack.isStackable())
            return false;

        for (IndexedItemStack targetStack : targetStacks) {
            ItemStack inventoryStack = targetStack.itemStack;
            if (InventoryItemHelper.canMergeItems(stack, inventoryStack)) {
                int stackSize = inventoryStack.getCount() + stack.getCount();
                int maxSize = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());

                if (stackSize > maxSize) {
                    stack.setCount(stackSize - maxSize);
                    inventoryStack.setCount(maxSize);
                } else {
                    stack.setCount(0);
                    inventoryStack.setCount(stackSize);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean addToSlot(ItemStack stack, IndexedItemStack[] targetStacks, IInventory inventory) {
        if (stack.isEmpty())
            return true;

        for (IndexedItemStack targetStack : targetStacks) {
            if (targetStack.itemStack.isEmpty() && inventory.isItemValidForSlot(targetStack.index, stack)) {
                if (stack.getCount() > inventory.getInventoryStackLimit()) {
                    targetStack.itemStack = stack.splitStack(inventory.getInventoryStackLimit());
                }
                else {
                    targetStack.itemStack = stack.copy();
                    stack.setCount(0);
                    return true;
                }
            }
        }
        return false;
    }
}
