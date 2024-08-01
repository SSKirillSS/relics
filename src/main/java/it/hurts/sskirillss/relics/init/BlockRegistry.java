package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.blocks.ResearchingTableBlock;
import it.hurts.sskirillss.relics.items.BlockItemBase;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class BlockRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MODID);

    public static final RegistryObject<ResearchingTableBlock> RESEARCHING_TABLE = BLOCKS.register("researching_table", () -> new ResearchingTableBlock(BlockBehaviour.Properties.of()
            .lightLevel((s) -> 15)
            .strength(1.5F)
            .sound(SoundType.WOOD)
            .noOcclusion()));

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());

        for (RegistryObject<? extends Block> block : BLOCKS.getEntries())
            ITEMS.register(block.getId().getPath(), () -> new BlockItemBase(block.get(), new Item.Properties()));

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}