package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Builder(toBuilder = true)
public record LockComponent(int unlocks) {
    public static final LockComponent EMPTY = new LockComponent(0);

    public static final Codec<LockComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.INT.fieldOf("breaks").forGetter(LockComponent::unlocks))
                    .apply(instance, LockComponent::new)
    );

    public static final StreamCodec<ByteBuf, LockComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LockComponent::unlocks,
            LockComponent::new
    );
}