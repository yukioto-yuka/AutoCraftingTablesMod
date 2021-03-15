package com.yukioto_it.util.inventory;

import net.minecraft.inventory.IInventory;

public interface IInventoryCommand {
    boolean execute(IndexedItemStack[] targetStacks, IndexedItemStack[] inventoryStacks, IInventory inventory);
}
