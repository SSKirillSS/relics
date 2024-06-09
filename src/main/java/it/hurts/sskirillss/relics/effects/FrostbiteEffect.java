package it.hurts.sskirillss.relics.effects;

import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class FrostbiteEffect extends MobEffect {
    public FrostbiteEffect() {
        super(MobEffectCategory.HARMFUL, 0x00FFFF);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        private static final int DAMAGE_INTERVAL = 20;
        private static final float BASE_DAMAGE = 0.5F;

        @SubscribeEvent
        public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
            LivingEntity entity = event.getEntity();
            Level level = entity.getCommandSenderWorld();

            MobEffectInstance effect = entity.getEffect(EffectRegistry.FROSTBITE.get());
            if (effect != null) {
                int amplifier = effect.getAmplifier();
                float growthRate = BASE_DAMAGE * amplifier + 0.3F;
                float accumulatedDamage = entity.getPersistentData().getFloat("accumulatedDamage") + growthRate;
                entity.getPersistentData().putFloat("accumulatedDamage", accumulatedDamage);

                entity.setTicksFrozen(Integer.MAX_VALUE);
                entity.extinguishFire();
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, 1.0F, 1.0F);

                if (entity.tickCount % DAMAGE_INTERVAL == 0) {
                    entity.hurt(level.damageSources().freeze(), accumulatedDamage);
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    entity.getPersistentData().putFloat("accumulatedDamage", 0.0F);
                }
            } else {
                entity.getPersistentData().remove("accumulatedDamage");
            }
        }

        @SubscribeEvent
        public static void onEntityRender(RenderLivingEvent.Pre<?, ?> event) {
            LivingEntity entity = event.getEntity();
            Level level = entity.getCommandSenderWorld();

            if (!entity.hasEffect(EffectRegistry.FROSTBITE.get()) || entity.isDeadOrDying())
                return;

            entity.setTicksFrozen(Integer.MAX_VALUE);

            for (int i = 0; i < 4; i++) {
                float xOffset = (float) (Math.random() - 0.5) * 0.4F;
                float zOffset = (float) (Math.random() - 0.5) * 0.4F;

                double xPos = entity.getX() + xOffset;
                double yPos = entity.getY() + entity.getBbHeight() / 1.2;
                double zPos = entity.getZ() + zOffset;

                level.addParticle(ParticleTypes.SNOWFLAKE, xPos, yPos, zPos,
                        (float) (Math.random() - 0.5) * 0.1,
                        (float) (Math.random() - 0.5) * 0.1,
                        (float) (Math.random() - 0.5) * 0.1);
            }
        }
    }
}