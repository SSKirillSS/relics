package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class OutRunnerItem extends RelicItem<OutRunnerItem.Stats> implements ICurioItem {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "out_runner_movement_speed", UUID.fromString("9bf3eeb5-8587-4fb7-ad81-fd76e01f4acf"));

    private static final String TAG_RUN_DURATION = "duration";

    public OutRunnerItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.out_runner.shift_1"));
        return tooltip;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        int duration = NBTUtils.getInt(stack, TAG_RUN_DURATION, 0);
        if (!player.isSprinting() || player.isShiftKeyDown() || player.isInWater()) {
            if (duration > 0) NBTUtils.setInt(stack, TAG_RUN_DURATION, 0);
            EntityUtils.removeAttributeModifier(player.getAttribute(Attributes.MOVEMENT_SPEED), new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
            player.maxUpStep = 0.6F;
            return;
        }
        if (duration < config.activationTime) {
            if (player.tickCount % 20 == 0) NBTUtils.setInt(stack, TAG_RUN_DURATION, duration + 1);
        } else {
            player.getCommandSenderWorld().addParticle(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.15F, player.getZ(), 0, 0.25F, 0);
            EntityUtils.applyAttributeModifier(player.getAttribute(Attributes.MOVEMENT_SPEED), new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
            livingEntity.maxUpStep = Math.max(livingEntity.maxUpStep, 1.1F);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttributeModifier(slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED), new AttributeModifier(SPEED_INFO.getRight(),
                SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
        slotContext.getWearer().maxUpStep = 0.6F;
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.CAVE;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 1.25F;
        public int activationTime = 10;
    }
}