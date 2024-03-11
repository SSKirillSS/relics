package it.hurts.sskirillss.relics.client.hud.abilities;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActiveAbilityUtils {
    public static ItemStack getStackInCuriosSlot(Player player, int slot) {
        return CuriosApi.getCuriosHelper().getEquippedCurios(player).map(handler -> handler.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
    }

    public static List<AbilitiesRenderHandler.AbilityEntry> getActiveEntries(Player player) {
        List<AbilitiesRenderHandler.AbilityEntry> entries = new ArrayList<>();

        CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(handler -> {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);

                if (!(stack.getItem() instanceof IRelicItem relic))
                    continue;

                List<String> abilities = getRelicActiveAbilities(stack);

                if (abilities.isEmpty())
                    continue;

                for (String ability : abilities) {
                    if (relic.canUseAbility(stack, ability))
                        entries.add(new AbilitiesRenderHandler.AbilityEntry(slot, ability));
                }
            }
        });

        return entries;
    }

    public static List<String> getRelicActiveAbilities(ItemStack stack) {
        if (!(stack.getItem() instanceof IRelicItem relic))
            return new ArrayList<>();

        List<String> abilities = new ArrayList<>();

        for (Map.Entry<String, AbilityData> ability : relic.getRelicData().getAbilities().getAbilities().entrySet()) {
            if (ability.getValue().getCastData().getKey() == CastType.NONE)
                continue;

            abilities.add(ability.getKey());
        }

        return abilities;
    }
}