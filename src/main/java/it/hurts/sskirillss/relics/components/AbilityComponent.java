package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder(toBuilder = true)
public record AbilityComponent(@Singular Map<String, StatComponent> stats, ResearchComponent research, LockComponent lock, AbilityExtenderComponent extender, int points) {
    public static final AbilityComponent EMPTY = new AbilityComponent(Map.of(), ResearchComponent.EMPTY, LockComponent.EMPTY, AbilityExtenderComponent.EMPTY, 0);

    public static final Codec<AbilityComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.unboundedMap(Codec.STRING, StatComponent.CODEC).fieldOf("stats").forGetter(AbilityComponent::stats),
                            ResearchComponent.CODEC.fieldOf("research").forGetter(AbilityComponent::research),
                            LockComponent.CODEC.fieldOf("lock").forGetter(AbilityComponent::lock),
                            AbilityExtenderComponent.CODEC.fieldOf("extender").forGetter(AbilityComponent::extender),
                            Codec.INT.fieldOf("points").forGetter(AbilityComponent::points))
                    .apply(instance, AbilityComponent::new)
    );
}