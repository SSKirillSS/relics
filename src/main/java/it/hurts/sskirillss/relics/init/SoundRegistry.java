package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundRegistry {
    public static SoundEvent RICOCHET = register("ricochet");
    public static SoundEvent THROW = register("throw");

    @SubscribeEvent
    public static void resisterSound(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(RICOCHET);
        event.getRegistry().register(THROW);
    }

    private static SoundEvent register(String name) {
        ResourceLocation rl = new ResourceLocation(Reference.MODID, name);

        return new SoundEvent(rl).setRegistryName(rl);
    }
}