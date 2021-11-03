package it.hurts.sskirillss.relics.client.particles.spark;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.hurts.sskirillss.relics.init.ParticleRegistry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Locale;

public class SparkTintData implements IParticleData {
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
        return ParticleRegistry.SPARK_TINT;
    }

    @Override
    public void writeToNetwork(PacketBuffer buf) {
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
                this.getType().getRegistryName(), tint.getRed(), tint.getGreen(), tint.getBlue(), diameter, lifeTime);
    }

    private static float validateDiameter(float diameter) {
        return (float) MathHelper.clamp(diameter, 0.05, 5.0);
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

    public static final IDeserializer<SparkTintData> DESERIALIZER = new IDeserializer<SparkTintData>() {
        @Nonnull
        @Override
        public SparkTintData fromCommand(@Nonnull ParticleType<SparkTintData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int red = MathHelper.clamp(reader.readInt(), 0, 255);
            reader.expect(' ');
            int green = MathHelper.clamp(reader.readInt(), 0, 255);
            reader.expect(' ');
            int blue = MathHelper.clamp(reader.readInt(), 0, 255);

            reader.expect(' ');
            float diameter = validateDiameter(reader.readFloat());

            reader.expect(' ');
            int lifeTime = reader.readInt();

            return new SparkTintData(new Color(red, green, blue), diameter, lifeTime);
        }

        @Override
        public SparkTintData fromNetwork(@Nonnull ParticleType<SparkTintData> type, PacketBuffer buf) {
            int red = MathHelper.clamp(buf.readInt(), 0, 255);
            int green = MathHelper.clamp(buf.readInt(), 0, 255);
            int blue = MathHelper.clamp(buf.readInt(), 0, 255);
            Color color = new Color(red, green, blue);

            float diameter = validateDiameter(buf.readFloat());

            int lifeTime = buf.readInt();

            return new SparkTintData(color, diameter, lifeTime);
        }
    };
}