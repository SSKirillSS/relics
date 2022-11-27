package it.hurts.sskirillss.relics.client.particles.spark;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.hurts.sskirillss.relics.init.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Locale;

public class SparkTintData implements ParticleOptions {
    private final Color tint;
    private final float diameter;
    private final int lifeTime;

    public SparkTintData(Color tint, float diameter, int lifeTime) {
        this.tint = tint;
        this.lifeTime = lifeTime;
        this.diameter = validateDiameter(diameter);
    }

    public Color getTint() {
        return tint;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public float getDiameter() {
        return diameter;
    }

    @Nonnull
    @Override
    public ParticleType<SparkTintData> getType() {
        return ParticleRegistry.SPARK_TINT.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeInt(tint.getRed());
        buf.writeInt(tint.getGreen());
        buf.writeInt(tint.getBlue());
        buf.writeFloat(diameter);
        buf.writeInt(lifeTime);
    }

    @Nonnull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %i %i %i %.2f %i",
                ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()), tint.getRed(), tint.getGreen(), tint.getBlue(), diameter, lifeTime);
    }

    private static float validateDiameter(float diameter) {
        return (float) Mth.clamp(diameter, 0.05, 5.0);
    }

    public static final Codec<SparkTintData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("tint").forGetter(d -> d.tint.getRGB()),
                    Codec.FLOAT.fieldOf("diameter").forGetter(d -> d.diameter),
                    Codec.INT.fieldOf("life_time").forGetter(d -> d.lifeTime)
            ).apply(instance, SparkTintData::new)
    );

    private SparkTintData(int tintRGB, float diameter, int lifeTime) {
        this.tint = new Color(tintRGB);
        this.lifeTime = lifeTime;
        this.diameter = validateDiameter(diameter);

    }

    public static final ParticleOptions.Deserializer<SparkTintData> DESERIALIZER = new ParticleOptions.Deserializer<SparkTintData>() {
        @Nonnull
        @Override
        public SparkTintData fromCommand(@Nonnull ParticleType<SparkTintData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int red = Mth.clamp(reader.readInt(), 0, 255);
            reader.expect(' ');
            int green = Mth.clamp(reader.readInt(), 0, 255);
            reader.expect(' ');
            int blue = Mth.clamp(reader.readInt(), 0, 255);

            reader.expect(' ');
            float diameter = validateDiameter(reader.readFloat());

            reader.expect(' ');
            int lifeTime = reader.readInt();

            return new SparkTintData(new Color(red, green, blue), diameter, lifeTime);
        }

        @Override
        public SparkTintData fromNetwork(@Nonnull ParticleType<SparkTintData> type, FriendlyByteBuf buf) {
            int red = Mth.clamp(buf.readInt(), 0, 255);
            int green = Mth.clamp(buf.readInt(), 0, 255);
            int blue = Mth.clamp(buf.readInt(), 0, 255);
            Color color = new Color(red, green, blue);

            float diameter = validateDiameter(buf.readFloat());

            int lifeTime = buf.readInt();

            return new SparkTintData(color, diameter, lifeTime);
        }
    };
}