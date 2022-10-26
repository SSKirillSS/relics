package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class StellarCatalystItem extends RelicItem {
    public StellarCatalystItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

//    @Mod.EventBusSubscriber(modid = Reference.MODID)
//    public static class StellarCatalystServerEvents {
//        @SubscribeEvent
//        public static void onEntityDamage(LivingHurtEvent event) {
//            Stats stats = INSTANCE.stats;
//
//            DamageSource source = event.getSource();
//
//            if (!(source.getEntity() instanceof Player) || (source instanceof IndirectEntityDamageSource
//                    && source.getDirectEntity() instanceof FallingStarEntity))
//                return;
//
//            Player player = (Player) event.getSource().getEntity();
//
//            if (EntityUtils.findEquippedCurio(player, ItemRegistry.STELLAR_CATALYST.get()).isEmpty())
//                return;
//
//            LivingEntity target = event.getEntityLiving();
//            Level world = target.getCommandSenderWorld();
//            Random random = world.getRandom();
//
//            if (world.isNight() && world.canSeeSky(target.blockPosition())
//                    && random.nextFloat() <= stats.chance) {
//                FallingStarEntity projectile = new FallingStarEntity((LivingEntity) event.getSource().getEntity(),
//                        event.getEntityLiving(), event.getAmount() * stats.damageMultiplier);
//
//                projectile.setPos(target.getX(), Math.min(target.getCommandSenderWorld().getMaxBuildHeight(), Math.min(target.getCommandSenderWorld().getMaxBuildHeight(),
//                        target.getY() + target.getCommandSenderWorld().getRandom().nextInt(stats.additionalSummonHeight) + stats.minSummonHeight)), target.getZ());
//                projectile.owner = player;
//
//                world.addFreshEntity(projectile);
//            }
//        }
//    }
}