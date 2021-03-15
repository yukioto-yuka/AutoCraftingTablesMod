package com.yukioto_it.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryItemHelper {
    private final IInventory inventory;
    private final IndexedItemStack[] copiedStacks;

    public InventoryItemHelper(IInventory inventory) {
        this.inventory = inventory;

        copiedStacks = new IndexedItemStack[inventory.getSizeInventory()];
        for (int i = 0; i < copiedStacks.length; i++) {
            ItemStack stack = inventory.getStackInSlot(i).copy();
            copiedStacks[i] = new IndexedItemStack(stack, i);
        }
    }

    public boolean executeCommandToInventory(IInventoryCommand command, int startIndex, int endIndex, boolean reverseDirection) {
        IndexedItemStack[] indexedStacks = getIndexedStacks(startIndex, endIndex, reverseDirection);
        return command.execute(indexedStacks, copiedStacks, inventory);
    }

    public boolean executeCommandToInventory(IInventoryCommand command, int index) {
        return executeCommandToInventory(command, index, index + 1, false);
    }

    public static boolean executeCommandToInventory(IInventory inventory, IInventoryCommand command, int startIndex, int endIndex, boolean reverseDirection) {
        InventoryItemHelper helper = new InventoryItemHelper(inventory);
        boolean result = helper.executeCommandToInventory(command, startIndex, endIndex, reverseDirection);
        if (!result)
            return false;

        helper.setToInventory();
        return true;
    }

    public static boolean executeCommandToInventory(IInventory inventory, IInventoryCommand command, int index) {
        return executeCommandToInventory(inventory, command, index, index + 1, false);
    }

    public void setToInventory() {
        for (IndexedItemStack stack : copiedStacks) {
            inventory.setInventorySlotContents(stack.index, stack.itemStack);
        }
    }

    public IndexedItemStack[] getIndexedStacks(int startIndex, int endIndex, boolean reverseDirection) {
        IndexedItemStack[] result = new IndexedItemStack[endIndex - startIndex];
        int index = 0;
        if (!reverseDirection) {
            for (int i = startIndex; i < endIndex; i++) {
                result[index] = copiedStacks[i];
                index++;
            }
        }
        else {
            for (int i = endIndex - 1; i >= startIndex; i--) {
                result[index] = copiedStacks[i];
                index++;
            }
        }
        return result;
    }

    public static boolean canMergeItems(ItemStack stackA, ItemStack stackB) {
        if (stackA.isEmpty() || stackB.isEmpty())
            return false;

        if (!ItemStack.areItemsEqual(stackA, stackB))
            return false;

        return ItemStack.areItemStackTagsEqual(stackA, stackB);
    }

}
