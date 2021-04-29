package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class LuckyHorseshoeItem extends RelicItem implements ICurioItem, IHasTooltip {
    public LuckyHorseshoeItem() {
        super(Rarity.RARE);
    }

    @Override
    public java.util.List<ITextComponent> getShiftTooltip() {
        java.util.List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.lucky_horseshoe.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return livingEntity.getCommandSenderWorld().getRandom().nextFloat() <= RelicsConfig.LuckyHorseshoe.LOOTING_CHANCE.get()
                ? RelicsConfig.LuckyHorseshoe.ADDITIONAL_LOOTING.get() : 0;
    }

    @Override
    public int getFortuneBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return livingEntity.getCommandSenderWorld().getRandom().nextFloat() <= RelicsConfig.LuckyHorseshoe.FORTUNA_CHANCE.get()
                ? RelicsConfig.LuckyHorseshoe.ADDITIONAL_FORTUNE.get() : 0;
    }
}