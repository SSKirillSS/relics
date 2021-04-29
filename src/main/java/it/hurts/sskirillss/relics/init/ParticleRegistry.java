package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.particles.CircleTintFactory;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ParticleRegistry {
    public static ParticleType<CircleTintData> CIRCLE_TINT;

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientParticleRegistry {
        @SubscribeEvent
        public static void onParticleFactoryRegistration(ParticleFactoryRegisterEvent event) {
            Minecraft.getInstance().particleEngine.register(CIRCLE_TINT, CircleTintFactory::new);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ServerParticleRegistry {
        @SubscribeEvent
        public static void onParticleTypeRegistration(RegistryEvent.Register<ParticleType<?>> event) {
            CIRCLE_TINT = new CircleTintFactory.CircleTintType();
            CIRCLE_TINT.setRegistryName(Reference.MODID + ":" + "circle_tint");
            event.getRegistry().register(CIRCLE_TINT);
        }
    }
}