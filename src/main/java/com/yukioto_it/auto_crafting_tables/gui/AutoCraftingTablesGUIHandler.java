package com.yukioto_it.auto_crafting_tables.gui;

import com.yukioto_it.auto_crafting_tables.inventory.container.ContainerAutoReverseWorkbench;
import com.yukioto_it.auto_crafting_tables.tileentity.TileEntityAutoReverseWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class AutoCraftingTablesGUIHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);

        switch (ID) {
            case 0:

                break;
            case 1:
                if (tileEntity instanceof TileEntityAutoReverseWorkbench)
                    return new ContainerAutoReverseWorkbench((TileEntityAutoReverseWorkbench)tileEntity, player);
                break;
            case 2:

                break;
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);

//        ITextComponent displayName = tileEntity.getDisplayName();

        switch (ID) {
            case 0:

                break;
            case 1:
                if (tileEntity instanceof TileEntityAutoReverseWorkbench) {
                    TileEntityAutoReverseWorkbench tile = (TileEntityAutoReverseWorkbench)tileEntity;
                    Container container = new ContainerAutoReverseWorkbench(tile, player);
                    return new GuiAutoReverseWorkbench(container, tile);
                }
                break;
            case 2:

                break;
        }
        return null;
    }
}
