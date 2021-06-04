package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
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
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class MidnightRobeItem extends RelicItem implements ICurioItem, IHasTooltip {
    private static final AttributeModifier MIDNIGHT_ROBE_SPEED_BOOST = new AttributeModifier(UUID.fromString("21a949be-67d9-43bb-96b8-496782d60933"),
            Reference.MODID + ":" + "midnight_robe_movement_speed", RelicsConfig.MidnightRobe.MOVEMENT_SPEED_MULTIPLIER.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

    public static final String TAG_UPDATE_TIME = "time";

    public MidnightRobeItem() {
        super(Rarity.RARE);
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
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

        if (livingEntity.tickCount % 20 == 0 && time > 0) {
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
        }

        if (livingEntity.getHealth() < livingEntity.getMaxHealth() * RelicsConfig.MidnightRobe.HEALTH_PERCENTAGE.get()) {
            if (time <= 0) {
                if (livingEntity.getCommandSenderWorld().isNight()) {
                    livingEntity.setInvisible(true);
                    if (!movementSpeed.hasModifier(MIDNIGHT_ROBE_SPEED_BOOST))
                        movementSpeed.addTransientModifier(MIDNIGHT_ROBE_SPEED_BOOST);
                }
            } else {
                livingEntity.setInvisible(false);
                if (movementSpeed.hasModifier(MIDNIGHT_ROBE_SPEED_BOOST))
                    movementSpeed.removeModifier(MIDNIGHT_ROBE_SPEED_BOOST);
            }
        } else {
            livingEntity.setInvisible(false);
            if (movementSpeed.hasModifier(MIDNIGHT_ROBE_SPEED_BOOST))
                movementSpeed.removeModifier(MIDNIGHT_ROBE_SPEED_BOOST);
        }
    }

    @Override
    public void onUnequip(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed.hasModifier(MIDNIGHT_ROBE_SPEED_BOOST)) {
            movementSpeed.removeModifier(MIDNIGHT_ROBE_SPEED_BOOST);
            livingEntity.setInvisible(false);
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.CAVE;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class MidnightRobeServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            if (event.getSource().getEntity() instanceof PlayerEntity
                    && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MIDNIGHT_ROBE.get(),
                    (LivingEntity) event.getSource().getEntity()).isPresent()) {
                ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MIDNIGHT_ROBE.get(),
                        (LivingEntity) event.getSource().getEntity()).get().getRight();
                if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) <= 0)
                    event.setAmount(event.getAmount() * RelicsConfig.MidnightRobe.STEALTH_DAMAGE_MULTIPLIER.get().floatValue());
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, RelicsConfig.MidnightRobe.ATTACK_INVISIBILITY_PENALTY.get());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class MidnightRobeClientEvents {
        @SubscribeEvent
        public static void onEntityRender(RenderPlayerEvent.Pre event) {
            PlayerEntity player = event.getPlayer();
            if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MIDNIGHT_ROBE.get(), player).isPresent()
                    && player.getHealth() < player.getMaxHealth() * RelicsConfig.MidnightRobe.HEALTH_PERCENTAGE.get()
                    && player.getCommandSenderWorld().getTimeOfDay(1.0F) > 0.26F
                    && player.getCommandSenderWorld().getTimeOfDay(1.0F) < 0.827F) event.setCanceled(true);
        }
    }
}