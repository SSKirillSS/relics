package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
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
import java.util.List;
import java.util.UUID;

public class DelayRingItem extends Item implements ICurioItem, IHasTooltip {
    public static final String TAG_UPDATE_TIME = "time";
    public static final String TAG_STORED_AMOUNT = "amount";
    public static final String TAG_IS_ACTIVE = "active";
    public static final String TAG_KILLER_UUID = "killer";

    public DelayRingItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.delay_ring.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.delay_ring.shift_2"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
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
            if (!player.getCooldownTracker().hasCooldown(ItemRegistry.DELAY_RING.get())) {
                if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
                    if (time < RelicsConfig.DelayRing.DELAY_DURATION.get()) {
                        if (livingEntity.ticksExisted % 20 == 0) NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
                    } else {
                        if (NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0) > 0) {
                            player.setHealth(Math.min(NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0), player.getMaxHealth()));
                        } else {
                            if (!NBTUtils.getString(stack, TAG_KILLER_UUID, "").equals("")
                                    && player.getEntityWorld().getPlayerByUuid(UUID.fromString(NBTUtils.getString(stack, TAG_KILLER_UUID, ""))) != null) {
                                player.attackEntityFrom(DamageSource.causePlayerDamage(player.getEntityWorld().getPlayerByUuid(UUID.fromString(NBTUtils.getString(stack, TAG_KILLER_UUID, "")))), Integer.MAX_VALUE);
                                NBTUtils.setString(stack, TAG_KILLER_UUID, "");
                            } else {
                                player.attackEntityFrom(DamageSource.GENERIC, Integer.MAX_VALUE);
                            }
                        }
                        ParticleUtils.createBall(new CircleTintData(new Color(0.4F, 0.05F, 0.7F), 0.5F, 40, 0.94F, true),
                                player.getPosition(), player.getEntityWorld(), 3, 0.2F);
                        player.getCooldownTracker().setCooldown(ItemRegistry.DELAY_RING.get(), RelicsConfig.DelayRing.USAGE_COOLDOWN.get() * 20);
                        NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                        NBTUtils.setInt(stack, TAG_STORED_AMOUNT, 0);
                        NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, false);
                    }
                }
            }
        }
    }

    @Override
    public void onUnequip(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
            if (!NBTUtils.getString(stack, TAG_KILLER_UUID, "").equals("")
                    && livingEntity.getEntityWorld().getPlayerByUuid(UUID.fromString(NBTUtils.getString(stack, TAG_KILLER_UUID, ""))) != null) {
                livingEntity.attackEntityFrom(DamageSource.causePlayerDamage(livingEntity.getEntityWorld().getPlayerByUuid(UUID.fromString(NBTUtils.getString(stack, TAG_KILLER_UUID, "")))), Integer.MAX_VALUE);
                NBTUtils.setString(stack, TAG_KILLER_UUID, "");
            } else {
                livingEntity.attackEntityFrom(DamageSource.GENERIC, Integer.MAX_VALUE);
            }
        }
        NBTUtils.setInt(stack, TAG_STORED_AMOUNT, 0);
        NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, false);
        NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
        NBTUtils.setString(stack, TAG_KILLER_UUID, "");
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class DelayRingEvents {
        @SubscribeEvent
        public static void onEntityDeath(LivingDeathEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).get().getRight();
                    if (!NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false) && !player.getCooldownTracker().hasCooldown(ItemRegistry.DELAY_RING.get())) {
                        if (event.getSource().getTrueSource() instanceof PlayerEntity) NBTUtils.setString(stack, TAG_KILLER_UUID, event.getSource().getTrueSource().getUniqueID().toString());
                        NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, true);
                        player.setHealth(1.0F);
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).get().getRight();
                    if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
                        NBTUtils.setInt(stack, TAG_STORED_AMOUNT, NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)
                                - Math.round(event.getAmount() * RelicsConfig.DelayRing.DAMAGE_MULTIPLIER.get().floatValue()));
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), player).get().getRight();
                    if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
                        NBTUtils.setInt(stack, TAG_STORED_AMOUNT, NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)
                                + Math.round(event.getAmount() * RelicsConfig.DelayRing.HEALING_MULTIPLIER.get().floatValue()));
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}