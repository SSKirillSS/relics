package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

public class HolyLocketItem extends RelicItem<HolyLocketItem.Stats> {
    public static HolyLocketItem INSTANCE;

    public HolyLocketItem() {
        super(Rarity.RARE);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.holy_locket.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.holy_locket.shift_2"));
        return tooltip;
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.DESERT;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    @Mod.EventBusSubscriber
    static class HolyLocketEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            Item item = ItemRegistry.HOLY_LOCKET.get();
            if (event.getSource().getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                LivingEntity entity = event.getEntityLiving();
                if (!entity.isInvertedHealAndHarm()) return;
                CuriosApi.getCuriosHelper().findEquippedCurio(item, player).ifPresent(triple -> {
                    if (player.getCommandSenderWorld().random.nextFloat() <= config.igniteChance) entity.setSecondsOnFire(config.burnDuration);
                    event.setAmount(event.getAmount() * config.damageMultiplier);
                });
            } else if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                CuriosApi.getCuriosHelper().findEquippedCurio(item, player).ifPresent(triple -> {
                    if (random.nextFloat() <= config.healChance) {
                        player.heal(event.getAmount());
                        event.setCanceled(true);
                        return;
                    }
                    if (player.getCooldowns().isOnCooldown(item)) event.setCanceled(true);
                    else player.getCooldowns().addCooldown(item, config.invulnerabilityTime);
                });
            }
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingAttackEvent event) {
            Item item = ItemRegistry.HOLY_LOCKET.get();
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if (CuriosApi.getCuriosHelper().findEquippedCurio(item, player).isPresent()
                    && player.getCooldowns().isOnCooldown(item)) event.setCanceled(true);
        }
    }

    public static class Stats extends RelicStats {
        public float damageMultiplier = 1.5F;
        public float igniteChance = 0.25F;
        public int burnDuration = 4;
        public float healChance = 0.1F;
        public int invulnerabilityTime = 10;
    }
}