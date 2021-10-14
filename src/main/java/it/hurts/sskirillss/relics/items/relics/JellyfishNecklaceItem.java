package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Multimap;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.JellyfishNecklaceModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class JellyfishNecklaceItem extends RelicItem<JellyfishNecklaceItem.Stats> implements ICurioItem {
    public static JellyfishNecklaceItem INSTANCE;

    public JellyfishNecklaceItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .model(new JellyfishNecklaceModel())
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.AQUATIC)
                        .chance(0.15F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.healMultiplier * 100 - 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.magicResistance * 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg("-" + (int) Math.abs(config.swimSpeedModifier * 100) + "%")
                        .negative()
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .active()
                        .build())
                .build();
    }

    @Override
    public void castAbility(PlayerEntity player, ItemStack stack) {
        if (!player.isUnderWater() || player.getCooldowns().isOnCooldown(stack.getItem()))
            return;

        float rot = ((float) Math.PI / 180F);
        float f0 = MathHelper.cos(player.xRot * rot);
        float f1 = -MathHelper.sin(player.yRot * rot) * f0;
        float f2 = -MathHelper.sin(player.xRot * rot);
        float f3 = MathHelper.cos(player.yRot * rot) * f0;
        float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
        float f5 = config.riptidePower;
        f1 *= (f5 / f4);
        f2 *= (f5 / f4);
        f3 *= (f5 / f4);

        player.push(f1, f2, f3);
        player.startAutoSpinAttack(20);

        player.getCooldowns().addCooldown(stack.getItem(), config.riptideCooldown * 20);

        player.getCommandSenderWorld().playSound(null, player, SoundEvents.TRIDENT_RIPTIDE_3, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> result = super.getAttributeModifiers(slotContext, uuid, stack);

        if (!isBroken(stack))
            result.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier(uuid, Reference.MODID + ":" + "ice_breaker_movement_speed",
                    config.swimSpeedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));

        return result;
    }

    @Override
    public boolean showAttributesTooltip(String identifier, ItemStack stack) {
        return false;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class JellyfishNecklaceEvents {
        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            Stats config = INSTANCE.config;
            LivingEntity entity = event.getEntityLiving();

            if (!entity.isInWater())
                return;
            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.JELLYFISH_NECKLACE.get(), entity).ifPresent(triple -> {
                if (isBroken(triple.getRight()))
                    return;

                event.setAmount(event.getAmount() * config.healMultiplier);
            });
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;

            if (event.getSource() != DamageSource.MAGIC)
                return;

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.JELLYFISH_NECKLACE.get(), event.getEntityLiving()).ifPresent(triple -> {
                if (isBroken(triple.getRight()))
                    return;

                event.setAmount(event.getAmount() * config.magicResistance);
            });
        }
    }

    public static class Stats extends RelicStats {
        public float swimSpeedModifier = -0.2F;
        public float magicResistance = 0.25F;
        public float healMultiplier = 3.0F;
        public int riptideCooldown = 5;
        public float riptidePower = 3.0F;
    }
}