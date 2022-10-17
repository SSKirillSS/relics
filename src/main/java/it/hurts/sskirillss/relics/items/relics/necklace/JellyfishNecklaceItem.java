package it.hurts.sskirillss.relics.items.relics.necklace;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.indev.*;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;

public class JellyfishNecklaceItem extends RelicItem {
    public JellyfishNecklaceItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .hasAbility()
                .build());
    }

    @Override
    public RelicDataNew getNewData() {
        return RelicDataNew.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("unsinkable", RelicAbilityEntry.builder()
                                .build())
                        .ability("shock", RelicAbilityEntry.builder()
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .ability("paralysis", RelicAbilityEntry.builder()
                                .requiredLevel(5)
                                .stat("duration", RelicAbilityStat.builder()
                                        .initialValue(0.5D, 1.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 0.5D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
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

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox())) {
            if (entity == player)
                continue;

            if (entity.hurt(DamageSource.playerAttack(player), (float) getAbilityValue(stack, "shock", "damage"))) {
                addExperience(stack, 1);

                if (canUseAbility(stack, "paralysis"))
                    entity.addEffect(new MobEffectInstance(EffectRegistry.PARALYSIS.get(), (int) Math.round(getAbilityValue(stack, "paralysis", "duration") * 20), 0));
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttribute(slotContext.entity(), stack, ForgeMod.ENTITY_GRAVITY.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}