package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DelayRingItem extends RelicItem<DelayRingItem.Stats> implements ICurioItem, IHasTooltip {
    public static final String TAG_UPDATE_TIME = "time";
    public static final String TAG_STORED_AMOUNT = "amount";
    public static final String TAG_IS_ACTIVE = "active";
    public static final String TAG_KILLER_UUID = "killer";

    public static DelayRingItem INSTANCE;

    public DelayRingItem() {
        super(Rarity.EPIC);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.delay_ring.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.delay_ring.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) > 0) {
            tooltip.add(new TranslationTextComponent("tooltip.relics.delay_ring.tooltip_1", NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0)));
            tooltip.add(new TranslationTextComponent("tooltip.relics.delay_ring.tooltip_2", NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)));
        }
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity) {
            int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
            PlayerEntity player = (PlayerEntity) livingEntity;
            if (!player.getCooldowns().isOnCooldown(ItemRegistry.DELAY_RING.get())
                    && NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
                if (time < config.delayDuration) {
                    if (livingEntity.tickCount % 20 == 0) NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
                } else {
                    if (NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0) > 0) {
                        player.setHealth(Math.min(NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0), player.getMaxHealth()));
                    } else {
                        if (!NBTUtils.getString(stack, TAG_KILLER_UUID, "").equals("")
                                && player.getCommandSenderWorld().getPlayerByUUID(UUID.fromString(NBTUtils.getString(stack, TAG_KILLER_UUID, ""))) != null) {
                            player.hurt(DamageSource.playerAttack(player.getCommandSenderWorld()
                                    .getPlayerByUUID(UUID.fromString(NBTUtils.getString(stack, TAG_KILLER_UUID, "")))), Integer.MAX_VALUE);
                        } else {
                            player.hurt(DamageSource.GENERIC, Integer.MAX_VALUE);
                        }
                        ParticleUtils.createBall(new CircleTintData(new Color(0.4F, 0.05F, 0.7F), 0.5F, 40, 0.94F, true),
                                player.position(), player.getCommandSenderWorld(), 3, 0.2F);
                    }
                    player.getCooldowns().addCooldown(ItemRegistry.DELAY_RING.get(), config.useCooldown * 20);
                    NBTUtils.setInt(stack, TAG_STORED_AMOUNT, 0);
                    NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                    NBTUtils.setString(stack, TAG_KILLER_UUID, "");
                    NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, false);
                }
            }
        }
    }

    @Override
    public void onUnequip(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ItemStack ring = CuriosApi.getCuriosHelper().getCuriosHandler(livingEntity).map(
                handler -> handler.getStacksHandler(identifier)
                        .map(stacks -> stacks.getStacks().getStackInSlot(index)).orElse(ItemStack.EMPTY))
                .orElse(ItemStack.EMPTY);
        if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)
                && ring == ItemStack.EMPTY) {
            if (!NBTUtils.getString(stack, TAG_KILLER_UUID, "").equals("")
                    && livingEntity.getCommandSenderWorld().getPlayerByUUID(UUID.fromString(NBTUtils.getString(stack, TAG_KILLER_UUID, ""))) != null) {
                livingEntity.hurt(DamageSource.playerAttack(livingEntity.getCommandSenderWorld()
                        .getPlayerByUUID(UUID.fromString(NBTUtils.getString(stack, TAG_KILLER_UUID, "")))), Integer.MAX_VALUE);
                NBTUtils.setString(stack, TAG_KILLER_UUID, "");
            } else {
                livingEntity.hurt(DamageSource.GENERIC, Integer.MAX_VALUE);
            }
            ParticleUtils.createBall(new CircleTintData(new Color(0.4F, 0.05F, 0.7F), 0.5F, 40, 0.94F, true),
                    livingEntity.position(), livingEntity.getCommandSenderWorld(), 3, 0.2F);
        }
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
        @SubscribeEvent
        public static void onEntityDeath(LivingDeathEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).get().getRight();
                    if (!NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false) && !player.getCooldowns().isOnCooldown(ItemRegistry.DELAY_RING.get())) {
                        if (event.getSource().getEntity() instanceof PlayerEntity) NBTUtils.setString(stack, TAG_KILLER_UUID, event.getSource().getEntity().getUUID().toString());
                        NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, true);
                        player.setHealth(1.0F);
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).get().getRight();
                    if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false) && NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) < config.delayDuration) {
                        NBTUtils.setInt(stack, TAG_STORED_AMOUNT, NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)
                                - Math.round(event.getAmount() * config.damageMultiplier));
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            Stats config = INSTANCE.config;
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).get().getRight();
                    if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
                        NBTUtils.setInt(stack, TAG_STORED_AMOUNT, NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)
                                + Math.round(event.getAmount() * config.healMultiplier));
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    public static class Stats extends RelicStats {
        public int useCooldown = 60;
        public int delayDuration = 10;
        public float damageMultiplier = 1.0F;
        public float healMultiplier = 2.0F;
    }
}