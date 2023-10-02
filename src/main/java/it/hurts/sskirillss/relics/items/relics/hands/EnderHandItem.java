package it.hurts.sskirillss.relics.items.relics.hands;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateInfo;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class EnderHandItem extends RelicItem {
    @Override
    public RelicData constructRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("neutrality", RelicAbilityEntry.builder()
                                .maxLevel(0)
                                .build())
                        .ability("swap", RelicAbilityEntry.builder()
                                .maxLevel(10)
                                .active(AbilityCastType.INSTANTANEOUS, AbilityCastPredicate.builder()
                                        .predicate("target", data -> {
                                                    EntityHitResult result = EntityUtils.rayTraceEntity(data.getPlayer(), (entity) -> !entity.isSpectator() && entity.isPickable(), AbilityUtils.getAbilityValue(data.getStack(), "swap", "distance"));

                                                    return PredicateInfo.builder()
                                                            .condition(result != null && result.getEntity() instanceof LivingEntity)
                                                            .build();
                                                }
                                        )
                                )
                                .stat("distance", RelicAbilityStat.builder()
                                        .initialValue(16D, 32D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 1))
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
    public void castActiveAbility(ItemStack stack, Player player, String ability, AbilityCastType type, AbilityCastStage stage) {
        if (ability.equals("swap")) {
            if (player.getCooldowns().isOnCooldown(stack.getItem()))
                return;

            Level level = player.level();

            EntityHitResult result = EntityUtils.rayTraceEntity(player, (entity) -> !entity.isSpectator() && entity.isPickable(), AbilityUtils.getAbilityValue(stack, "swap", "distance"));

            if (result == null || !(result.getEntity() instanceof LivingEntity entity))
                return;

            Vec3 targetPos = player.position();
            Vec3 currentPos = entity.position();

            player.teleportTo(currentPos.x(), currentPos.y(), currentPos.z());
            level.playSound(null, currentPos.x(), currentPos.y(), currentPos.z(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

            entity.teleportTo(targetPos.x(), targetPos.y(), targetPos.z());
            level.playSound(null, targetPos.x(), targetPos.y(), targetPos.z(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

            int distance = (int) Math.round(targetPos.distanceTo(currentPos));

            LevelingUtils.addExperience(player, stack, 1 + Math.round(distance * 0.1F));
        }
    }
}