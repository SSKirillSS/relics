package it.hurts.sskirillss.relics.items.relics.belt;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.client.renderer.items.models.DrownedBeltModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicLootData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
                .loot(RelicLootData.builder()
                        .table(RelicUtils.Worldgen.AQUATIC)
                        .chance(0.1F)
                        .build())
                .loot(RelicLootData.builder()
                        .table(EntityType.DROWNED.getDefaultLootTable().toString())
                        .chance(0.01F)
                        .build())
                .build();
    }

    @Override
    public RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return RelicSlotModifier.builder()
                .entry(Pair.of("talisman", 1))
                .build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new DrownedBeltModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class DrownedBeltServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            PlayerEntity player = null;
            float value = 1.0F;

            if (event.getEntityLiving() instanceof PlayerEntity) {
                player = (PlayerEntity) event.getEntityLiving();
                value = stats.incomingDamageMultiplier;
            } else if (event.getSource().getEntity() instanceof PlayerEntity) {
                player = (PlayerEntity) event.getSource().getEntity();
                value = stats.dealtDamageMultiplier;
            }

            if (player != null && player.isUnderWater()
                    && !EntityUtils.findEquippedCurio(player, ItemRegistry.DROWNED_BELT.get()).isEmpty())
                event.setAmount(event.getAmount() * value);
        }

        @SubscribeEvent
        public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
            ItemStack stack = event.getItem();

            if (!(event.getEntityLiving() instanceof PlayerEntity) || stack.getItem() != Items.TRIDENT)
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            if (player.getCooldowns().isOnCooldown(stack.getItem()))
                event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onItemUseFinish(LivingEntityUseItemEvent.Stop event) {
            Stats stats = INSTANCE.stats;

            ItemStack stack = event.getItem();

            if (!(event.getEntityLiving() instanceof PlayerEntity) || stack.getItem() != Items.TRIDENT)
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

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