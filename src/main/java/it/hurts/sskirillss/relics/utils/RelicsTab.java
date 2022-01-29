package it.hurts.sskirillss.relics.utils;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RelicsTab  extends CreativeModeTab {
    public static final CreativeModeTab RELICS_TAB = new RelicsTab(Reference.MODID);

    public RelicsTab(String label) {
        super(label);
    }

    @Override
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(ItemRegistry.BASTION_RING.get());
    }
}