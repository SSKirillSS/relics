package it.hurts.sskirillss.relics.items.relics.hands;

import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
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
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("neutrality")
                                .maxLevel(0)
                                .build())
                        .ability(AbilityData.builder("swap")
                                .maxLevel(10)
                                .active(CastType.INSTANTANEOUS, CastPredicate.builder()
                                        .predicate("target", data -> {
                                            EntityHitResult result = EntityUtils.rayTraceEntity(data.getPlayer(), (entity) -> !entity.isSpectator() && entity.isPickable(), getAbilityValue(data.getStack(), "swap", "distance"));

                                            return result != null && result.getEntity() instanceof LivingEntity;
                                        })
                                        .build())
                                .stat(StatData.builder("distance")
                                        .initialValue(16D, 32D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .style(StyleData.builder()
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.END)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (ability.equals("swap")) {
            if (player.getCooldowns().isOnCooldown(stack.getItem()))
                return;

            Level level = player.getLevel();

            EntityHitResult result = EntityUtils.rayTraceEntity(player, (entity) -> !entity.isSpectator() && entity.isPickable(), getAbilityValue(stack, "swap", "distance"));

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

            addExperience(player, stack, 1 + Math.round(distance * 0.1F));
        }
    }
}