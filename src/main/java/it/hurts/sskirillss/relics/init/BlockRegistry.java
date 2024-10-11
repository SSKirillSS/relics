package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.blocks.PhantomBlock;
import it.hurts.sskirillss.relics.blocks.ResearchingTableBlock;
import it.hurts.sskirillss.relics.items.BlockItemBase;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Reference.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Reference.MODID);

    public static final DeferredHolder<Block, ResearchingTableBlock> RESEARCHING_TABLE = BLOCKS.register("researching_table", () -> new ResearchingTableBlock(BlockBehaviour.Properties.of()
            .lightLevel((s) -> 15)
            .strength(1.5F)
            .sound(SoundType.WOOD)
            .noOcclusion()));

    public static final DeferredHolder<Block, PhantomBlock> PHANTOM_BLOCK = BLOCKS.register("phantom_block", PhantomBlock::new);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);

        for (DeferredHolder<? extends Block, ? extends Block> block : BLOCKS.getEntries())
            ITEMS.register(block.getId().getPath(), () -> new BlockItemBase(block.get(), new Item.Properties()));

        ITEMS.register(bus);
    }
}