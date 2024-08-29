package it.hurts.sskirillss.relics.components.utils;

import com.mojang.serialization.Codec;
import it.hurts.sskirillss.relics.components.misc.UnboundedMultimapCodec;

public interface RelicsCodec<A> extends Codec<A> {
    static <K, V> UnboundedMultimapCodec<K, V> unboundedMultimap(final Codec<K> keyCodec, final Codec<V> elementCodec) {
        return new UnboundedMultimapCodec<>(keyCodec, elementCodec);
    }
}