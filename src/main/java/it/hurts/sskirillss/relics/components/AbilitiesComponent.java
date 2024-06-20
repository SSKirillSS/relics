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
public class AbilitiesComponent {
    private Map<String, AbilityComponent> abilities = new HashMap<>();

    public static final Codec<AbilitiesComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.unboundedMap(Codec.STRING, AbilityComponent.CODEC).fieldOf("abilities").forGetter(AbilitiesComponent::getAbilities))
                    .apply(instance, AbilitiesComponent::new)
    );

    public static final StreamCodec<ByteBuf, AbilitiesComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, AbilityComponent.STREAM_CODEC), AbilitiesComponent::getAbilities,
            AbilitiesComponent::new
    );
}