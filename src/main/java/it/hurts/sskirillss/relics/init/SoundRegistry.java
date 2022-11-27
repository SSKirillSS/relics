package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MODID);

    public static final RegistryObject<SoundEvent> RICOCHET = SOUNDS.register("ricochet", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "ricochet")));
    public static final RegistryObject<SoundEvent> THROW = SOUNDS.register("throw", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "throw")));

    public static void registerSounds() {
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}