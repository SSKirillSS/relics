package it.hurts.sskirillss.relics.items.relics.belt;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

public class DrownedBeltItem extends RelicItem<DrownedBeltItem.Stats> {
    public static DrownedBeltItem INSTANCE;

    public DrownedBeltItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#7889b8", "#25374e")
                .ability(AbilityTooltip.builder()
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.dealtDamageMultiplier * 100 - 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.incomingDamageMultiplier * 100 - 100) + "%")
                        .negative()
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Override
    public RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return RelicSlotModifier.builder()
                .entry(Pair.of("talisman", 1))
                .build();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class DrownedBeltServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            Player player = null;
            float value = 1.0F;

            if (event.getEntityLiving() instanceof Player) {
                player = (Player) event.getEntityLiving();
                value = stats.incomingDamageMultiplier;
            } else if (event.getSource().getEntity() instanceof Player) {
                player = (Player) event.getSource().getEntity();
                value = stats.dealtDamageMultiplier;
            }

            if (player != null && player.isUnderWater()
                    && !EntityUtils.findEquippedCurio(player, ItemRegistry.DROWNED_BELT.get()).isEmpty())
                event.setAmount(event.getAmount() * value);
        }

        @SubscribeEvent
        public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
            ItemStack stack = event.getItem();

            if (!(event.getEntityLiving() instanceof Player player) || stack.getItem() != Items.TRIDENT)
                return;

            if (player.getCooldowns().isOnCooldown(stack.getItem()))
                event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onItemUseFinish(LivingEntityUseItemEvent.Stop event) {
            Stats stats = INSTANCE.stats;

            ItemStack stack = event.getItem();

            if (!(event.getEntityLiving() instanceof Player player) || stack.getItem() != Items.TRIDENT)
                return;

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.DROWNED_BELT.get()).isEmpty())
                return;

            int duration = stack.getItem().getUseDuration(stack) - event.getDuration();
            int enchantment = EnchantmentHelper.getRiptide(stack);

            if (duration < 10 || enchantment <= 0)
                return;

            player.getCooldowns().addCooldown(stack.getItem(), (stats.riptideCooldown / (enchantment + 1)) * 20);
        }
    }

    public static class Stats extends RelicStats {
        public float incomingDamageMultiplier = 1.5F;
        public float dealtDamageMultiplier = 2.0F;
        public int riptideCooldown = 10;
    }
}