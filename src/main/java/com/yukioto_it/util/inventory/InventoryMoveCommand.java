package com.yukioto_it.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryMoveCommand implements IInventoryCommand {
    private final int startIndex;
    private final int endIndex;

    public InventoryMoveCommand(int moveIndex) {
        this.startIndex = moveIndex;
        this.endIndex = moveIndex + 1;
    }

    public InventoryMoveCommand(int moveStartIndex, int moveEndIndex) {
        this.startIndex = moveStartIndex;
        this.endIndex = moveEndIndex;
    }

    @Override
    public boolean execute(IndexedItemStack[] targetStacks, IndexedItemStack[] inventoryStacks, IInventory inventory) {
        NonNullList<ItemStack> moveStacks = NonNullList.create();
        for (int i = startIndex; i < endIndex; i++) {
            moveStacks.add(inventoryStacks[i].itemStack);
        }

        InventoryMergeCommand command = new InventoryMergeCommand(false, moveStacks);
        return command.execute(targetStacks, inventoryStacks, inventory);
    }
}
