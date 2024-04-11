package it.hurts.sskirillss.relics.utils;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class TickerUtils {
    @Nullable
    public static <A extends BlockEntity, T extends BlockEntity> BlockEntityTicker<T> getTicker(BlockEntityType<T> innerType, BlockEntityType<A> targetType, BlockEntityTicker<? super A> ticker) {
        return targetType == innerType ? (BlockEntityTicker<T>) ticker : null;
    }
}