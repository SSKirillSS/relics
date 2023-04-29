package it.hurts.sskirillss.relics.items.relics.belt;

import it.hurts.sskirillss.relics.api.events.leveling.ExperienceAddEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber
public class LeatherBeltItem extends RelicItem {
    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("slots", RelicAbilityEntry.builder()
                                .requiredPoints(2)
                                .stat("talisman", RelicAbilityStat.builder()
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#32a167", "#16702e")
                        .build())
                .build();
    }

    @Override
    public RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return RelicSlotModifier.builder()
                .entry(Pair.of("talisman", (int) Math.round(AbilityUtils.getAbilityValue(stack, "slots", "talisman"))))
                .build();
    }

    @SubscribeEvent
    public static void onExperienceAdded(ExperienceAddEvent event) {
        Player player = event.getEntity();
        ItemStack relic = event.getStack();

        if (player == null || relic.getItem() == ItemRegistry.LEATHER_BELT.get())
            return;

        if (relic.getTags().map(tag -> tag.location().getPath()).anyMatch(tag -> tag.equals("talisman"))) {
            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.LEATHER_BELT.get());

            if (stack.isEmpty())
                return;

            LevelingUtils.addExperience(player, stack, 1);
        }
    }
}