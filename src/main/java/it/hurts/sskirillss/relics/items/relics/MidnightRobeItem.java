package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class MidnightRobeItem extends RelicItem<MidnightRobeItem.Stats> implements ICurioItem, IHasTooltip {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "midnight_robe_movement_speed", UUID.fromString("21a949be-67d9-43bb-96b8-496782d60933"));

    public static final String TAG_UPDATE_TIME = "time";

    public static MidnightRobeItem INSTANCE;

    public MidnightRobeItem() {
        super(Rarity.RARE);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.midnight_robe.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.midnight_robe.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeModifier attribute = new AttributeModifier(SPEED_INFO.getRight(),
                SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL);
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

        if (livingEntity.tickCount % 20 == 0 && time > 0) {
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
        }

        if (livingEntity.getHealth() < livingEntity.getMaxHealth() * config.healthPercentage) {
            if (time <= 0) {
                if (livingEntity.getCommandSenderWorld().isNight()) {
                    livingEntity.setInvisible(true);
                    EntityUtils.applyAttributeModifier(livingEntity.getAttribute(Attributes.MOVEMENT_SPEED), attribute);
                }
            } else {
                livingEntity.setInvisible(false);
                EntityUtils.removeAttributeModifier(livingEntity.getAttribute(Attributes.MOVEMENT_SPEED), attribute);
            }
        } else {
            livingEntity.setInvisible(false);
            EntityUtils.removeAttributeModifier(livingEntity.getAttribute(Attributes.MOVEMENT_SPEED), attribute);
        }
    }

    @Override
    public void onUnequip(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        EntityUtils.removeAttributeModifier(livingEntity.getAttribute(Attributes.MOVEMENT_SPEED), new AttributeModifier(SPEED_INFO.getRight(),
                SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
        livingEntity.setInvisible(false);
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.CAVE;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class MidnightRobeServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            if (event.getSource().getEntity() instanceof PlayerEntity
                    && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MIDNIGHT_ROBE.get(),
                    (LivingEntity) event.getSource().getEntity()).isPresent()) {
                ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MIDNIGHT_ROBE.get(),
                        (LivingEntity) event.getSource().getEntity()).get().getRight();
                if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) <= 0)
                    event.setAmount(event.getAmount() * config.damageMultiplier);
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, config.stealthDamageCooldown);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class MidnightRobeClientEvents {
        @SubscribeEvent
        public static void onEntityRender(RenderPlayerEvent.Pre event) {
            Stats config = INSTANCE.config;
            PlayerEntity player = event.getPlayer();
            if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MIDNIGHT_ROBE.get(), player).isPresent()
                    && player.getHealth() < player.getMaxHealth() * config.healthPercentage
                    && player.getCommandSenderWorld().getTimeOfDay(1.0F) > 0.26F
                    && player.getCommandSenderWorld().getTimeOfDay(1.0F) < 0.827F) event.setCanceled(true);
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 1.25F;
        public float healthPercentage = 0.2F;
        public float damageMultiplier = 2.0F;
        public int stealthDamageCooldown = 5;
    }
}