package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import net.minecraft.network.codec.StreamCodec;

@Builder(toBuilder = true)
public record DataComponent(AbilitiesComponent abilities, LevelingComponent leveling) {
    public static final DataComponent EMPTY = new DataComponent(AbilitiesComponent.EMPTY, LevelingComponent.EMPTY);

    public static final Codec<DataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(AbilitiesComponent.CODEC.fieldOf("abilities").forGetter(DataComponent::abilities),
                            LevelingComponent.CODEC.fieldOf("leveling").forGetter(DataComponent::leveling))
                    .apply(instance, DataComponent::new)
    );

    public static final StreamCodec<ByteBuf, DataComponent> STREAM_CODEC = StreamCodec.composite(
            AbilitiesComponent.STREAM_CODEC, DataComponent::abilities,
            LevelingComponent.STREAM_CODEC, DataComponent::leveling,
            DataComponent::new
    );
}