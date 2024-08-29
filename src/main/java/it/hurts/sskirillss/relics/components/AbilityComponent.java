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
public record AbilityComponent(@Singular Map<String, StatComponent> stats, ResearchComponent research, int points, int cooldownCap, int cooldown, boolean ticking) {
    public static final AbilityComponent EMPTY = new AbilityComponent(Map.of(), ResearchComponent.EMPTY, 0, 0, 0, false);

    public static final Codec<AbilityComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.unboundedMap(Codec.STRING, StatComponent.CODEC).fieldOf("stats").forGetter(AbilityComponent::stats),
                            ResearchComponent.CODEC.fieldOf("research").forGetter(AbilityComponent::research),
                            Codec.INT.fieldOf("points").forGetter(AbilityComponent::points),
                            Codec.INT.fieldOf("cooldownCap").forGetter(AbilityComponent::cooldownCap),
                            Codec.INT.fieldOf("cooldown").forGetter(AbilityComponent::cooldown),
                            Codec.BOOL.fieldOf("ticking").forGetter(AbilityComponent::ticking)
                    )
                    .apply(instance, AbilityComponent::new)
    );

    public static final StreamCodec<ByteBuf, AbilityComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, StatComponent.STREAM_CODEC), AbilityComponent::stats,
            ResearchComponent.STREAM_CODEC, AbilityComponent::research,
            ByteBufCodecs.INT, AbilityComponent::points,
            ByteBufCodecs.INT, AbilityComponent::cooldownCap,
            ByteBufCodecs.INT, AbilityComponent::cooldown,
            ByteBufCodecs.BOOL, AbilityComponent::ticking,
            AbilityComponent::new
    );
}