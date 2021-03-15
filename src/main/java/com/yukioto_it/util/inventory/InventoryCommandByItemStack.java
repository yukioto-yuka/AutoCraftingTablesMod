package com.yukioto_it.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public abstract class InventoryCommandByItemStack implements IInventoryCommand {
    protected final NonNullList<ItemStack> usedItemStacks;
    protected final boolean useCopy;

    public InventoryCommandByItemStack(boolean useCopy, ItemStack... itemStack) {
        this.usedItemStacks = NonNullList.from(ItemStack.EMPTY, itemStack);
        this.useCopy = useCopy;
    }

    public InventoryCommandByItemStack(boolean useCopy, NonNullList<ItemStack> itemStacks) {
        this.usedItemStacks = itemStacks;
        this.useCopy = useCopy;
    }

    @Override
    public boolean execute(IndexedItemStack[] targetStacks, IndexedItemStack[] inventoryStacks, IInventory inventory) {
        for (ItemStack stack : usedItemStacks) {
            ItemStack usedStack = (useCopy) ? stack.copy() : stack;

            boolean result = execute(usedStack, targetStacks, inventoryStacks, inventory);
            if (!result)
                return false;
        }
        return true;
    }

    protected abstract boolean execute(ItemStack itemStack, IndexedItemStack[] targetStacks, IndexedItemStack[] inventoryStacks, IInventory inventory);
}
