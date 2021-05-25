package it.hurts.sskirillss.relics.crafting;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunicAltarContext extends ItemHandlerWrapper {
    protected final PlayerEntity player;
    protected final List<ItemStack> runes;
    protected final ItemStack relic;
    protected final List<ItemStack> inputs;

    public RunicAltarContext(IItemHandlerModifiable inner, PlayerEntity player, List<ItemStack> runes, ItemStack relic) {
        super(inner);
        this.player = player;
        this.runes = runes;
        this.relic = relic;

        List<ItemStack> inputs = new ArrayList<>(runes);
        inputs.add(relic);
        this.inputs = inputs;
    }

    @Nullable
    public PlayerEntity getPlayer() {
        return player;
    }

    public List<ItemStack> getRunes() {
        return runes;
    }

    public ItemStack getRelic() {
        return relic;
    }

    public List<ItemStack> getInputs() {
        return inputs;
    }
}