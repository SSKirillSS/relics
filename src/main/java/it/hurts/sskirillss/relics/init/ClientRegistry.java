package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.entities.renderer.NullRenderer;
import it.hurts.sskirillss.relics.entities.renderer.SpaceDissectorRenderer;
import it.hurts.sskirillss.relics.items.relics.ReflectionNecklaceItem;
import it.hurts.sskirillss.relics.items.relics.SpaceDissectorItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistry {
    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.STELLAR_CATALYST_PROJECTILE.get(), NullRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.SPACE_DISSECTOR.get(), new SpaceDissectorRenderer.RenderFactory());
        RenderTypeLookup.setRenderLayer(BlockRegistry.CHALK_BLOCK.get(), RenderType.getCutout());

        ItemModelsProperties.registerProperty(
                ItemRegistry.SPACE_DISSECTOR.get(), new ResourceLocation(Reference.MODID, "mode"),
                (stack, world, entity) -> NBTUtils.getBoolean(stack, SpaceDissectorItem.TAG_IS_THROWN, false) ? 2
                        : (entity instanceof PlayerEntity && ((PlayerEntity) entity).getCooldownTracker().hasCooldown(stack.getItem())) ? 1 : 0);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(ReflectionNecklaceItem.RL);
    }
}