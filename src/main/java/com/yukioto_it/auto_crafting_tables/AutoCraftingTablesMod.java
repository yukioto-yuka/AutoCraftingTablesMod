package com.yukioto_it.auto_crafting_tables;

import com.yukioto_it.auto_crafting_tables.block.BlockAutoReverseWorkbench;
import com.yukioto_it.auto_crafting_tables.gui.AutoCraftingTablesGUIHandler;
import com.yukioto_it.auto_crafting_tables.tileentity.TileEntityAutoReverseWorkbench;
import com.yukioto_it.util.network.CustomMessagePacketManager;
import com.yukioto_it.util.network.MainPacketHandler;
import com.yukioto_it.util.network.MessagePacketCustom;
import com.yukioto_it.util.network.MessagePacketGuiEvent;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = AutoCraftingTablesMod.MOD_ID, name = AutoCraftingTablesMod.NAME, version = AutoCraftingTablesMod.VERSION)
public class AutoCraftingTablesMod {
    public static final String MOD_ID = "auto_crafting_tables";
    public static final String NAME = "Auto Crafting Tables Mod";
    public static final String VERSION = "1.0";

    @Mod.Instance(MOD_ID)
    public static AutoCraftingTablesMod INSTANCE;

    protected List<RegisteredItem> items = new ArrayList<>();
    protected List<RegisteredBlock> blocks = new ArrayList<>();
    protected List<RegisteredTileEntity> tileEntities = new ArrayList<>();

    public AutoCraftingTablesMod() {
        registerBlock(new RegisteredBlock(new BlockAutoReverseWorkbench(), "auto_reverse_workbench_block"));

        registerTileEntity(new RegisteredTileEntity(TileEntityAutoReverseWorkbench.class, "auto_reverse_workbench_block"));
    }

    public void registerItem(RegisteredItem item) {
        items.add(item);
    }

    public void registerBlock(RegisteredBlock block) {
        blocks.add(block);
    }

    public void registerTileEntity(RegisteredTileEntity tileEntity) {
        tileEntities.add(tileEntity);
    }

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MainPacketHandler.register(MessagePacketGuiEvent.GuiEventMessageHandler.class, MessagePacketGuiEvent.class, Side.SERVER);
        MainPacketHandler.register(CustomMessagePacketManager.CustomMessageHandler.class, MessagePacketCustom.class, Side.SERVER);
        MainPacketHandler.register(CustomMessagePacketManager.CustomMessageHandler.class, MessagePacketCustom.class, Side.CLIENT);
    }


    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        for (RegisteredItem item : items) {
            event.getRegistry().register(item.item);
        }

        for (RegisteredBlock block : blocks) {
            event.getRegistry().register(block.getItemBlock());
        }
    }


    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        for (RegisteredBlock block : blocks) {
            event.getRegistry().register(block.block);
        }

        for (RegisteredTileEntity tileEntity : tileEntities) {
            GameRegistry.registerTileEntity(tileEntity.tileEntity, tileEntity.getResourceLocation());
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new AutoCraftingTablesGUIHandler());
    }


    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        for (RegisteredItem item : items) {
            ModelLoader.setCustomModelResourceLocation(
                    item.item, 0, item.getModelResourceLocation()
            );
        }

        for (RegisteredBlock block : blocks) {
            ModelLoader.setCustomModelResourceLocation(
                    block.getItem(), 0, block.getModelResourceLocation()
            );
        }
    }

    public static class RegisteredItem {
        public final Item item;
        public final String itemName;
        public final String texture;
        public final String translationKey;

        public RegisteredItem(Item item, String itemName) {
            this(item, itemName, itemName, itemName);
        }

        public RegisteredItem(Item item, String itemName, String texture) {
            this(item, itemName, texture, itemName);
        }

        public RegisteredItem(Item item, String itemName, String texture, String translationKey) {
            this.item = item;
            this.itemName = itemName;
            this.texture = texture;
            this.translationKey = translationKey;

            item.setRegistryName(MOD_ID, itemName)
                    .setUnlocalizedName(translationKey);
        }

        public ModelResourceLocation getModelResourceLocation() {
            return new ModelResourceLocation(
                    new ResourceLocation(MOD_ID, itemName),
                    "inventory"
            );
        }
    }

    public static class RegisteredBlock {
        public final Block block;
        public final String blockName;
        public final String texture;
        public final String translationKey;

        public RegisteredBlock(Block block, String blockName) {
            this(block, blockName, blockName, blockName);
        }

        public RegisteredBlock(Block block, String blockName, String texture) {
            this(block, blockName, texture, blockName);
        }

        public RegisteredBlock(Block block, String blockName, String texture, String translationKey) {
            this.block = block;
            this.blockName = blockName;
            this.texture = texture;
            this.translationKey = translationKey;

            block.setRegistryName(MOD_ID, blockName)
                    .setUnlocalizedName(translationKey);
        }

        public Item getItemBlock() {
            return new ItemBlock(block)
                    .setRegistryName(MOD_ID, blockName);
        }

        public Item getItem() {
            return Item.getItemFromBlock(block);
        }

        public ModelResourceLocation getModelResourceLocation() {
            return new ModelResourceLocation(
                    new ResourceLocation(MOD_ID, blockName),
                    "inventory"
            );
        }
    }

    public static class RegisteredTileEntity {
        public final Class<? extends TileEntity> tileEntity;
        public final String tileEntityName;

        public RegisteredTileEntity(Class<? extends TileEntity> tileEntity, String tileEntityName) {
            this.tileEntity = tileEntity;
            this.tileEntityName = tileEntityName;
        }

        public ResourceLocation getResourceLocation() {
            return new ResourceLocation(MOD_ID, tileEntityName);
        }
    }
}
