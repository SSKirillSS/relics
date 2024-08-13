package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Builder(toBuilder = true)
public record LevelingComponent(int level, int experience, int points, int luck) {
    public static final LevelingComponent EMPTY = new LevelingComponent(0, 0, 0, 0);

    public static final Codec<LevelingComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.INT.fieldOf("level").forGetter(LevelingComponent::level),
                            Codec.INT.fieldOf("experience").forGetter(LevelingComponent::experience),
                            Codec.INT.fieldOf("points").forGetter(LevelingComponent::points),
                            Codec.INT.fieldOf("luck").forGetter(LevelingComponent::luck))
                    .apply(instance, LevelingComponent::new)
    );

    public static final StreamCodec<ByteBuf, LevelingComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LevelingComponent::level,
            ByteBufCodecs.INT, LevelingComponent::experience,
            ByteBufCodecs.INT, LevelingComponent::points,
            ByteBufCodecs.INT, LevelingComponent::luck,
            LevelingComponent::new
    );
}