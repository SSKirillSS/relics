package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.entities.ThrownRelicExperienceBottle;
import net.minecraft.Util;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class DispenserBehaviorRegistry {
    public static void register() {
        DispenserBlock.registerBehavior(ItemRegistry.RELIC_EXPERIENCE_BOTTLE.get(), new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                return Util.make(new ThrownRelicExperienceBottle(EntityRegistry.THROWN_RELIC_EXPERIENCE_BOTTLE.get(), level), (entity) -> {
                    entity.setPos(position.x(), position.y(), position.z());
                    entity.setItem(stack);
                });
            }

            @Override
            protected float getUncertainty() {
                return super.getUncertainty() * 0.5F;
            }

            @Override
            protected float getPower() {
                return super.getPower() * 1.25F;
            }
        });
    }
}