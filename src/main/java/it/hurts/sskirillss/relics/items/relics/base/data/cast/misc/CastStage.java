package it.hurts.sskirillss.relics.items.relics.base.data.cast.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

@Getter
@AllArgsConstructor
public enum CastStage {
    START(0),
    TICK(1),
    END(2);

    public static final IntFunction<CastStage> BY_ID = ByIdMap.continuous(CastStage::getId, CastStage.values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;
}