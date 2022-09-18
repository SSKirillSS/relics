package it.hurts.sskirillss.relics.items.relics.ring;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class DelayRingItem extends RelicItem<DelayRingItem.Stats> {
    public static final String TAG_UPDATE_TIME = "time";
    public static final String TAG_STORED_AMOUNT = "amount";
    public static final String TAG_KILLER_UUID = "killer";

    public static DelayRingItem INSTANCE;

    public DelayRingItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#e60032", "#670d4e")
                .ability(AbilityTooltip.builder()
                        .arg(stats.delayDuration)
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) > 0) {
            tooltip.add(new TranslatableComponent("tooltip.relics.delay_ring.tooltip_1", NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0)));
            tooltip.add(new TranslatableComponent("tooltip.relics.delay_ring.tooltip_2", NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)));
        }
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        Level world = player.getCommandSenderWorld();
        int points = NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0);
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1);

        if (player.tickCount % 4 == 0 && time > 0)
            world.addParticle(points > 0 ? ParticleTypes.HEART : ParticleTypes.ANGRY_VILLAGER,
                    player.getX() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                    player.getEyeY() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                    player.getZ() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                    0, -0.25F, 0);

        if (world.isClientSide()
                || player.getCooldowns().isOnCooldown(stack.getItem()))
            return;

        if (time > 0) {
            if (player.tickCount % 20 == 0) {
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, --time);

                world.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK,
                        SoundSource.MASTER, 0.75F, 1.0F + time * 0.1F);
            }
        } else if (time == 0)
            delay(player, stack);
    }

    private void delay(LivingEntity entity, ItemStack stack) {
        if (!(entity instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        NBTUtils.setInt(stack, TAG_UPDATE_TIME, -1);

        Level world = player.getCommandSenderWorld();
        int points = NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0);

        world.playSound(null, player.blockPosition(), SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.MASTER, 1.0F, 1.0F);
        player.getCooldowns().addCooldown(stack.getItem(), stats.useCooldown * 20);

        if (points > 0)
            player.heal(points);
        else {
            String uuidString = NBTUtils.getString(stack, TAG_KILLER_UUID, "");
            DamageSource source = DamageSource.GENERIC;

            if (!uuidString.equals("")) {
                Player killer = world.getPlayerByUUID(UUID.fromString(uuidString));

                if (killer != null)
                    source = DamageSource.playerAttack(killer);
            }

            player.hurt(source, Integer.MAX_VALUE);
        }

        NBTUtils.setInt(stack, TAG_STORED_AMOUNT, 0);
        NBTUtils.setString(stack, TAG_KILLER_UUID, "");
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1) > -1
                && newStack.getItem() != stack.getItem())
            delay(slotContext.getWearer(), stack);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class DelayRingEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onEntityDeath(LivingDeathEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntityLiving() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.DELAY_RING.get());

            if (stack.isEmpty() || player.getCooldowns().isOnCooldown(stack.getItem()))
                return;

            Entity source = event.getSource().getEntity();

            if (source instanceof Player)
                NBTUtils.setString(stack, TAG_KILLER_UUID, source.getUUID().toString());

            NBTUtils.setInt(stack, TAG_UPDATE_TIME, stats.delayDuration);
            NBTUtils.setInt(stack, TAG_STORED_AMOUNT, 0);

            player.setHealth(1.0F);

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            LivingEntity entity = event.getEntityLiving();

            if (!(entity instanceof Player))
                return;

            Player player = (Player) entity;
            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.DELAY_RING.get());

            if (stack.isEmpty() || player.getCooldowns().isOnCooldown(stack.getItem())
                    || NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1) < 0)
                return;

            NBTUtils.setInt(stack, TAG_STORED_AMOUNT, NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)
                    - Math.round(event.getAmount() * stats.damageMultiplier));

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            Stats stats = INSTANCE.stats;

            LivingEntity entity = event.getEntityLiving();

            if (!(entity instanceof Player))
                return;

            Player player = (Player) entity;
            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.DELAY_RING.get());

            if (stack.isEmpty() || player.getCooldowns().isOnCooldown(stack.getItem())
                    || NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1) < 0)
                return;

            NBTUtils.setInt(stack, TAG_STORED_AMOUNT, NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)
                    + Math.round(event.getAmount() * stats.healMultiplier));

            event.setCanceled(true);
        }
    }

    public static class Stats extends RelicStats {
        public int useCooldown = 60;
        public int delayDuration = 10;
        public float damageMultiplier = 1.0F;
        public float healMultiplier = 2.0F;
    }
}