package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicLootData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class GhostSkinTalismanItem extends RelicItem<GhostSkinTalismanItem.Stats> {
    public static GhostSkinTalismanItem INSTANCE;

    public GhostSkinTalismanItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#bba6db", "#544a62")
                .ability(AbilityTooltip.builder()
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("-" + (int) Math.abs(stats.attackSpeedMultiplier * 100) + "%")
                        .negative()
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .loot(RelicLootData.builder()
                        .table(RelicUtils.Worldgen.NETHER)
                        .chance(0.1F)
                        .build())
                .build();
    }

    @Override
    public RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ATTACK_SPEED, stats.attackSpeedMultiplier))
                .build();
    }

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            RayTraceResult ray = event.getRayTraceResult();

            if (ray.getType() != RayTraceResult.Type.ENTITY)
                return;

            EntityRayTraceResult entityRay = (EntityRayTraceResult) ray;
            Entity entity = entityRay.getEntity();

            if (!(entity instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) entity;

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.GHOST_SKIN_TALISMAN.get()).isEmpty())
                return;

            event.setCanceled(true);
        }
    }

    public static class Stats extends RelicStats {
        public float attackSpeedMultiplier = -0.5F;
    }
}