package it.hurts.sskirillss.relics.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.hurts.sskirillss.relics.init.ParticleRegistry;
import lombok.Getter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Locale;

public class BasicColoredParticleOptions implements ParticleOptions {
    @Getter
    private final BasicColoredParticleConstructor data;

    private BasicColoredParticleOptions(int color, float diameter, int lifetime, float roll) {
        this.data = BasicColoredParticleConstructor.builder()
                .color(color)
                .diameter(diameter)
                .lifetime(lifetime)
                .roll(roll)
                .build();
    }

    public BasicColoredParticleOptions(BasicColoredParticleConstructor data) {
        this.data = data;
    }

    @Nonnull
    @Override
    public ParticleType<BasicColoredParticleOptions> getType() {
        return ParticleRegistry.BASIC_COLORED.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeInt(data.getColor().getRGB());
        buf.writeFloat(data.getDiameter());
        buf.writeInt(data.getLifetime());
        buf.writeFloat(data.getRoll());
    }

    @Nonnull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d %.2f %d %.2f",
                ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()), data.getColor().getRGB(), data.getDiameter(), data.getLifetime(), data.getRoll());
    }

    public static final Codec<BasicColoredParticleOptions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("color").forGetter(options -> options.getData().getColor().getRGB()),
                    Codec.FLOAT.fieldOf("diameter").forGetter(options -> options.getData().getDiameter()),
                    Codec.INT.fieldOf("lifetime").forGetter(options -> options.getData().getLifetime()),
                    Codec.FLOAT.fieldOf("roll").forGetter(options -> options.getData().getRoll())
            ).apply(instance, BasicColoredParticleOptions::new));

    public static final ParticleOptions.Deserializer<BasicColoredParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Nonnull
        @Override
        public BasicColoredParticleOptions fromCommand(@Nonnull ParticleType<BasicColoredParticleOptions> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int color = reader.readInt();

            reader.expect(' ');
            float diameter = reader.readFloat();

            reader.expect(' ');
            int lifetime = reader.readInt();

            reader.expect(' ');
            float roll = reader.readFloat();

            return new BasicColoredParticleOptions(color, diameter, lifetime, roll);
        }

        @Override
        public BasicColoredParticleOptions fromNetwork(@Nonnull ParticleType<BasicColoredParticleOptions> type, FriendlyByteBuf buf) {
            int color = buf.readInt();
            float diameter = buf.readFloat();
            int lifetime = buf.readInt();
            float roll = buf.readFloat();

            return new BasicColoredParticleOptions(color, diameter, lifetime, roll);
        }
    };
}
