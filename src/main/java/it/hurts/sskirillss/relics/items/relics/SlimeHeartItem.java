package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Consumer;

public class SlimeHeartItem extends RelicItem<SlimeHeartItem.Stats> {
    public static final String TAG_SLIME_AMOUNT = "slime";

    public static SlimeHeartItem INSTANCE;

    public SlimeHeartItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.CAVE)
                        .chance(0.1F)
                        .build())
                .loot(RelicLoot.builder()
                        .table(EntityType.SLIME.getDefaultLootTable().toString())
                        .chance(0.001F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .active(Minecraft.getInstance().options.keyShift.getKey().getDisplayName().getString()
                                + " + " + Minecraft.getInstance().options.keyUse.getKey().getDisplayName().getString())
                        .build())
                .shift(ShiftTooltip.builder()
                        .arg(Minecraft.getInstance().options.keyShift.getKey().getDisplayName().getString())
                        .build())
                .build();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        int slime = NBTUtils.getInt(stack, TAG_SLIME_AMOUNT, 0);

        if (slime > 0)
            tooltip.add(new TranslationTextComponent("tooltip.relics.slime_heart.tooltip_1", slime, config.slimeCapacity));
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        World world = player.getCommandSenderWorld();

        if (isBroken(stack) || !(entity instanceof SlimeEntity) || !player.isShiftKeyDown())
            return ActionResultType.FAIL;

        SlimeEntity slime = (SlimeEntity) entity;
        int amount = NBTUtils.getInt(stack, TAG_SLIME_AMOUNT, 0);

        if (amount >= config.slimeCapacity)
            return ActionResultType.FAIL;

        NBTUtils.setInt(stack, TAG_SLIME_AMOUNT, (int) Math.min(config.slimeCapacity,
                amount + slime.getHealth() * (config.healthMultiplier + player.getRandom().nextFloat())));

        slime.remove();
        world.playSound(null, player.blockPosition(), SoundEvents.SLIME_ATTACK, SoundCategory.HOSTILE, 1.0F, 1.0F);

        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !slotContext.getWearer().isShiftKeyDown();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SlimeHeartEvents {

        @SubscribeEvent
        public static void onEntityFall(LivingFallEvent event) {
            Stats config = INSTANCE.getConfig();
            LivingEntity entity = event.getEntityLiving();

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SLIME_HEART.get(), entity).ifPresent(triple -> {
                ItemStack stack = triple.getRight();
                int amount = NBTUtils.getInt(stack, TAG_SLIME_AMOUNT, 0);

                if (isBroken(stack) || event.getDistance() < 2 || entity.isShiftKeyDown() || amount <= 0)
                    return;

                entity.fallDistance = 0.0F;
                event.setCanceled(true);

                NBTUtils.setInt(stack, TAG_SLIME_AMOUNT, amount - 1);

                entity.playSound(SoundEvents.SLIME_SQUISH, 1F, 1F);
                BounceHandler.addBounceHandler(entity, -entity.getDeltaMovement().y() * config.motionMultiplier);
            });
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
                Vector3d motion = this.entity.getDeltaMovement();

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
        public int slimeCapacity = 100;
        public float healthMultiplier = 2.0F;
        public float motionMultiplier = 0.9F;
    }
}