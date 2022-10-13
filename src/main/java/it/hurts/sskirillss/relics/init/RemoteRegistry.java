package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.client.renderer.entities.*;
import it.hurts.sskirillss.relics.client.renderer.tiles.*;
import it.hurts.sskirillss.relics.items.SolidSnowballItem;
import it.hurts.sskirillss.relics.items.relics.InfinityHamItem;
import it.hurts.sskirillss.relics.items.relics.ShadowGlaiveItem;
import it.hurts.sskirillss.relics.items.relics.back.ElytraBoosterItem;
import it.hurts.sskirillss.relics.items.relics.feet.AquaWalkerItem;
import it.hurts.sskirillss.relics.items.relics.feet.MagmaWalkerItem;
import it.hurts.sskirillss.relics.items.relics.feet.RollerSkatesItem;
import it.hurts.sskirillss.relics.items.relics.BlazingFlaskItem;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static it.hurts.sskirillss.relics.items.relics.back.ArrowQuiverItem.*;

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
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RUNIC_ALTAR_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RUNIC_ANVIL_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.BLOODY_LECTERN_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RESEARCHING_TABLE.get(), RenderType.cutout());

        HotkeyRegistry.register();

        ItemRegistry.getRegisteredRelics().forEach(item -> ItemProperties.register(item, new ResourceLocation(Reference.MODID, "broken"),
                (stack, world, entity, id) -> DurabilityUtils.isBroken(stack) ? 1 : 0));
        ItemProperties.register(ItemRegistry.INFINITY_HAM.get(), new ResourceLocation(Reference.MODID, "pieces"),
                (stack, world, entity, id) -> Math.min(10, NBTUtils.getInt(stack, InfinityHamItem.TAG_PIECES, 0)));
        ItemProperties.register(ItemRegistry.SHADOW_GLAIVE.get(), new ResourceLocation(Reference.MODID, "charges"),
                (stack, world, entity, id) -> Math.min(8, NBTUtils.getInt(stack, ShadowGlaiveItem.TAG_CHARGES, 0)));
        ItemProperties.register(ItemRegistry.MAGIC_MIRROR.get(), new ResourceLocation(Reference.MODID, "world"),
                (stack, world, entity, id) -> {
                    Entity e = Minecraft.getInstance().getCameraEntity();

                    if (e == null)
                        return 0;

                    return switch (e.getLevel().dimension().location().getPath()) {
                        case "overworld" -> 1;
                        case "the_nether" -> 2;
                        case "the_end" -> 3;
                        default -> 0;
                    };
                });
        ItemProperties.register(ItemRegistry.SHADOW_GLAIVE.get(), new ResourceLocation(Reference.MODID, "charges"),
                (stack, world, entity, id) -> Math.min(8, NBTUtils.getInt(stack, ShadowGlaiveItem.TAG_CHARGES, 0)));
        ItemProperties.register(ItemRegistry.MAGMA_WALKER.get(), new ResourceLocation(Reference.MODID, "heat"),
                (stack, world, entity, id) -> {
                    int heat = NBTUtils.getInt(stack, MagmaWalkerItem.TAG_HEAT, 0);
                    int maxHeat = 60;

                    return heat > maxHeat ? 4 : (int) Math.floor(heat / (maxHeat / 4F));
                });
        ItemProperties.register(ItemRegistry.AQUA_WALKER.get(), new ResourceLocation(Reference.MODID, "drench"),
                (stack, world, entity, id) -> {
                    int drench = NBTUtils.getInt(stack, AquaWalkerItem.TAG_DRENCH, 0);

                    return (int) Math.floor(drench / (getAbilityValue(stack, "walking", "time") / 4F));
                });
        ItemProperties.register(ItemRegistry.ARROW_QUIVER.get(), new ResourceLocation(Reference.MODID, "fullness"),
                (stack, world, entity, id) -> {
                    int maxAmount = getSlotsAmount(stack);
                    int amount = getArrows(stack).size();

                    return amount > 0 ? (int) Math.floor(amount / (maxAmount / 2F)) + 1 : 0;
                });
        ItemProperties.register(ItemRegistry.ELYTRA_BOOSTER.get(), new ResourceLocation(Reference.MODID, "fuel"),
                (stack, world, entity, id) -> {
                    int fuel = NBTUtils.getInt(stack, ElytraBoosterItem.TAG_FUEL, 0);

                    return (int) Math.floor(fuel / (getAbilityValue(stack, "boost", "capacity") / 4F));
                });
        ItemProperties.register(ItemRegistry.SOLID_SNOWBALL.get(), new ResourceLocation(Reference.MODID, "snow"),
                (stack, world, entity, id) -> {
                    int snow = NBTUtils.getInt(stack, SolidSnowballItem.TAG_SNOW, 0);
                    int capacity = 10;

                    ItemStack relic = EntityUtils.findEquippedCurio(entity, ItemRegistry.WOOL_MITTEN.get());

                    return (int) Math.floor(snow / (capacity / 5F));
                });
        ItemProperties.register(ItemRegistry.ROLLER_SKATES.get(), new ResourceLocation(Reference.MODID, "active"),
                (stack, world, entity, id) -> NBTUtils.getInt(stack, RollerSkatesItem.TAG_SKATING_DURATION, 0) > 0 ? 1 : 0);

        ItemProperties.register(ItemRegistry.BLAZING_FLASK.get(), new ResourceLocation(Reference.MODID, "active"),
                (stack, world, entity, id) -> NBTUtils.getString(stack, BlazingFlaskItem.TAG_POSITION, "").isEmpty() ? 0 : 1);

        MinecraftForgeClient.registerTooltipComponentFactory(ArrowQuiverTooltip.class, ClientArrowQuiverTooltip::new);
    }

    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.STELLAR_CATALYST_PROJECTILE.get(), new StellarCatalystProjectileRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.SHADOW_GLAIVE.get(), new ShadowGlaiveRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.BLOCK_SIMULATION.get(), new BlockSimulationRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.SHOCKWAVE.get(), new NullRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.LIFE_ESSENCE.get(), new NullRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.STALACTITE.get(), new StalactiteRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.DISSECTION.get(), new DissectionRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.SPORE.get(), new SporeRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.SHADOW_SAW.get(), new ShadowSawRenderer.RenderFactory());
        event.registerEntityRenderer(EntityRegistry.POISONED_PUDDLE.get(), new NullRenderer.RenderFactory());

        event.registerBlockEntityRenderer(TileRegistry.PEDESTAL_TILE.get(), PedestalTileRenderer::new);
        event.registerBlockEntityRenderer(TileRegistry.RUNIC_ALTAR_TILE.get(), RunicAltarTileRenderer::new);
        event.registerBlockEntityRenderer(TileRegistry.RUNIC_ANVIL_TILE.get(), RunicAnvilTileRenderer::new);
        event.registerBlockEntityRenderer(TileRegistry.BLOODY_LECTERN_TILE.get(), BloodyLecternTileRenderer::new);
        event.registerBlockEntityRenderer(TileRegistry.RESEARCHING_TABLE.get(), ResearchingTableRenderer::new);
    }
}