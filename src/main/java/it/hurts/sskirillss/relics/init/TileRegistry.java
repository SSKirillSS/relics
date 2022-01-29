package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.tiles.BloodyLecternTile;
import it.hurts.sskirillss.relics.tiles.PedestalTile;
import it.hurts.sskirillss.relics.tiles.RunicAltarTile;
import it.hurts.sskirillss.relics.tiles.RunicAnvilTile;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TileRegistry {
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Reference.MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Reference.MODID);

    public static final RegistryObject<BlockEntityType<PedestalTile>> PEDESTAL_TILE = TILES.register("pedestal", () ->
            BlockEntityType.Builder.of(PedestalTile::new, BlockRegistry.PEDESTAL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<RunicAltarTile>> RUNIC_ALTAR_TILE = TILES.register("runic_altar", () ->
            BlockEntityType.Builder.of(RunicAltarTile::new, BlockRegistry.RUNIC_ALTAR_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<RunicAnvilTile>> RUNIC_ANVIL_TILE = TILES.register("runic_anvil", () ->
            BlockEntityType.Builder.of(RunicAnvilTile::new, BlockRegistry.RUNIC_ANVIL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<BloodyLecternTile>> BLOODY_LECTERN_TILE = TILES.register("bloody_lectern", () ->
            BlockEntityType.Builder.of(BloodyLecternTile::new, BlockRegistry.BLOODY_LECTERN_BLOCK.get()).build(null));

    public static void registerTiles() {
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}