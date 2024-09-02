package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Builder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
public record ResearchComponent(Map<String, List<Integer>> links, boolean researched) {
    public static final ResearchComponent EMPTY = new ResearchComponent(new HashMap<>(), false);

    public static final Codec<ResearchComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.unboundedMap(Codec.STRING, Codec.list(Codec.INT)).fieldOf("links").forGetter(ResearchComponent::links),
                    Codec.BOOL.fieldOf("researched").forGetter(ResearchComponent::researched)
            ).apply(instance, ResearchComponent::new)
    );

    public static final StreamCodec<ByteBuf, ResearchComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT.apply(ByteBufCodecs.list())), ResearchComponent::links,
            ByteBufCodecs.BOOL, ResearchComponent::researched,
            ResearchComponent::new
    );
}