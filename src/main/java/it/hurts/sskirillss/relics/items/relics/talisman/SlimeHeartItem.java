package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class SlimeHeartItem extends RelicItem {
    public SlimeHeartItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

//    @Mod.EventBusSubscriber(modid = Reference.MODID)
//    public static class SlimeHeartEvents {
//        @SubscribeEvent
//        public static void onEntityHeal(LivingHealEvent event) {
//            Stats stats = INSTANCE.getStats();
//
//            LivingEntity entity = event.getEntityLiving();
//            ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.SLIME_HEART.get());
//
//            if (stack.isEmpty())
//                return;
//
//            event.setAmount(event.getAmount() * stats.healingMultiplier);
//        }
//
//        @SubscribeEvent
//        public static void onEntityFall(LivingFallEvent event) {
//            Stats stats = INSTANCE.getStats();
//
//            LivingEntity entity = event.getEntityLiving();
//            ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.SLIME_HEART.get());
//
//            if (stack.isEmpty() || event.getDistance() < 2 || entity.isShiftKeyDown())
//                return;
//
//            entity.fallDistance = 0.0F;
//            event.setCanceled(true);
//
//            entity.playSound(SoundEvents.SLIME_SQUISH, 1F, 1F);
//
//            BounceHandler.addBounceHandler(entity, -entity.getDeltaMovement().y());
//        }
//    }
//
//    public static class BounceHandler implements Consumer<LivingEvent.LivingUpdateEvent> {
//        private static final IdentityHashMap<Entity, BounceHandler> bouncingEntities = new IdentityHashMap<>();
//
//        public final LivingEntity entity;
//        private boolean wasInAir;
//        private double delta;
//        private int time;
//        private int tick;
//
//        public BounceHandler(LivingEntity entityLiving, double motion) {
//            this.entity = entityLiving;
//            this.wasInAir = false;
//            this.delta = motion;
//            this.time = 0;
//
//            if (motion != 0) {
//                this.tick = entityLiving.tickCount + 1;
//            } else
//                this.tick = 0;
//
//            bouncingEntities.put(entityLiving, this);
//        }
//
//        @Override
//        public void accept(LivingEvent.LivingUpdateEvent event) {
//            if (event.getEntityLiving() != this.entity || this.entity.isFallFlying())
//                return;
//
//            if (this.entity.tickCount == this.tick) {
//                Vec3 motion = this.entity.getDeltaMovement();
//
//                this.entity.setDeltaMovement(motion.x, this.delta, motion.z);
//                this.tick = 0;
//            }
//
//            if (this.wasInAir && this.entity.isOnGround()) {
//                if (this.time == 0)
//                    this.time = this.entity.tickCount;
//                else if (this.entity.tickCount - this.time > 5) {
//                    MinecraftForge.EVENT_BUS.unregister(this);
//
//                    bouncingEntities.remove(this.entity);
//                }
//            } else {
//                this.time = 0;
//                this.wasInAir = true;
//            }
//        }
//
//        public static void addBounceHandler(LivingEntity entity, double bounce) {
//            if (entity instanceof FakePlayer)
//                return;
//
//            BounceHandler handler = bouncingEntities.get(entity);
//
//            if (handler == null)
//                MinecraftForge.EVENT_BUS.addListener(new BounceHandler(entity, bounce));
//            else if (bounce != 0) {
//                handler.delta = bounce;
//                handler.tick = entity.tickCount + 1;
//            }
//        }
//    }
}