package it.hurts.sskirillss.relics.items.relics.base.data.cast.data;

import it.hurts.sskirillss.relics.utils.Reference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PredicateInfo {
    @Builder.Default
    private List<Object> placeholders;

    private ResourceLocation icon;

    private Boolean condition;

    public static class PredicateInfoBuilder {
        private List<Object> placeholders = new ArrayList<>();

        public PredicateInfoBuilder description(Object... placeholders) {
            this.placeholders.addAll(Arrays.asList(placeholders));

            return this;
        }

        public PredicateInfoBuilder icon(Item item, String name) {
            this.icon = new ResourceLocation(Reference.MODID, "textures/gui/description/icons/" + ForgeRegistries.ITEMS.getKey(item).getPath() + "/" + name + ".png");

            return this;
        }
    }
}