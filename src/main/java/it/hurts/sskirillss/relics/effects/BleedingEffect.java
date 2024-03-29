package it.hurts.sskirillss.relics.effects;

import it.hurts.sskirillss.relics.init.EffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class BleedingEffect extends MobEffect {
    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0X6836AA);
    }

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
            LivingEntity entity = event.getEntity();

            if (entity.tickCount % 20 == 0 && entity.hasEffect(EffectRegistry.BLEEDING.get()))
                entity.hurt(entity.level().damageSources().magic(), Math.min(10, entity.getHealth() * 0.05F));
        }
    }
}