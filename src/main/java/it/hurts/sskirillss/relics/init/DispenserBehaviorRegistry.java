package it.hurts.sskirillss.relics.init;

import net.minecraft.world.level.block.DispenserBlock;

public class DispenserBehaviorRegistry {
    public static void register() {
        DispenserBlock.registerProjectileBehavior(ItemRegistry.RELIC_EXPERIENCE_BOTTLE.get());
    }
}