package it.hurts.sskirillss.relics.items.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreativeContentConstructor {
    private final List<CreativeContentData> entries = new ArrayList<>();

    public void entry(CreativeModeTab tab, CreativeModeTab.TabVisibility visibility, ItemStack... items) {
        entries.add(new CreativeContentData(tab, visibility, Arrays.asList(items)));
    }

    public void entry(CreativeModeTab tab, CreativeModeTab.TabVisibility visibility, ItemLike... items) {
        this.entry(tab, visibility, Arrays.stream(items).map(ItemStack::new).toArray(ItemStack[]::new));
    }

    @Getter
    @AllArgsConstructor
    public static class CreativeContentData {
        private final CreativeModeTab tab;

        private final CreativeModeTab.TabVisibility visibility;

        private final List<ItemStack> stacks;
    }
}