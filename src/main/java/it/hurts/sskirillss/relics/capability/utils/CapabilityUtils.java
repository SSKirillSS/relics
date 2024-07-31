package it.hurts.sskirillss.relics.capability.utils;

import it.hurts.sskirillss.relics.capability.entries.IRelicsCapability;
import it.hurts.sskirillss.relics.init.CapabilityRegistry;
import net.minecraft.world.entity.player.Player;

public class CapabilityUtils {
    public static IRelicsCapability getRelicsCapability(Player player) {
        return player.getCapability(CapabilityRegistry.DATA).orElse(null);
    }
}