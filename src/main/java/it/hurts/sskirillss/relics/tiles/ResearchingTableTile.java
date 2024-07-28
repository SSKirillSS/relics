package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.init.TileRegistry;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ResearchingTableTile extends BlockEntity {
    @Getter
    @Setter
    private ItemStack stack = ItemStack.EMPTY;

    public int ticksExisted;

    public ResearchingTableTile(BlockPos pos, BlockState state) {
        super(TileRegistry.RESEARCHING_TABLE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ResearchingTableTile tile) {
        if (level == null)
            return;

        tile.ticksExisted++;
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);

        stack = ItemStack.parseOptional(provider, compound.getCompound("stack"));
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);

        if (stack != null && !stack.isEmpty())
            compound.put("stack", stack.save(provider, new CompoundTag()));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);

        this.saveAdditional(tag, provider);

        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}