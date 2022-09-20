package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigDataOld;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
    public RelicStyleData getStyle(ItemStack stack) {
        return RelicStyleData.builder()
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
    public RelicConfigDataOld<Stats> getConfigData() {
        return RelicConfigDataOld.<Stats>builder()
                .stats(new Stats())
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
            HitResult ray = event.getRayTraceResult();

            if (ray.getType() != HitResult.Type.ENTITY)
                return;

            EntityHitResult entityRay = (EntityHitResult) ray;
            Entity entity = entityRay.getEntity();

            if (!(entity instanceof Player player))
                return;

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.GHOST_SKIN_TALISMAN.get()).isEmpty())
                return;

            event.setCanceled(true);
        }
    }

    public static class Stats extends RelicStats {
        public float attackSpeedMultiplier = -0.5F;
    }
}