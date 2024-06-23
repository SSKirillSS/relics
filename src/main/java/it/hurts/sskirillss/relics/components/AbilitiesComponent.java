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
public record AbilitiesComponent(@Singular Map<String, AbilityComponent> abilities) {
    public static final AbilitiesComponent EMPTY = new AbilitiesComponent(Map.of());

    public static final Codec<AbilitiesComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.unboundedMap(Codec.STRING, AbilityComponent.CODEC).fieldOf("abilities").forGetter(AbilitiesComponent::abilities))
                    .apply(instance, AbilitiesComponent::new)
    );

    public static final StreamCodec<ByteBuf, AbilitiesComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, AbilityComponent.STREAM_CODEC), AbilitiesComponent::abilities,
            AbilitiesComponent::new
    );
}