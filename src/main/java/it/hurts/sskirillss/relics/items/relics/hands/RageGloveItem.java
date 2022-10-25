package it.hurts.sskirillss.relics.items.relics.hands;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class RageGloveItem extends RelicItem {
    public static final String TAG_STACKS = "stacks";
    public static final String TAG_TIME = "time";

    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("rage", RelicAbilityEntry.builder()
                                .stat("incoming_damage", RelicAbilityStat.builder()
                                        .initialValue(0.02D, 0.05D)
                                        .upgradeModifier("add", -0.0015D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(MathUtils.round(value, 3) * 100, 3)))
                                        .build())
                                .stat("dealt_damage", RelicAbilityStat.builder()
                                        .initialValue(0.025D, 0.1D)
                                        .upgradeModifier("add", 0.0025D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(MathUtils.round(value, 3) * 100, 3)))
                                        .build())
                                .stat("movement_speed", RelicAbilityStat.builder()
                                        .initialValue(0.01D, 0.025D)
                                        .upgradeModifier("add", 0.005D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(MathUtils.round(value, 3) * 100, 3)))
                                        .build())
                                .stat("attack_speed", RelicAbilityStat.builder()
                                        .initialValue(0.005D, 0.01D)
                                        .upgradeModifier("add", 0.005D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(MathUtils.round(value, 3) * 100, 3)))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#eed551", "#dcbe1d")
                        .build())
                .build();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player) || player.tickCount % 20 != 0 || DurabilityUtils.isBroken(stack))
            return;

        int stacks = NBTUtils.getInt(stack, TAG_STACKS, 0);
        int time = NBTUtils.getInt(stack, TAG_TIME, 0);

        if (stacks <= 0)
            return;

        EntityUtils.resetAttribute(player, stack, Attributes.ATTACK_SPEED, (float) (stacks * getAbilityValue(stack, "rage", "attack_speed")), AttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityUtils.resetAttribute(player, stack, Attributes.MOVEMENT_SPEED, (float) (stacks * getAbilityValue(stack, "rage", "movement_speed")), AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (time > 0)
            NBTUtils.setInt(stack, TAG_TIME, --time);
        else {
            NBTUtils.setInt(stack, TAG_STACKS, 0);

            addExperience(player, stack, (int) Math.floor(stacks / 3F));

            EntityUtils.removeAttribute(player, stack, Attributes.ATTACK_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityUtils.removeAttribute(player, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class RageGloveEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Entity source = event.getSource().getEntity();

            if (source instanceof Player player) {
                if (!(event.getSource().getDirectEntity() instanceof Player))
                    return;

                ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.RAGE_GLOVE.get());

                if (stack.isEmpty())
                    return;

                int stacks = NBTUtils.getInt(stack, TAG_STACKS, 0);

                NBTUtils.setInt(stack, TAG_STACKS, ++stacks);
                NBTUtils.setInt(stack, TAG_TIME, 3);

                event.setAmount((float) (event.getAmount() + (event.getAmount() * (stacks * getAbilityValue(stack, "rage", "dealt_damage")))));
            } else {
                ItemStack stack = EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.RAGE_GLOVE.get());

                if (stack.isEmpty())
                    return;

                int stacks = NBTUtils.getInt(stack, TAG_STACKS, 0);

                if (stacks <= 0)
                    return;

                event.setAmount((float) (event.getAmount() + (event.getAmount() * (stacks * getAbilityValue(stack, "rage", "incoming_damage")))));
            }
        }
    }
}