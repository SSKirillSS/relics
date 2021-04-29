package it.hurts.sskirillss.relics.utils;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class RelicsTab  extends ItemGroup {
    public static final ItemGroup RELICS_TAB = new RelicsTab(Reference.MODID);

    public RelicsTab(String label) {
        super(label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ItemRegistry.TERRASTEEL_INGOT.get());
    }
}