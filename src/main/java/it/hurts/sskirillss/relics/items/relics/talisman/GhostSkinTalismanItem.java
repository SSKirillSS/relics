package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class GhostSkinTalismanItem extends RelicItem {
    public GhostSkinTalismanItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

//    @Override
//    public RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
//        return RelicAttributeModifier.builder()
//                .attribute(new RelicAttributeModifier.Modifier(Attributes.ATTACK_SPEED, stats.attackSpeedMultiplier))
//                .build();
//    }
//
//    @Mod.EventBusSubscriber
//    public static class Events {
//        @SubscribeEvent
//        public static void onProjectileImpact(ProjectileImpactEvent event) {
//            HitResult ray = event.getRayTraceResult();
//
//            if (ray.getType() != HitResult.Type.ENTITY)
//                return;
//
//            EntityHitResult entityRay = (EntityHitResult) ray;
//            Entity entity = entityRay.getEntity();
//
//            if (!(entity instanceof Player player))
//                return;
//
//            if (EntityUtils.findEquippedCurio(player, ItemRegistry.GHOST_SKIN_TALISMAN.get()).isEmpty())
//                return;
//
//            event.setCanceled(true);
//        }
//    }
}