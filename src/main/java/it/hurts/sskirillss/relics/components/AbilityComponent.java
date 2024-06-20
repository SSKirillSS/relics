package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AbilityComponent {
    private Map<String, StatComponent> stats = new HashMap<>();

    private int points = 0;

    private int cooldownCap = 0;
    private int cooldown = 0;

    private boolean ticking = false;

    public static final Codec<AbilityComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.unboundedMap(Codec.STRING, StatComponent.CODEC).fieldOf("stats").forGetter(AbilityComponent::getStats),
                            Codec.INT.fieldOf("points").forGetter(AbilityComponent::getPoints),
                            Codec.INT.fieldOf("cooldownCap").forGetter(AbilityComponent::getCooldownCap),
                            Codec.INT.fieldOf("cooldown").forGetter(AbilityComponent::getCooldown),
                            Codec.BOOL.fieldOf("ticking").forGetter(AbilityComponent::isTicking)
                    )
                    .apply(instance, AbilityComponent::new)
    );

    public static final StreamCodec<ByteBuf, AbilityComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, StatComponent.STREAM_CODEC), AbilityComponent::getStats,
            ByteBufCodecs.INT, AbilityComponent::getPoints,
            ByteBufCodecs.INT, AbilityComponent::getCooldownCap,
            ByteBufCodecs.INT, AbilityComponent::getCooldown,
            ByteBufCodecs.BOOL, AbilityComponent::isTicking,
            AbilityComponent::new
    );
}