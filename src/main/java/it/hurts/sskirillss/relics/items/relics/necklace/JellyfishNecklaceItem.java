package it.hurts.sskirillss.relics.items.relics.necklace;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;

public class JellyfishNecklaceItem extends RelicItem {
    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("unsinkable", RelicAbilityEntry.builder()
                                .maxLevel(0)
                                .build())
                        .ability("shock", RelicAbilityEntry.builder()
                                .active(AbilityCastType.TOGGLEABLE)
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(0.5D, 2.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability("paralysis", RelicAbilityEntry.builder()
                                .requiredLevel(5)
                                .stat("duration", RelicAbilityStat.builder()
                                        .initialValue(0.5D, 1.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 200))
                .styleData(RelicStyleData.builder()
                        .borders("#dc41ff", "#832698")
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        if (player.isEyeInFluid(FluidTags.WATER))
            EntityUtils.applyAttribute(player, stack, ForgeMod.ENTITY_GRAVITY.get(), -1F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        else
            EntityUtils.removeAttribute(player, stack, ForgeMod.ENTITY_GRAVITY.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

        Level level = player.getCommandSenderWorld();

        if (!player.isSpectator() && AbilityUtils.isAbilityTicking(stack, "shock")) {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox())) {
                if (entity == player)
                    continue;

                if (entity.hurt(DamageSource.playerAttack(player), (float) AbilityUtils.getAbilityValue(stack, "shock", "damage"))) {
                    LevelingUtils.addExperience(player, stack, 1);

                    if (AbilityUtils.canUseAbility(stack, "paralysis"))
                        entity.addEffect(new MobEffectInstance(EffectRegistry.PARALYSIS.get(), (int) Math.round(AbilityUtils.getAbilityValue(stack, "paralysis", "duration") * 20), 0));
                }
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttribute(slotContext.entity(), stack, ForgeMod.ENTITY_GRAVITY.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}