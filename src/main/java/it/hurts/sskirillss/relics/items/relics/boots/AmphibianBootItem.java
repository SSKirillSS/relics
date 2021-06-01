package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class AmphibianBootItem extends RelicItem implements ICurioItem, IHasTooltip {
    private static final AttributeModifier AMPHIBIAN_BOOT_SWIM_SPEED = new AttributeModifier(UUID.fromString("c12bcc95-aa73-48b7-9ff8-e8c70d713b43"),
            Reference.MODID + ":" + "amphibian_boot_swim_speed", 1.5F, AttributeModifier.Operation.MULTIPLY_TOTAL);

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
        if (!(livingEntity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        if (!player.isInWater() || !player.isOnGround()) {
            if (swimSpeed.hasModifier(AMPHIBIAN_BOOT_SWIM_SPEED))
                swimSpeed.removeModifier(AMPHIBIAN_BOOT_SWIM_SPEED);
            return;
        }
        if (swimSpeed.hasModifier(AMPHIBIAN_BOOT_SWIM_SPEED)) return;
        swimSpeed.addTransientModifier(AMPHIBIAN_BOOT_SWIM_SPEED);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ModifiableAttributeInstance swimSpeed = slotContext.getWearer().getAttribute(ForgeMod.SWIM_SPEED.get());
        if (!swimSpeed.hasModifier(AMPHIBIAN_BOOT_SWIM_SPEED)) return;
        swimSpeed.removeModifier(AMPHIBIAN_BOOT_SWIM_SPEED);
    }
}