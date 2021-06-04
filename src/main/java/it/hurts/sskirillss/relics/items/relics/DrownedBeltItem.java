package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class DrownedBeltItem extends RelicItem implements ICurioItem, IHasTooltip {
    private static final AttributeModifier DROWNED_BELT_SWIM_SPEED = new AttributeModifier(UUID.fromString("1a0aa526-7a44-42a7-9d6d-a3d2fae599ef"),
            Reference.MODID + ":" + "drowned_belt_swim_speed", RelicsConfig.DrownedBelt.UNDERWATER_SPEED_MULTIPLIER.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

    public DrownedBeltItem() {
        super(Rarity.UNCOMMON);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.drowned_belt.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity.isInWater()) {
            livingEntity.setAirSupply(livingEntity.getMaxAirSupply());
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(String identifier, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> result = ICurioItem.super.getAttributeModifiers(identifier, stack);
        result.put(ForgeMod.SWIM_SPEED.get(), DROWNED_BELT_SWIM_SPEED);
        return result;
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.AQUATIC;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class DrownedBeltServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DROWNED_BELT.get(), player).isPresent()
                        && player.isInWater()) {
                    event.setAmount(event.getAmount() * RelicsConfig.DrownedBelt.INCOMING_DAMAGE_MULTIPLIER.get().floatValue());
                }
            }

            if (event.getSource().getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DROWNED_BELT.get(), player).isPresent()
                        && player.isInWater()) {
                    event.setAmount(event.getAmount() * RelicsConfig.DrownedBelt.DEALT_DAMAGE_MULTIPLIER.get().floatValue());
                }
            }
        }
    }
}