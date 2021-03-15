package com.yukioto_it.auto_crafting_tables.inventory.iinventory;

import com.yukioto_it.auto_crafting_tables.tileentity.TileEntityAutoReverseWorkbench;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.crafting.IShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class InventoryReversingIngredient extends InventoryCrafting {
    private final TileEntityAutoReverseWorkbench tileWorkbench;
    protected final int inventoryWidth = 3;
    protected final int inventoryHeight = 3;

    protected List<IRecipe> matchingRecipes = new ArrayList<>();
    public int ingredientIndex = 0;
    public int recipeIndex = 0;

    protected final NonNullList<ItemStack> stackList;

    protected IRecipe recipeUsed;
    public ItemStack craftingResult = ItemStack.EMPTY;
    public NonNullList<ItemStack> remainingItems = NonNullList.create();

    public InventoryReversingIngredient(TileEntityAutoReverseWorkbench tileEntity) {
        super(null, 0, 0);
        stackList = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        this.tileWorkbench = tileEntity;
    }

    @Override
    public int getSizeInventory() {
        return inventoryWidth * inventoryHeight;
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
    public ItemStack getStackInRowAndColumn(int row, int column) {
        if (row < 0 || inventoryWidth <= row)
            return ItemStack.EMPTY;
        if (column < 0 || inventoryHeight <= column)
            return ItemStack.EMPTY;

        return getStackInSlot(row + column * inventoryWidth);
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
    public void markDirty() {
        tileWorkbench.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        stackList.clear();
    }

    @Override
    public int getHeight()
    {
        return inventoryHeight;
    }

    @Override
    public int getWidth()
    {
        return inventoryWidth;
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper)
    {
        for (ItemStack itemstack : stackList)
        {
            helper.accountStack(itemstack);
        }
    }

    public void readFromNBT(NBTTagCompound compound) {
        resetIndex();
        clearRecipe();

        if (compound.hasKey("RecipeIndex", 3)) {
            recipeIndex = compound.getInteger("RecipeIndex");
        }
        if (compound.hasKey("IngredientIndex", 3)) {
            ingredientIndex = compound.getInteger("IngredientIndex");
        }

        if (compound.hasKey("RecipeIngredients", 10)) {
            NBTTagCompound recipeCompound = compound.getCompoundTag("RecipeIngredients");
            ItemStackHelper.loadAllItems(recipeCompound, stackList);
            onInventoryChanged();
            setMatchingRecipesFromRecipeUsed();
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("RecipeIndex", recipeIndex);
        compound.setInteger("IngredientIndex", ingredientIndex);

        if (!isEmpty()) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            ItemStackHelper.saveAllItems(nbtTagCompound, stackList);
            compound.setTag("RecipeIngredients", nbtTagCompound);
        }
        return compound;
    }

    public static boolean isItemsEqual(ItemStack stackA, ItemStack stackB) {
        return ItemStack.areItemsEqual(stackA, stackB);
    }

    public boolean isInputItemValid(ItemStack stack) {
        if (recipeUsed == null)
            return false;

        return isItemsEqual(stack, craftingResult);
    }

    public IRecipe getRecipe() {
        return recipeUsed;
    }

    public void setRecipe(ItemStack stack) {
        resetIndex();
        clearRecipe();

        if (stack.isEmpty())
            return;

        setMatchingRecipes(stack);
        setRecipeFromIndex();
    }

    protected void setMatchingRecipes(ItemStack stack) {
        for (IRecipe recipe : CraftingManager.REGISTRY) {
            ItemStack result = recipe.getRecipeOutput();
            if (isItemsEqual(stack, result)) {
                matchingRecipes.add(recipe);
            }
        }
    }

    public void setItemStack(int index, ItemStack stack) {
        resetIndex();

        ItemStack setStack = stack.copy();
        setStack.setCount(1);

        setInventorySlotContents(index, setStack);
        onInventoryChanged();
        setMatchingRecipesFromRecipeUsed();
    }

    protected void setMatchingRecipesFromRecipeUsed() {
        if (recipeUsed == null)
            return;

        ItemStack output = recipeUsed.getRecipeOutput();
        if (output.isEmpty()) {
            matchingRecipes.add(recipeUsed);
        }
        else {
            setMatchingRecipes(output);

            recipeIndex = 0;
            for (int i = 0; i < matchingRecipes.size(); i++) {
                if (matchingRecipes.get(i) == recipeUsed) {
                    recipeIndex = i;
                    break;
                }
            }
        }
    }

    public void resetIndex() {
        matchingRecipes = new ArrayList<>();
        recipeIndex = 0;
        ingredientIndex = 0;
    }

    public void setRecipeFromIndex() {
        if (matchingRecipes.size() == 0)
            return;

        int index = getRegularIndex(recipeIndex, matchingRecipes.size());
        IRecipe recipe = matchingRecipes.get(index);

        setInventoryFromRecipe(recipe);
        onInventoryChanged();
    }


    public void onInventoryChanged() {
        recipeUsed = CraftingManager.findMatchingRecipe(this, tileWorkbench.getWorld());
        setRecipeElements();
    }

    public void clearRecipe() {
        clear();
        recipeUsed = null;
        setRecipeElements();
    }

    protected void setRecipeElements() {
        if (recipeUsed == null) {
            craftingResult = ItemStack.EMPTY;
            remainingItems = NonNullList.create();
        }
        else {
            craftingResult = recipeUsed.getCraftingResult(this);
            remainingItems = recipeUsed.getRemainingItems(this);
        }
    }


    public void nextRecipe() {
        if (recipeIndex >= Integer.MAX_VALUE)
            recipeIndex = 0;
        else recipeIndex++;
        ingredientIndex = 0;

        setRecipeFromIndex();
    }

    public void prevRecipe() {
        if (recipeIndex <= Integer.MIN_VALUE)
            recipeIndex = 0;
        else recipeIndex--;
        ingredientIndex = 0;

        setRecipeFromIndex();
    }

    public void nextIngredient() {
        if (ingredientIndex >= Integer.MAX_VALUE)
            ingredientIndex = 0;
        else ingredientIndex++;

        setRecipeFromIndex();
    }

    public void prevIngredient() {
        if (ingredientIndex <= Integer.MIN_VALUE)
            ingredientIndex = 0;
        else ingredientIndex--;

        setRecipeFromIndex();
    }

    protected void setInventoryFromRecipe(IRecipe recipe) {
        if (!this.isEmpty())
            clear();
        if (recipe == null)
            return;

        NonNullList<Ingredient> recipeItems = recipe.getIngredients();

        for (int x = 0; x < inventoryWidth; x++) {
            for (int y = 0; y < inventoryHeight; y++) {
                int slotIndex = x + y * inventoryWidth;

                Ingredient ingredient;
                if (recipe instanceof IShapedRecipe) {
                    IShapedRecipe shapedRecipe = (IShapedRecipe)recipe;

                    if (x >= shapedRecipe.getRecipeWidth() || y >= shapedRecipe.getRecipeHeight())
                        continue;

                    ingredient = recipeItems.get(x + y * shapedRecipe.getRecipeWidth());
                }
                else {
                    if (slotIndex >= recipeItems.size())
                        continue;

                    ingredient = recipeItems.get(slotIndex);
                }

                ItemStack[] stacks = ingredient.getMatchingStacks();
                if (stacks.length > 0) {
                    int index = getRegularIndex(ingredientIndex, stacks.length);
                    setInventorySlotContents(slotIndex, stacks[index].copy());
                }
            }
        }
    }

    protected int getRegularIndex(int index, int max) {
        index = index % max;
        while (index < 0) index += max;
        return index;
    }

    @Override
    public String getName() {
        return "ReversingIngredient";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        String name = getName();
        if (hasCustomName()) {
            return new TextComponentString(name);
        }
        else {
            return new TextComponentTranslation(name);
        }
    }
}
