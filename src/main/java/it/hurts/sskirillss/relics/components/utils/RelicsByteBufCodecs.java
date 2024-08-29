package it.hurts.sskirillss.relics.components.utils;

import com.google.common.collect.Multimap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public interface RelicsByteBufCodecs {
    static <B extends ByteBuf, K, V, M extends Multimap<K, V>> StreamCodec<B, M> multimap(Supplier<? extends M> factory, StreamCodec<? super B, K> keyCodec, StreamCodec<? super B, V> valueCodec) {
        return multimap(factory, keyCodec, valueCodec, Integer.MAX_VALUE);
    }

    static <B extends ByteBuf, K, V, M extends Multimap<K, V>> StreamCodec<B, M> multimap(final Supplier<? extends M> factory, final StreamCodec<? super B, K> keyCodec, final StreamCodec<? super B, V> valueCodec, final int maxSize) {
        return new StreamCodec<B, M>() {
            @Override
            public void encode(B buf, M multimap) {
                ByteBufCodecs.writeCount(buf, multimap.size(), maxSize);

                multimap.forEach((key, value) -> {
                    keyCodec.encode(buf, key);
                    valueCodec.encode(buf, value);
                });
            }

            @Override
            public M decode(B buf) {
                M multimap = factory.get();

                for (int i = 0; i < ByteBufCodecs.readCount(buf, maxSize); i++)
                    multimap.put(keyCodec.decode(buf), valueCodec.decode(buf));

                return multimap;
            }
        };
    }
}