package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintFactory;
import it.hurts.sskirillss.relics.client.particles.spark.SparkTintData;
import it.hurts.sskirillss.relics.client.particles.spark.SparkTintFactory;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Reference.MODID);

    public static final RegistryObject<ParticleType<CircleTintData>> CIRCLE_TINT = PARTICLES.register("circle_tint", CircleTintFactory.CircleTintType::new);
    public static final RegistryObject<ParticleType<SparkTintData>> SPARK_TINT = PARTICLES.register("spark_tint", SparkTintFactory.SparkTintType::new);

    public static void registerParticles() {
        PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}