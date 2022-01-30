package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.client.renderer.entities.ShadowGlaiveRenderer;
import it.hurts.sskirillss.relics.client.renderer.entities.SpaceDissectorRenderer;
import it.hurts.sskirillss.relics.client.renderer.entities.StellarCatalystProjectileRenderer;
import it.hurts.sskirillss.relics.client.renderer.tiles.BloodyLecternTileRenderer;
import it.hurts.sskirillss.relics.client.renderer.tiles.PedestalTileRenderer;
import it.hurts.sskirillss.relics.client.renderer.tiles.RunicAltarTileRenderer;
import it.hurts.sskirillss.relics.client.renderer.tiles.RunicAnvilTileRenderer;
import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.items.relics.InfinityHamItem;
import it.hurts.sskirillss.relics.items.relics.ShadowGlaiveItem;
import it.hurts.sskirillss.relics.items.relics.SpaceDissectorItem;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RemoteRegistry {
    public static final ResourceLocation CIRCLE = new ResourceLocation(Reference.MODID, "particle/circle_tint");
    public static final ResourceLocation SPARK = new ResourceLocation(Reference.MODID, "particle/spark_tint");

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        ResourceLocation location = event.getAtlas().location();

        if (location.equals(TextureAtlas.LOCATION_BLOCKS)) {
            event.addSprite(new ResourceLocation(Reference.MODID, "gui/curios/empty_talisman_slot"));
            event.addSprite(new ResourceLocation(Reference.MODID, "gui/curios/empty_feet_slot"));
        }

        if (location.equals(TextureAtlas.LOCATION_PARTICLES)) {
            event.addSprite(CIRCLE);
            event.addSprite(SPARK);
        }
    }

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.CHALK_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RUNIC_ALTAR_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RUNIC_ANVIL_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.BLOODY_LECTERN_BLOCK.get(), RenderType.cutout());

        HotkeyRegistry.register();

        ItemProperties.register(ItemRegistry.SPACE_DISSECTOR.get(), new ResourceLocation(Reference.MODID, "mode"),
                (stack, world, entity, id) -> NBTUtils.getBoolean(stack, SpaceDissectorItem.TAG_IS_THROWN, false) ? 1 : 0);
        ItemProperties.register(ItemRegistry.INFINITY_HAM.get(), new ResourceLocation(Reference.MODID, "pieces"),
                (stack, world, entity, id) -> Math.min(3, NBTUtils.getInt(stack, InfinityHamItem.TAG_PIECES, 0)));
        ItemProperties.register(ItemRegistry.SHADOW_GLAIVE.get(), new ResourceLocation(Reference.MODID, "charges"),
                (stack, world, entity, id) -> Math.min(8, NBTUtils.getInt(stack, ShadowGlaiveItem.TAG_CHARGES, 0)));
        ItemProperties.register(ItemRegistry.RELIC_CONTRACT.get(), new ResourceLocation(Reference.MODID, "blood"),
                (stack, world, entity, id) -> NBTUtils.getInt(stack, RelicContractItem.TAG_BLOOD, 0));

        ItemRegistry.getRegisteredRelics().forEach(item -> ItemProperties.register(item,
                new ResourceLocation(Reference.MODID, "broken"),
                (stack, world, entity, id) -> DurabilityUtils.isBroken(stack) ? 1 : 0));
    }

    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.STELLAR_CATALYST_PROJECTILE.get(), new StellarCatalystProjectileRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.SPACE_DISSECTOR.get(), new SpaceDissectorRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.SHADOW_GLAIVE.get(), new ShadowGlaiveRenderer.RenderFactory());


        event.registerBlockEntityRenderer(TileRegistry.PEDESTAL_TILE.get(), PedestalTileRenderer::new);
        event.registerBlockEntityRenderer(TileRegistry.RUNIC_ALTAR_TILE.get(), RunicAltarTileRenderer::new);
        event.registerBlockEntityRenderer(TileRegistry.RUNIC_ANVIL_TILE.get(), RunicAnvilTileRenderer::new);
        event.registerBlockEntityRenderer(TileRegistry.BLOODY_LECTERN_TILE.get(), BloodyLecternTileRenderer::new);
    }
}