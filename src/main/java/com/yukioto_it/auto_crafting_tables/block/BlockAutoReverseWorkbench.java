package com.yukioto_it.auto_crafting_tables.block;

import com.yukioto_it.auto_crafting_tables.AutoCraftingTablesMod;
import com.yukioto_it.auto_crafting_tables.tileentity.TileEntityAutoReverseWorkbench;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAutoReverseWorkbench extends Block implements ITileEntityProvider {
    public BlockAutoReverseWorkbench() {
        super(Material.WOOD);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
            return true;

        TileEntityAutoReverseWorkbench tileEntity = getTileEntity(worldIn, pos);
        if (tileEntity != null) {
            playerIn.openGui(AutoCraftingTablesMod.INSTANCE, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (stack.hasDisplayName()) {
            TileEntityAutoReverseWorkbench tileEntity = getTileEntity(worldIn, pos);
            if (tileEntity != null) {
                tileEntity.setCustomName(stack.getDisplayName());
            }
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityAutoReverseWorkbench tileEntity = getTileEntity(worldIn, pos);
        if (tileEntity != null) {
            InventoryHelper.dropInventoryItems(worldIn, pos, tileEntity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

    private TileEntityAutoReverseWorkbench getTileEntity(World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityAutoReverseWorkbench)
            return (TileEntityAutoReverseWorkbench)tileEntity;
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityAutoReverseWorkbench();
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }
}
