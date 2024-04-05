package it.hurts.sskirillss.relics.system.casts.slots.base;

import lombok.Data;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Data
public class SlotReference {
    public ItemStack gatherStack(Player player) {
        return ItemStack.EMPTY;
    };

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("class", this.getClass().getName());

        return tag;
    };

    public SlotReference deserializeNBT(CompoundTag tag) {
        return this;
    };
}