package it.hurts.sskirillss.relics.components;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.hurts.sskirillss.relics.components.utils.RelicsByteBufCodecs;
import it.hurts.sskirillss.relics.components.utils.RelicsCodec;
import lombok.Builder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Builder(toBuilder = true)
public record ResearchComponent(Multimap<Integer, Integer> links, boolean researched) {
    public static final ResearchComponent EMPTY = new ResearchComponent(LinkedHashMultimap.create(), false);

    public static final Codec<ResearchComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(RelicsCodec.unboundedMultimap(Codec.INT, Codec.INT).fieldOf("abilities").forGetter(ResearchComponent::links),
                            Codec.BOOL.fieldOf("researched").forGetter(ResearchComponent::researched))
                    .apply(instance, ResearchComponent::new)
    );

    public static final StreamCodec<ByteBuf, ResearchComponent> STREAM_CODEC = StreamCodec.composite(
            RelicsByteBufCodecs.multimap(HashMultimap::create, ByteBufCodecs.INT, ByteBufCodecs.INT), ResearchComponent::links,
            ByteBufCodecs.BOOL, ResearchComponent::researched,
            ResearchComponent::new
    );
}