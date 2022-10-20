package it.hurts.sskirillss.relics.items.relics.hands;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class EnderHandItem extends RelicItem {
    public static final String TAG_TIME = "time";
    public static final String TAG_TARGET = "target";

    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("swap", RelicAbilityEntry.builder()
                                .stat("distance", RelicAbilityStat.builder()
                                        .initialValue(16D, 32D)
                                        .upgradeModifier("add", 4D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#eed551", "#dcbe1d")
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || DurabilityUtils.isBroken(stack)
                || player.getCooldowns().isOnCooldown(stack.getItem()))
            return;

        Level level = player.getLevel();

        String uuid = NBTUtils.getString(stack, TAG_TARGET, "");
        int time = NBTUtils.getInt(stack, TAG_TIME, 0);

        if (time > 0)
            level.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, 1F + (time * 0.025F));

        if (player.isShiftKeyDown()) {
            if (uuid.isEmpty()) {
                EntityHitResult result = EntityUtils.rayTraceEntity(player, (entity) -> !entity.isSpectator() && entity.isPickable(), getAbilityValue(stack, "swap", "distance"));

                if (result != null && result.getEntity() instanceof LivingEntity entity) {
                    uuid = entity.getStringUUID();

                    NBTUtils.setString(stack, TAG_TARGET, uuid);
                }
            } else {
                if (time < 20) {
                    ++time;

                    if (!level.isClientSide())
                        ((ServerLevel) level).sendParticles(ParticleTypes.DRAGON_BREATH, player.getX(), player.getY() + player.getBbHeight() * 0.5F, player.getZ(),
                                time, player.getBbWidth() * 0.5F, player.getBbHeight() * 0.5F, player.getBbWidth() * 0.5F, 0.025F);

                    NBTUtils.setInt(stack, TAG_TIME, time);
                } else if (!level.isClientSide()) {
                    ServerLevel serverLevel = (ServerLevel) level;

                    Entity entity = serverLevel.getEntity(UUID.fromString(uuid));

                    if (entity == null) {
                        NBTUtils.setString(stack, TAG_TARGET, "");
                        NBTUtils.setInt(stack, TAG_TIME, 0);

                        return;
                    }

                    Vec3 targetPos = player.position();
                    Vec3 currentPos = entity.position();

                    player.teleportTo(currentPos.x(), currentPos.y(), currentPos.z());
                    level.playSound(null, currentPos.x(), currentPos.y(), currentPos.z(),
                            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                    entity.teleportTo(targetPos.x(), targetPos.y(), targetPos.z());
                    level.playSound(null, targetPos.x(), targetPos.y(), targetPos.z(),
                            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                    NBTUtils.setString(stack, TAG_TARGET, "");
                    NBTUtils.setInt(stack, TAG_TIME, 0);

                    int distance = (int) Math.round(targetPos.distanceTo(currentPos));

                    addExperience(stack, 1 + Math.round(distance * 0.1F));

                    player.getCooldowns().addCooldown(this, 20);

                    Vec3 finalVec = targetPos.add(currentPos.subtract(targetPos).normalize().multiply(distance, distance, distance));
                    distance = (int) Math.round(targetPos.distanceTo(finalVec)) * 5;

                    for (int i = 0; i < distance; i++) {
                        float x = (float) (((finalVec.x - targetPos.x) * i / distance) + targetPos.x);
                        float y = (float) (((finalVec.y - targetPos.y) * i / distance) + targetPos.y + player.getBbHeight() / 2F);
                        float z = (float) (((finalVec.z - targetPos.z) * i / distance) + targetPos.z);

                        serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 1, 0F, 0F, 0F, 0.005F);
                    }
                }
            }
        } else {
            if (time > 0)
                NBTUtils.setInt(stack, TAG_TIME, --time);
            else
                NBTUtils.setString(stack, TAG_TARGET, "");
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class Events {
        @SubscribeEvent
        public static void onFOVUpdate(FOVModifierEvent event) {
            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntity(), ItemRegistry.ENDER_HAND.get());

            if (stack.isEmpty())
                return;

            int time = NBTUtils.getInt(stack, TAG_TIME, 0);

            if (time > 0)
                event.setNewfov(event.getNewfov() - time / 32.0F);
        }
    }
}