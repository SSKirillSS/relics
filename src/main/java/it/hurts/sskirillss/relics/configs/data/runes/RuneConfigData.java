package it.hurts.sskirillss.relics.configs.data.runes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuneConfigData {
    private List<String> ingredients;

    @Builder.Default
    private RuneLootData loot = new RuneLootData();

    public static class RuneConfigDataBuilder {
        public RuneConfigDataBuilder ingredients(List<Item> values) {
            ingredients = values.stream()
                    .map(item -> item.getRegistryName().getNamespace() + ":" + item.getRegistryName().getPath())
                    .collect(Collectors.toList());

            return this;
        }
    }
}