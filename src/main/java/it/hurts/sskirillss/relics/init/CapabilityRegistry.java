package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.capability.entries.IRelicsCapability;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CapabilityRegistry {
    public static final Capability<IRelicsCapability> DATA = CapabilityManager.get(new CapabilityToken<>() {
    });

    @SubscribeEvent
    public static void onCapabilityRegistry(RegisterCapabilitiesEvent event) {
        event.register(IRelicsCapability.RelicsCapability.class);
    }

    @SubscribeEvent
    public static void onCapabilityAttach(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player)
            event.addCapability(new ResourceLocation(Reference.MODID, "data"), new IRelicsCapability.RelicsCapabilityProvider());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath())
            return;

        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getPlayer();

        oldPlayer.reviveCaps();

        newPlayer.getCapability(DATA).orElse(null).deserializeNBT(oldPlayer.getCapability(DATA).orElse(null).serializeNBT());

        oldPlayer.invalidateCaps();
    }
}