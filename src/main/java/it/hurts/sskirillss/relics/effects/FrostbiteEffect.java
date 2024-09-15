package it.hurts.sskirillss.relics.effects;

import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class FrostbiteEffect extends MobEffect {
    public FrostbiteEffect() {
        super(MobEffectCategory.HARMFUL, 0x00FFFF);
    }

    private static final int DAMAGE_INTERVAL = 20;
    private static final float BASE_DAMAGE = 0.2F;

    @EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onLivingUpdate(EntityTickEvent.Pre event) {
            if (event.getEntity() instanceof LivingEntity entity) {
                Level level = entity.getCommandSenderWorld();

                if (level.isClientSide)
                    return;

                ServerLevel serverLevel = (ServerLevel) level;

                MobEffectInstance effect = entity.getEffect(EffectRegistry.FROSTBITE);
                if (effect != null) {
                    int amplifier = effect.getAmplifier();
                    float growthRate = BASE_DAMAGE * amplifier + 0.1F;
                    float accumulatedDamage = entity.getPersistentData().getFloat("accumulatedDamage") + growthRate;
                    entity.getPersistentData().putFloat("accumulatedDamage", accumulatedDamage);

                    entity.setTicksFrozen(Integer.MAX_VALUE);
                    entity.clearFire();
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    float xOffset = (float) (Math.random() - 0.5) * 0.4F;
                    float zOffset = (float) (Math.random() - 0.5) * 0.4F;

                    double xPos = entity.getX() + xOffset;
                    double yPos = entity.getY() + entity.getBbHeight() / 1.2;
                    double zPos = entity.getZ() + zOffset;

                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, xPos, yPos, zPos,
                            4, 0, 0, 0, 0);

                    if (entity.tickCount % DAMAGE_INTERVAL == 0) {
                        entity.hurt(level.damageSources().freeze(), accumulatedDamage);
                        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        entity.getPersistentData().putFloat("accumulatedDamage", 0.0F);
                    }
                } else {
                    entity.getPersistentData().remove("accumulatedDamage");
                }
            }
        }
    }
}
