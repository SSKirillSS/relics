package it.hurts.sskirillss.relics.components.misc;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;

import java.util.stream.Stream;

public interface BaseMultimapCodec<K, V> {
    Codec<K> keyCodec();

    Codec<V> elementCodec();

    default <T> DataResult<Multimap<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        final ImmutableMultimap.Builder<K, V> read = ImmutableMultimap.builder();
        final Stream.Builder<Pair<T, T>> failed = Stream.builder();

        final DataResult<Unit> result = input.entries().reduce(
                DataResult.success(Unit.INSTANCE, Lifecycle.stable()), (r, pair) -> {
                    final DataResult<Pair<K, V>> entryResult = keyCodec().parse(ops, pair.getFirst()).apply2stable(Pair::of, elementCodec().parse(ops, pair.getSecond()));

                    entryResult.resultOrPartial().ifPresent(kvPair -> read.put(kvPair.getFirst(), kvPair.getSecond()));

                    if (entryResult.isError())
                        failed.add(pair);

                    return r.apply2stable((u, p) -> u, entryResult);
                }, (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
        );

        final Multimap<K, V> elements = read.build();

        return result.map(unit -> elements).setPartial(elements).mapError(e -> e + " missed input: " + ops.createMap(failed.build()));
    }

    default <T> RecordBuilder<T> encode(final Multimap<K, V> input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        for (final K key : input.keySet())
            for (final V value : input.get(key))
                prefix.add(keyCodec().encodeStart(ops, key), elementCodec().encodeStart(ops, value));

        return prefix;
    }
}