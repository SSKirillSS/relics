package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.blocks.*;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MODID);

    public static final RegistryObject<MagmaStoneBlock> MAGMA_STONE_BLOCK = BLOCKS.register("magma_stone", MagmaStoneBlock::new);
    public static final RegistryObject<ChalkBlock> CHALK_BLOCK = BLOCKS.register("chalk", ChalkBlock::new);
    public static final RegistryObject<PedestalBlock> PEDESTAL_BLOCK = BLOCKS.register("pedestal", PedestalBlock::new);

    public static void registerBlocks() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void onItemBlockRegistry(RegistryEvent.Register<Item> event) {
        BLOCKS.getEntries().stream().map(RegistryObject::get).filter(block -> !(block instanceof IVoidBlock))
                .forEach(block -> event.getRegistry().register(new BlockItem(block, new Item.Properties()
                        .tab(RelicsTab.RELICS_TAB)).setRegistryName(block.getRegistryName())));
    }
}