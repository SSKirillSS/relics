package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.client.particle.RainbowFireParticle;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Reference.MODID);

    public static final RegistryObject<SimpleParticleType> RAINBOW_FIRE = PARTICLES.register("rainbow_fire", () -> new SimpleParticleType(true));

    public static void register() {
        PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void onParticleRegistry(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(RAINBOW_FIRE.get(), RainbowFireParticle.Factory::new);
    }

}