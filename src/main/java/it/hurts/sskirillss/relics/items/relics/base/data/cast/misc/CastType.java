package it.hurts.sskirillss.relics.items.relics.base.data.cast.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

@Getter
@AllArgsConstructor
public enum CastType {
    NONE(0),
    INSTANTANEOUS(1),
    INTERRUPTIBLE(2),
    CYCLICAL(3),
    TOGGLEABLE(4),
    CHARGEABLE(5);

    public static final IntFunction<CastType> BY_ID = ByIdMap.continuous(CastType::getId, CastType.values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;
}