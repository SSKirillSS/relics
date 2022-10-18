package it.hurts.sskirillss.relics.tiles.base;

import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileBase extends BlockEntity {
    public TileBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null)
            load(pkt.getTag());
    }

    public double getX() {
        return this.getBlockPos().getX();
    }

    public double getY() {
        return this.getBlockPos().getY();
    }

    public double getZ() {
        return this.getBlockPos().getZ();
    }
}