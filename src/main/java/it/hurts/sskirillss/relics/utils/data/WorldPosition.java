package it.hurts.sskirillss.relics.utils.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Data
@AllArgsConstructor
public class WorldPosition {
    private ResourceKey<Level> level;
    private Vec3 pos;

    public static final Codec<WorldPosition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ResourceKey.codec(Registries.DIMENSION).fieldOf("level").forGetter(WorldPosition::getLevel),
                            Vec3.CODEC.fieldOf("pos").forGetter(WorldPosition::getPos))
                    .apply(instance, WorldPosition::new)
    );

//    public static final StreamCodec<ByteBuf, WorldPosition> STREAM_CODEC = StreamCodec.composite(
//            ResourceKey.streamCodec(Registries.DIMENSION), WorldPosition::getLevel,
//            Vec3.CODEC, WorldPosition::getPos,
//            WorldPosition::new
//    );
}