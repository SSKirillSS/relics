package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.RelicStats;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class AmphibianBootItem extends RelicItem<AmphibianBootItem.Stats> implements ICurioItem, IHasTooltip {
    private final MutablePair<String, UUID> SWIM_SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "amphibian_boot_swim_speed", UUID.fromString("c12bcc95-aa73-48b7-9ff8-e8c70d713b43"));

    public AmphibianBootItem() {
        super(Rarity.UNCOMMON);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.amphibian_boot.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance swimSpeed = livingEntity.getAttribute(ForgeMod.SWIM_SPEED.get());
        AttributeModifier modifier = new AttributeModifier(SWIM_SPEED_INFO.getRight(), SWIM_SPEED_INFO.getLeft(),
                config.swimSpeedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL);
        if (!(livingEntity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        if (!player.isInWater() || !player.isOnGround()) {
            EntityUtils.removeAttributeModifier(swimSpeed, modifier);
            return;
        }
        EntityUtils.applyAttributeModifier(swimSpeed, modifier);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttributeModifier(slotContext.getWearer().getAttribute(ForgeMod.SWIM_SPEED.get()),
                new AttributeModifier(SWIM_SPEED_INFO.getRight(), SWIM_SPEED_INFO.getLeft(), config.swimSpeedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.AQUATIC;
    }

    @Override
    public float getWorldgenChance() {
        return super.getWorldgenChance();
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    public static class Stats extends RelicStats {
        public float swimSpeedModifier = 1.5F;
    }
}