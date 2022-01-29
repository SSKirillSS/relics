package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.client.particles.circle.*;
import it.hurts.sskirillss.relics.client.particles.spark.*;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ParticleRegistry {
    public static ParticleType<CircleTintData> CIRCLE_TINT;
    public static ParticleType<SparkTintData> SPARK_TINT;

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientParticleRegistry {
        @SubscribeEvent
        public static void onParticleFactoryRegistration(ParticleFactoryRegisterEvent event) {
            Minecraft.getInstance().particleEngine.register(CIRCLE_TINT, CircleTintFactory::new);
            Minecraft.getInstance().particleEngine.register(SPARK_TINT, SparkTintFactory::new);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ServerParticleRegistry {
        @SubscribeEvent
        public static void onParticleTypeRegistration(RegistryEvent.Register<ParticleType<?>> event) {
            CIRCLE_TINT = new CircleTintFactory.CircleTintType();
            CIRCLE_TINT.setRegistryName(Reference.MODID + ":" + "circle_tint");
            event.getRegistry().register(CIRCLE_TINT);

            SPARK_TINT = new SparkTintFactory.SparkTintType();
            SPARK_TINT.setRegistryName(Reference.MODID + ":" + "spark_tint");
            event.getRegistry().register(SPARK_TINT);
        }
    }
}