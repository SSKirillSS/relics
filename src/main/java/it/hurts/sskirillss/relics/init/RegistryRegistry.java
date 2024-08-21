package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.items.relics.base.data.cast.containers.base.RelicContainer;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class RegistryRegistry {
    public static final ResourceKey<Registry<RelicContainer>> RELIC_CONTAINER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "relic_containers"));
    public static final Registry<RelicContainer> RELIC_CONTAINER_REGISTRY = new RegistryBuilder<>(RELIC_CONTAINER_REGISTRY_KEY).create();

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(RELIC_CONTAINER_REGISTRY);
    }
}