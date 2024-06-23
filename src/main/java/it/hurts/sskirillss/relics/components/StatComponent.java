package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Builder(toBuilder = true)
public record StatComponent(double initialValue) {
    public static final StatComponent EMPTY = new StatComponent(0D);

    public static final Codec<StatComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.DOUBLE.fieldOf("initialValue").forGetter(StatComponent::initialValue))
                    .apply(instance, StatComponent::new)
    );

    public static final StreamCodec<ByteBuf, StatComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, StatComponent::initialValue,
            StatComponent::new
    );
}