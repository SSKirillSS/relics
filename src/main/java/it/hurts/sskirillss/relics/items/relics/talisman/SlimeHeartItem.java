package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigDataOld;
import it.hurts.sskirillss.relics.entities.ShockwaveEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.IdentityHashMap;
import java.util.function.Consumer;

public class SlimeHeartItem extends RelicItem<SlimeHeartItem.Stats> {
    public static SlimeHeartItem INSTANCE;

    public SlimeHeartItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicStyleData getStyle(ItemStack stack) {
        return RelicStyleData.builder()
                .borders("#73c262", "#40a31c")
                .ability(AbilityTooltip.builder()
                        .arg((int) (stats.healingMultiplier * 100 - 100) + "%")
                        .negative()
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg(Minecraft.getInstance().options.keyShift.getKey().getDisplayName().getString())
                        .build())
                .build();
    }

    @Override
    public RelicConfigDataOld<Stats> getConfigData() {
        return RelicConfigDataOld.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SlimeHeartEvents {
        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            Stats stats = INSTANCE.getStats();

            LivingEntity entity = event.getEntityLiving();
            ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.SLIME_HEART.get());

            if (stack.isEmpty())
                return;

            event.setAmount(event.getAmount() * stats.healingMultiplier);
        }

        @SubscribeEvent
        public static void onEntityFall(LivingFallEvent event) {
            Stats stats = INSTANCE.getStats();

            LivingEntity entity = event.getEntityLiving();
            ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.SLIME_HEART.get());

            if (stack.isEmpty() || event.getDistance() < 2 || entity.isShiftKeyDown())
                return;

            entity.fallDistance = 0.0F;
            event.setCanceled(true);

            entity.playSound(SoundEvents.SLIME_SQUISH, 1F, 1F);

            BounceHandler.addBounceHandler(entity, -entity.getDeltaMovement().y());
        }
    }

    public static class BounceHandler implements Consumer<LivingEvent.LivingUpdateEvent> {
        private static final IdentityHashMap<Entity, BounceHandler> bouncingEntities = new IdentityHashMap<>();

        public final LivingEntity entity;
        private boolean wasInAir;
        private double delta;
        private int time;
        private int tick;

        public BounceHandler(LivingEntity entityLiving, double motion) {
            this.entity = entityLiving;
            this.wasInAir = false;
            this.delta = motion;
            this.time = 0;

            if (motion != 0) {
                this.tick = entityLiving.tickCount + 1;
            } else
                this.tick = 0;

            bouncingEntities.put(entityLiving, this);
        }

        @Override
        public void accept(LivingEvent.LivingUpdateEvent event) {
            if (event.getEntityLiving() != this.entity || this.entity.isFallFlying())
                return;

            if (this.entity.tickCount == this.tick) {
                Vec3 motion = this.entity.getDeltaMovement();

                this.entity.setDeltaMovement(motion.x, this.delta, motion.z);
                this.tick = 0;
            }

            if (this.wasInAir && this.entity.isOnGround()) {
                if (this.time == 0)
                    this.time = this.entity.tickCount;
                else if (this.entity.tickCount - this.time > 5) {
                    MinecraftForge.EVENT_BUS.unregister(this);

                    bouncingEntities.remove(this.entity);
                }
            } else {
                this.time = 0;
                this.wasInAir = true;
            }
        }

        public static void addBounceHandler(LivingEntity entity, double bounce) {
            if (entity instanceof FakePlayer)
                return;

            BounceHandler handler = bouncingEntities.get(entity);

            if (handler == null)
                MinecraftForge.EVENT_BUS.addListener(new BounceHandler(entity, bounce));
            else if (bounce != 0) {
                handler.delta = bounce;
                handler.tick = entity.tickCount + 1;
            }
        }
    }

    public static class Stats extends RelicStats {
        public float motionMultiplier = 0.9F;
        public float healingMultiplier = 0.75F;
    }
}