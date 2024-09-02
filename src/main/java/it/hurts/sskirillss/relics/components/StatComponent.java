package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;

@Builder(toBuilder = true)
public record StatComponent(double initialValue) {
    public static final StatComponent EMPTY = new StatComponent(0D);

    public static final Codec<StatComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.DOUBLE.fieldOf("initialValue").forGetter(StatComponent::initialValue))
                    .apply(instance, StatComponent::new)
    );
}