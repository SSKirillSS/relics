package it.hurts.sskirillss.relics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.minecraft.network.codec.StreamCodec;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DataComponent {
    private AbilitiesComponent abilities = new AbilitiesComponent();

    private LevelingComponent leveling = new LevelingComponent();

    public static final Codec<DataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(AbilitiesComponent.CODEC.fieldOf("abilities").forGetter(DataComponent::getAbilities),
                            LevelingComponent.CODEC.fieldOf("leveling").forGetter(DataComponent::getLeveling))
                    .apply(instance, DataComponent::new)
    );

    public static final StreamCodec<ByteBuf, DataComponent> STREAM_CODEC = StreamCodec.composite(
            AbilitiesComponent.STREAM_CODEC, DataComponent::getAbilities,
            LevelingComponent.STREAM_CODEC, DataComponent::getLeveling,
            DataComponent::new
    );
}