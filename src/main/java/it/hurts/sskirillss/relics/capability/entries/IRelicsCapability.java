package it.hurts.sskirillss.relics.capability.entries;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public interface IRelicsCapability extends INBTSerializable<CompoundTag> {
    CompoundTag getResearchData();

    void setResearchData(CompoundTag data);

    class RelicsCapability implements IRelicsCapability {
        @Getter
        @Setter
        private CompoundTag researchData = new CompoundTag();

        @Override
        public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
            final CompoundTag tag = new CompoundTag();

            tag.put("researchData", this.researchData);

            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            this.researchData = nbt.getCompound("researchData");
        }
    }

    class RelicsCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final IRelicsCapability backend = new IRelicsCapability.RelicsCapability();

        @Nullable
        @Override
        public Object getCapability(Object object, Object context) {
            return backend;
        }

        @Override
        public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
            return this.backend.serializeNBT(provider);
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            this.backend.deserializeNBT(provider, nbt);
        }
    }
}