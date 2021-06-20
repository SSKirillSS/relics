package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class MidnightRobeItem extends RelicItem<MidnightRobeItem.Stats> implements ICurioItem {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "midnight_robe_movement_speed", UUID.fromString("21a949be-67d9-43bb-96b8-496782d60933"));

    public static MidnightRobeItem INSTANCE;

    public MidnightRobeItem() {
        super(Rarity.RARE);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.midnight_robe.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.midnight_robe.shift_2"));
        return tooltip;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity.tickCount % 20 != 0 || !(livingEntity instanceof PlayerEntity) || livingEntity.getCommandSenderWorld().isClientSide()) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeModifier attribute = new AttributeModifier(SPEED_INFO.getRight(),
                SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL);
        if (canHide(livingEntity)) {
            player.addEffect(new EffectInstance(Effects.INVISIBILITY, 20, 0, false, false));
            EntityUtils.applyAttributeModifier(movementSpeed, attribute);
        } else EntityUtils.removeAttributeModifier(movementSpeed, attribute);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttributeModifier(slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED), new AttributeModifier(SPEED_INFO.getRight(),
                SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.CAVE;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    private static boolean canHide(LivingEntity entity) {
        Stats config = INSTANCE.config;
        if (!(entity instanceof PlayerEntity)) return false;
        PlayerEntity player = (PlayerEntity) entity;
        return CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MIDNIGHT_ROBE.get(), player).filter(triple ->
                player.getHealth() <= player.getMaxHealth() * config.healthPercentage && player.getCommandSenderWorld().isNight()
                        && !player.getCooldowns().isOnCooldown(triple.getRight().getItem())).isPresent();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class MidnightRobeServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            if (!(event.getSource().getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            if (!canHide(player)) return;
            event.setAmount(event.getAmount() * config.damageMultiplier);
            player.getCooldowns().addCooldown(ItemRegistry.MIDNIGHT_ROBE.get(), config.stealthDamageCooldown * 20);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class MidnightRobeClientEvents {
        @SubscribeEvent
        public static void onEntityRender(RenderPlayerEvent.Pre event) {
            Stats config = INSTANCE.config;
            PlayerEntity player = event.getPlayer();
            if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MIDNIGHT_ROBE.get(), player).isPresent()
                    && player.getHealth() <= player.getMaxHealth() * config.healthPercentage
                    && player.getCommandSenderWorld().getTimeOfDay(1.0F) > 0.26F
                    && player.getCommandSenderWorld().getTimeOfDay(1.0F) < 0.827F
                    && !player.getCooldowns().isOnCooldown(ItemRegistry.MIDNIGHT_ROBE.get())) event.setCanceled(true);
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 1.25F;
        public float healthPercentage = 0.2F;
        public float damageMultiplier = 2.0F;
        public int stealthDamageCooldown = 5;
    }
}