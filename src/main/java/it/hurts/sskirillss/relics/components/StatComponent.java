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
public class StatComponent {
    private double initialValue = 0D;

    public static final Codec<StatComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.DOUBLE.fieldOf("initialValue").forGetter(StatComponent::getInitialValue))
                    .apply(instance, StatComponent::new)
    );

    public static final StreamCodec<ByteBuf, StatComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, StatComponent::getInitialValue,
            StatComponent::new
    );
}