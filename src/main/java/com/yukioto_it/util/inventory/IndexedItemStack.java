package com.yukioto_it.util.inventory;

import net.minecraft.item.ItemStack;

public class IndexedItemStack {
    public ItemStack itemStack;
    public int index;

    public IndexedItemStack(ItemStack stack, int index) {
        this.itemStack = stack;
        this.index = index;
    }
}
