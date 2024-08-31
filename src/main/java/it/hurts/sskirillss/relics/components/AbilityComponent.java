package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Builder;
import lombok.Singular;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

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

    public static final StreamCodec<ByteBuf, AbilityComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, StatComponent.STREAM_CODEC), AbilityComponent::stats,
            ResearchComponent.STREAM_CODEC, AbilityComponent::research,
            LockComponent.STREAM_CODEC, AbilityComponent::lock,
            AbilityExtenderComponent.STREAM_CODEC, AbilityComponent::extender,
            ByteBufCodecs.INT, AbilityComponent::points,
            AbilityComponent::new
    );
}