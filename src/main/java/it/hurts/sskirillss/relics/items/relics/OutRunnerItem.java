package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class OutRunnerItem extends RelicItem implements ICurioItem, IHasTooltip {
    private static final AttributeModifier OUT_RUNNER_SPEED_BOOST = new AttributeModifier(UUID.fromString("9bf3eeb5-8587-4fb7-ad81-fd76e01f4acf"),
            Reference.MODID + ":" + "out_runner_movement_speed", RelicsConfig.OutRunner.SPEED_MULTIPLIER.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

    private static final String TAG_RUN_DURATION = "duration";

    public OutRunnerItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.out_runner.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        int duration = NBTUtils.getInt(stack, TAG_RUN_DURATION, 0);
        if (!player.isSprinting() || player.isShiftKeyDown() || player.isInWater()) {
            if (duration > 0) NBTUtils.setInt(stack, TAG_RUN_DURATION, 0);
            if (!movementSpeed.hasModifier(OUT_RUNNER_SPEED_BOOST)) return;
            movementSpeed.removeModifier(OUT_RUNNER_SPEED_BOOST);
            player.maxUpStep = 0.6F;
            return;
        }
        if (duration < 5) {
            if (player.tickCount % 20 == 0) NBTUtils.setInt(stack, TAG_RUN_DURATION, duration + 1);
        } else {
            player.getCommandSenderWorld().addParticle(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.15F, player.getZ(), 0, 0.25F, 0);
            if (movementSpeed.hasModifier(OUT_RUNNER_SPEED_BOOST)) return;
            movementSpeed.addTransientModifier(OUT_RUNNER_SPEED_BOOST);
            livingEntity.maxUpStep = Math.max(livingEntity.maxUpStep, RelicsConfig.OutRunner.STEP_HEIGHT.get().floatValue());
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED);
        if (!movementSpeed.hasModifier(OUT_RUNNER_SPEED_BOOST)) return;
        movementSpeed.removeModifier(OUT_RUNNER_SPEED_BOOST);
        slotContext.getWearer().maxUpStep = 0.6F;
    }
}