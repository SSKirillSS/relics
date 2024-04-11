package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;

public class IceSkatesItem extends RelicItem {
    private static final String TAG_SKATING_DURATION = "duration";

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("skating")
                                .stat(StatData.builder("speed")
                                        .initialValue(0.01D, 0.035D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 3) * 10 * 100))
                                        .build())
                                .stat(StatData.builder("duration")
                                        .initialValue(25D, 50D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value / 10, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("ram")
                                .requiredLevel(5)
                                .stat(StatData.builder("damage")
                                        .initialValue(0.05D, 0.25D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value * 10, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 200))
                .style(StyleData.builder()
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.COLD)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        Level level = player.getCommandSenderWorld();
        BlockPos pos = WorldUtils.getSolidBlockUnderFeet(level, player.blockPosition());

        int duration = NBTUtils.getInt(stack, TAG_SKATING_DURATION, 0);

        int maxDuration = (int) Math.round(getAbilityValue(stack, "skating", "duration"));

        if (player.isSprinting() && !player.isShiftKeyDown() && !player.isInWater() && !player.isInLava()
                && (pos != null && level.getBlockState(pos).is(BlockTags.ICE))) {
            if (player.tickCount % 20 == 0)
                addExperience(player, stack, 1);

            if (duration < maxDuration && player.tickCount % 2 == 0) {
                NBTUtils.setInt(stack, TAG_SKATING_DURATION, ++duration);
            }

            if (level.getRandom().nextInt(maxDuration) < duration)
                level.addParticle(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.15F,
                        player.getZ(), 0, 0.25F, 0);
        } else if (duration > 0)
            NBTUtils.setInt(stack, TAG_SKATING_DURATION, Math.max(0, duration - 2));

        if (canUseAbility(stack, "ram") && duration >= 10) {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox())) {
                if (entity == player || entity.hurtTime > 0)
                    continue;

                EntityUtils.hurt(entity, DamageSource.playerAttack(player), (float) (duration * getAbilityValue(stack, "ram", "damage")));

                double factor = Mth.clamp(duration * 0.025D, 1D, 2D);

                entity.setDeltaMovement(entity.position().add(0, 0.5F, 0).subtract(player.position()).normalize().multiply(factor, Math.max(1, factor / 2), factor));
            }
        }

        EntityUtils.removeAttribute(player, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (duration > 0) {
            EntityUtils.applyAttribute(player, stack, Attributes.MOVEMENT_SPEED, (float) (duration * getAbilityValue(stack, "skating", "speed")), AttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityUtils.applyAttribute(player, stack, ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6F, AttributeModifier.Operation.ADDITION);
        } else
            EntityUtils.removeAttribute(player, stack, ForgeMod.STEP_HEIGHT_ADDITION.get(), AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (stack.getItem() == newStack.getItem())
            return;

        LivingEntity entity = slotContext.entity();

        EntityUtils.removeAttribute(entity, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityUtils.removeAttribute(entity, stack, ForgeMod.STEP_HEIGHT_ADDITION.get(), AttributeModifier.Operation.ADDITION);
    }
}