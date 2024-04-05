package it.hurts.sskirillss.relics.system.casts.abilities;

import it.hurts.sskirillss.relics.system.casts.slots.base.SlotReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.nbt.CompoundTag;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbilityReference {
    private String id;

    private SlotReference slot;

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("id", id);
        tag.put("slot", slot.serializeNBT());

        return tag;
    }

    @SneakyThrows
    public AbilityReference deserializeNBT(CompoundTag tag) {
        this.id = tag.getString("id");

        CompoundTag slotTag = tag.getCompound("slot");

        this.slot = ((SlotReference) Class.forName(slotTag.getString("class")).getConstructor().newInstance()).deserializeNBT(slotTag);

        return this;
    }
}