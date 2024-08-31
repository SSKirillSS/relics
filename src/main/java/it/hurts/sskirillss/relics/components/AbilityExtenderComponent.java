package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Builder(toBuilder = true)
public record AbilityExtenderComponent(int cooldownCap, int cooldown, boolean ticking) {
    public static final AbilityExtenderComponent EMPTY = new AbilityExtenderComponent(0, 0, false);

    public static final Codec<AbilityExtenderComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.INT.fieldOf("cooldownCap").forGetter(AbilityExtenderComponent::cooldownCap),
                            Codec.INT.fieldOf("cooldown").forGetter(AbilityExtenderComponent::cooldown),
                            Codec.BOOL.fieldOf("ticking").forGetter(AbilityExtenderComponent::ticking))
                    .apply(instance, AbilityExtenderComponent::new)
    );

    public static final StreamCodec<ByteBuf, AbilityExtenderComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AbilityExtenderComponent::cooldownCap,
            ByteBufCodecs.INT, AbilityExtenderComponent::cooldown,
            ByteBufCodecs.BOOL, AbilityExtenderComponent::ticking,
            AbilityExtenderComponent::new
    );
}