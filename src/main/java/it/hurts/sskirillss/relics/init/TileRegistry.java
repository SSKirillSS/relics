package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.tiles.PedestalTile;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileRegistry {
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Reference.MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Reference.MODID);

    public static final RegistryObject<TileEntityType<PedestalTile>> PEDESTAL_TILE = TILES.register("pedestal", () ->
            TileEntityType.Builder.of(PedestalTile::new, BlockRegistry.PEDESTAL_BLOCK.get()).build(null));

    public static void registerTiles() {
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}