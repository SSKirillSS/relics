package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LevelingComponent {
    private int level = 0;
    private int experience = 0;
    private int points = 0;

    public static final Codec<LevelingComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.INT.fieldOf("level").forGetter(LevelingComponent::getLevel),
                            Codec.INT.fieldOf("experience").forGetter(LevelingComponent::getExperience),
                            Codec.INT.fieldOf("points").forGetter(LevelingComponent::getPoints))
                    .apply(instance, LevelingComponent::new)
    );

    public static final StreamCodec<ByteBuf, LevelingComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LevelingComponent::getLevel,
            ByteBufCodecs.INT, LevelingComponent::getExperience,
            ByteBufCodecs.INT, LevelingComponent::getPoints,
            LevelingComponent::new
    );
}