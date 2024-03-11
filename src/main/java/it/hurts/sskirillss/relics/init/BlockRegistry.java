package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.blocks.ResearchingTableBlock;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MODID);

    public static final RegistryObject<ResearchingTableBlock> RESEARCHING_TABLE = BLOCKS.register("researching_table", ResearchingTableBlock::new);

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());

        for (RegistryObject<? extends Block> block : BLOCKS.getEntries())
            ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(RelicsTab.RELICS_TAB)));

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}