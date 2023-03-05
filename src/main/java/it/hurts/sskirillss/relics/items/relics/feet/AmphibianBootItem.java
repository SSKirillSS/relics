package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;

public class AmphibianBootItem extends RelicItem {
    private static final String TAG_DURATION = "duration";

    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("swimming", RelicAbilityEntry.builder()
                                .stat("speed", RelicAbilityStat.builder()
                                        .initialValue(0.005D, 0.01D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 3) * 100 * 20))
                                        .build())
                                .stat("duration", RelicAbilityStat.builder()
                                        .initialValue(15D, 35D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value / 5, 1))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#ff6900", "#ff2e00")
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        int duration = NBTUtils.getInt(stack, TAG_DURATION, 0);

        if (player.isSwimming()) {
            if (player.tickCount % 20 == 0)
                addExperience(player, stack, 1);

            if (duration < getAbilityValue(stack, "swimming", "duration"))
                NBTUtils.setInt(stack, TAG_DURATION, duration + 1);
        } else if (duration > 0)
            NBTUtils.setInt(stack, TAG_DURATION, --duration);

        EntityUtils.removeAttribute(player, stack, ForgeMod.SWIM_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (duration > 0)
            EntityUtils.applyAttribute(player, stack, ForgeMod.SWIM_SPEED.get(), (float) (duration * getAbilityValue(stack, "swimming", "speed")), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (stack.getItem() == newStack.getItem())
            return;

        EntityUtils.removeAttribute(slotContext.entity(), stack, ForgeMod.SWIM_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}