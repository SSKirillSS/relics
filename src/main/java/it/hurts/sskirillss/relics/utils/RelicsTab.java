package it.hurts.sskirillss.relics.utils;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RelicsTab  extends ItemGroup {
    public static final ItemGroup RELICS_TAB = new RelicsTab(Reference.MODID);

    public RelicsTab(String label) {
        super(label);
    }

    @Override
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(ItemRegistry.BASTION_RING.get());
    }
}