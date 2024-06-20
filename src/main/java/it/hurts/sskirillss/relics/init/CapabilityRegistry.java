package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.capability.entries.IRelicsCapability;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CapabilityRegistry {
    public static final EntityCapability<IRelicsCapability, Void> DATA = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "research_data"), IRelicsCapability.class);

    @SubscribeEvent
    public static void onCapabilityRegistry(RegisterCapabilitiesEvent event) {
        event.registerEntity(DATA, EntityType.PLAYER, (entity, context) -> new IRelicsCapability.RelicsCapability());
    }

//    @SubscribeEvent
//    public static void onPlayerClone(PlayerEvent.Clone event) {
//        if (!event.isWasDeath())
//            return;
//
//        Player oldPlayer = event.getOriginal();
//        Player newPlayer = event.getEntity();
//
//        oldPlayer.reviveCaps();
//
//        newPlayer.getCapability(DATA).orElse(null).deserializeNBT(oldPlayer.getCapability(DATA).orElse(null).serializeNBT());
//
//        oldPlayer.invalidateCaps();
//    }
}