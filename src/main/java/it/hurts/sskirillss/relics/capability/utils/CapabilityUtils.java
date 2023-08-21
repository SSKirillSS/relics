package it.hurts.sskirillss.relics.capability.utils;

import it.hurts.sskirillss.relics.capability.entries.IRelicsCapability;
import it.hurts.sskirillss.relics.init.CapabilityRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityUtils {
    public static LazyOptional<IRelicsCapability> getRelicsCapability(Player player) {
        return player.getCapability(CapabilityRegistry.DATA);
    }
}