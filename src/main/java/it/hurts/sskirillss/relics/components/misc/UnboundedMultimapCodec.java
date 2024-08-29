package it.hurts.sskirillss.relics.components.misc;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

public record UnboundedMultimapCodec<K, V>(Codec<K> keyCodec, Codec<V> elementCodec) implements BaseMultimapCodec<K, V>, Codec<Multimap<K, V>> {
    @Override
    public <T> DataResult<Pair<Multimap<K, V>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> decode(ops, map)).map(r -> Pair.of(r, input));
    }

    @Override
    public <T> DataResult<T> encode(final Multimap<K, V> input, final DynamicOps<T> ops, final T prefix) {
        return encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    @Override
    public String toString() {
        return "UnboundedMultimapCodec[" + keyCodec + " -> " + elementCodec + ']';
    }
}