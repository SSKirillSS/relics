package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DelayRingItem extends RelicItem<DelayRingItem.Stats> implements ICurioItem {
    public static final String TAG_UPDATE_TIME = "time";
    public static final String TAG_STORED_AMOUNT = "amount";
    public static final String TAG_KILLER_UUID = "killer";

    public static DelayRingItem INSTANCE;

    public DelayRingItem() {
        super(Rarity.EPIC);

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg(config.delayDuration)
                        .build())
                .build();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) > 0) {
            tooltip.add(new TranslationTextComponent("tooltip.relics.delay_ring.tooltip_1", NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0)));
            tooltip.add(new TranslationTextComponent("tooltip.relics.delay_ring.tooltip_2", NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)));
        }
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1);

        if (player.getCommandSenderWorld().isClientSide()
                || player.getCooldowns().isOnCooldown(stack.getItem()))
            return;

        if (time > 0) {
            if (player.tickCount % 20 == 0)
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
        } else if (time == 0)
            delay(player, stack);
    }

    private void delay(LivingEntity entity, ItemStack stack) {
        if (!(entity instanceof PlayerEntity))
            return;

        NBTUtils.setInt(stack, TAG_UPDATE_TIME, -1);

        PlayerEntity player = (PlayerEntity) entity;
        World world = player.getCommandSenderWorld();
        int points = NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0);

        world.playSound(null, player.blockPosition(), SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundCategory.MASTER, 1.0F, 1.0F);
        player.getCooldowns().addCooldown(stack.getItem(), config.useCooldown * 20);

        if (points > 0)
            player.heal(points);
        else {
            String uuidString = NBTUtils.getString(stack, TAG_KILLER_UUID, "");
            DamageSource source = DamageSource.GENERIC;

            if (!uuidString.equals("")) {
                PlayerEntity killer = world.getPlayerByUUID(UUID.fromString(uuidString));

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

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.END_CITY_TREASURE);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class DelayRingEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onEntityDeath(LivingDeathEvent event) {
            Stats config = INSTANCE.config;

            if (!(event.getEntityLiving() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).ifPresent(triple -> {
                ItemStack stack = triple.getRight();

                if (player.getCooldowns().isOnCooldown(stack.getItem()))
                    return;

                Entity source = event.getSource().getEntity();

                if (source instanceof PlayerEntity)
                    NBTUtils.setString(stack, TAG_KILLER_UUID, source.getUUID().toString());

                NBTUtils.setInt(stack, TAG_UPDATE_TIME, config.delayDuration);
                NBTUtils.setInt(stack, TAG_STORED_AMOUNT, 0);

                player.setHealth(1.0F);

                event.setCanceled(true);
            });
        }

        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), event.getEntityLiving()).ifPresent(triple -> {
                ItemStack stack = triple.getRight();

                if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1) < 0)
                    return;

                NBTUtils.setInt(stack, TAG_STORED_AMOUNT, NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)
                        - Math.round(event.getAmount() * config.damageMultiplier));

                event.setCanceled(true);
            });
        }

        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            Stats config = INSTANCE.config;

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), event.getEntityLiving()).ifPresent(triple -> {
                ItemStack stack = triple.getRight();

                if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1) < 0)
                    return;

                NBTUtils.setInt(stack, TAG_STORED_AMOUNT, NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)
                        + Math.round(event.getAmount() * config.healMultiplier));

                event.setCanceled(true);
            });
        }
    }

    public static class Stats extends RelicStats {
        public int useCooldown = 60;
        public int delayDuration = 10;
        public float damageMultiplier = 1.0F;
        public float healMultiplier = 2.0F;
    }
}