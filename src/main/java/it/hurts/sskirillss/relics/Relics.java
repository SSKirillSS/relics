package it.hurts.sskirillss.relics;

import it.hurts.sskirillss.relics.init.*;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MODID)
public class Relics {
    public Relics() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);

        MinecraftForge.EVENT_BUS.register(this);

        ItemRegistry.register();
        BlockRegistry.register();
        TileRegistry.register();
        EntityRegistry.register();
        EffectRegistry.register();
        CodecRegistry.register();
        SoundRegistry.register();
        CommandRegistry.register();
        ParticleRegistry.register();
        CreativeTabRegistry.register();
    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }
}