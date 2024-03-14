package it.hurts.sskirillss.relics;

import it.hurts.sskirillss.relics.config.ConfigHelper;
import it.hurts.sskirillss.relics.init.*;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MODID)
public class Relics {
    public static final Logger LOGGER = LogManager.getLogger(Reference.MODID);

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
        DispenserBehaviorRegistry.register();

        ConfigHelper.setupConfigs();

        InterModComms.sendTo("carryon", "blacklistBlock", () -> "relics:researching_table");
    }
}