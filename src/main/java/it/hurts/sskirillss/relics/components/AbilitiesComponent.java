package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder(toBuilder = true)
public record AbilitiesComponent(@Singular Map<String, AbilityComponent> abilities) {
    public static final AbilitiesComponent EMPTY = new AbilitiesComponent(Map.of());

    public static final Codec<AbilitiesComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.unboundedMap(Codec.STRING, AbilityComponent.CODEC).fieldOf("abilities").forGetter(AbilitiesComponent::abilities))
                    .apply(instance, AbilitiesComponent::new)
    );
}