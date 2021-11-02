package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttribute;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.JellyfishNecklaceModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class JellyfishNecklaceItem extends RelicItem<JellyfishNecklaceItem.Stats> implements ICurioItem {
    public static JellyfishNecklaceItem INSTANCE;

    public JellyfishNecklaceItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .hasAbility()
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.AQUATIC)
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (config.healMultiplier * 100 - 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (config.magicResistance * 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("-" + (int) Math.abs(config.swimSpeedModifier * 100) + "%")
                        .negative()
                        .build())
                .ability(AbilityTooltip.builder()
                        .active()
                        .build())
                .build();
    }

    @Override
    public RelicAttribute getAttributes(ItemStack stack) {
        return RelicAttribute.builder()
                .attribute(new RelicAttribute.Modifier(ForgeMod.SWIM_SPEED.get(), config.swimSpeedModifier))
                .build();
    }

    @Override
    public void castAbility(PlayerEntity player, ItemStack stack) {
        if (!player.isInWaterOrRain() || player.getCooldowns().isOnCooldown(stack.getItem()))
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
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new JellyfishNecklaceModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class JellyfishNecklaceEvents {
        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            Stats config = INSTANCE.config;
            LivingEntity entity = event.getEntityLiving();

            if (EntityUtils.findEquippedCurio(entity, ItemRegistry.JELLYFISH_NECKLACE.get()).isEmpty()
                    || !entity.isInWater())
                return;

            event.setAmount(event.getAmount() * config.healMultiplier);
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;

            if (EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.JELLYFISH_NECKLACE.get()).isEmpty()
                    || event.getSource() != DamageSource.MAGIC)
                return;

            event.setAmount(event.getAmount() * config.magicResistance);
        }
    }

    public static class Stats extends RelicStats {
        public float swimSpeedModifier = -0.2F;
        public float magicResistance = 0.25F;
        public float healMultiplier = 3.0F;
        public int riptideCooldown = 5;
        public float riptidePower = 2.0F;
    }
}