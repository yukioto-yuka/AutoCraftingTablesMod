package com.yukioto_it.auto_crafting_tables.tileentity;

import com.yukioto_it.auto_crafting_tables.inventory.iinventory.InventoryReversingIngredient;
import com.yukioto_it.util.inventory.InventoryDecreaseCommand;
import com.yukioto_it.util.inventory.InventoryItemHelper;
import com.yukioto_it.util.inventory.InventoryMergeCommand;
import com.yukioto_it.util.inventory.InventoryMoveCommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

public class TileEntityAutoReverseWorkbench extends TileEntityLockable implements ISidedInventory, ITickable {
    public final int outputSlotSize = 18;
    private NonNullList<ItemStack> stackList = NonNullList.withSize(1 + outputSlotSize, ItemStack.EMPTY);
    public InventoryReversingIngredient inventoryIngredient = new InventoryReversingIngredient(this);
    private boolean isWorking = false;
    protected String customName;

    @Override
    public int getSizeInventory() {
        return stackList.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : stackList) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return stackList.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(stackList, index, count);

        if (!stack.isEmpty())
            markDirty();

        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(stackList, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        stackList.set(index, stack);

        int limit = getInventoryStackLimit();
        if (stack.getCount() > limit)
            stack.setCount(limit);

        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this)
            return false;

        double x = pos.getX() + 0.5d;
        double y = pos.getY() + 0.5d;
        double z = pos.getZ() + 0.5d;
        return player.getDistanceSq(x, y, z) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 0) {
            return inventoryIngredient.isInputItemValid(stack);
        }
        return true;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return this.isWorking ? 1 : 0;
        }
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.isWorking = (value != 0);
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public void clear() {
        stackList.clear();
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return null;
    }

    @Override
    public String getGuiID() {
        return null;
    }

    @Override
    public String getName() {
        return hasCustomName() ? customName : "container.auto_reverse_workbench";
    }

    public void setCustomName(String name)
    {
        this.customName = name;
    }

    @Override
    public boolean hasCustomName() {
        return customName != null && !customName.isEmpty();
    }

    public ITextComponent getDisplayName() {
        String name = getName();
        if (hasCustomName()) {
            return new TextComponentString(name);
        }
        else {
            return new TextComponentTranslation(name);
        }
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        stackList = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, stackList);
        inventoryIngredient.readFromNBT(compound);

        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, stackList);
        inventoryIngredient.writeToNBT(compound);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", customName);
        }
        return compound;
    }

    @Override
    public void update() {
        if (world.isRemote)
            return;

        isWorking = reverseItemStack();
    }

    private boolean reverseItemStack() {
        ItemStack inputItem = stackList.get(0);
        if (inputItem.isEmpty())
            return false;

        if (!isItemValidForSlot(0, inputItem)) {
            InventoryItemHelper.executeCommandToInventory(
                    this,
                    new InventoryMoveCommand(0),
                    1, getSizeInventory(), false);
            return false;
        }

        if (inventoryIngredient.getRecipe() == null)
            return false;

        InventoryItemHelper helper = new InventoryItemHelper(this);

        ItemStack result = inventoryIngredient.craftingResult;
        if (!helper.executeCommandToInventory(
                new InventoryDecreaseCommand(true, result),
                0))
            return false;

        NonNullList<ItemStack> remainingItems = inventoryIngredient.remainingItems;
        if (!helper.executeCommandToInventory(
                new InventoryDecreaseCommand(true, remainingItems),
                1, getSizeInventory(), false))
            return false;

        for (int i = 0; i < inventoryIngredient.getSizeInventory(); i++) {
            ItemStack stack = inventoryIngredient.getStackInSlot(i);
            if (stack.isEmpty())
                continue;
            if (!helper.executeCommandToInventory(
                    new InventoryMergeCommand(true, stack),
                    1, getSizeInventory(), false))
                return false;
        }

        helper.setToInventory();
        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        int[] result = new int[getSizeInventory()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }
        return result;
    }

    public boolean isRemainingItem(ItemStack stack) {
        for (ItemStack itemStack : inventoryIngredient.remainingItems) {
            if (InventoryReversingIngredient.isItemsEqual(stack, itemStack))
                return true;
        }
        return false;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        if (index == 0)
            return isItemValidForSlot(index, itemStackIn);
        if (isRemainingItem(itemStackIn))
            return true;
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (index == 0)
            return false;
        if (isRemainingItem(stack))
            return false;
        return true;
    }

    IItemHandler handler = new SidedInvWrapper(this, EnumFacing.WEST);

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T)handler;
        }
        return super.getCapability(capability, facing);
    }
}
