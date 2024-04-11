package it.hurts.sskirillss.relics.capability.entries;

import it.hurts.sskirillss.relics.init.CapabilityRegistry;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public interface IRelicsCapability extends INBTSerializable<CompoundTag> {
    CompoundTag getResearchData();

    void setResearchData(CompoundTag data);

    class RelicsCapability implements IRelicsCapability {
        @Getter
        @Setter
        private CompoundTag researchData = new CompoundTag();

        @Override
        public CompoundTag serializeNBT() {
            final CompoundTag tag = new CompoundTag();

            tag.put("researchData", this.researchData);

            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.researchData = nbt.getCompound("researchData");
        }
    }

    class RelicsCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final IRelicsCapability backend = new IRelicsCapability.RelicsCapability();

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return CapabilityRegistry.DATA.orEmpty(cap, LazyOptional.of(() -> backend));
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT(nbt);
        }
    }
}