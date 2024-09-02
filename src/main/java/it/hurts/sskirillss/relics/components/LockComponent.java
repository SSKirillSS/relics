package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;

@Builder(toBuilder = true)
public record LockComponent(int unlocks) {
    public static final LockComponent EMPTY = new LockComponent(0);

    public static final Codec<LockComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.INT.fieldOf("breaks").forGetter(LockComponent::unlocks))
                    .apply(instance, LockComponent::new)
    );
}