package it.hurts.sskirillss.relics.items.relics.base.data.style;

import it.hurts.sskirillss.relics.items.relics.base.data.style.misc.Backgrounds;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

@Data
@Builder
public class StyleData {
    @Builder.Default
    private ResourceLocation background = Backgrounds.DEFAULT;

    @Builder.Default
    private BiFunction<Player, ItemStack, TooltipData> tooltip;

    public static class StyleDataBuilder {
        private BiFunction<Player, ItemStack, TooltipData> tooltip = (player, stack) -> TooltipData.builder().build();

        public StyleDataBuilder tooltip(TooltipData tooltip) {
            this.tooltip = (player, stack) -> tooltip;

            return this;
        }

        public StyleDataBuilder tooltip(BiFunction<Player, ItemStack, TooltipData> tooltip) {
            this.tooltip = tooltip;

            return this;
        }
    }
}