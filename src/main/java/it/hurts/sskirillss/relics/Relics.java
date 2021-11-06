package it.hurts.sskirillss.relics;

import it.hurts.sskirillss.relics.configs.JSONManager;
import it.hurts.sskirillss.relics.configs.RelicsConfig;
import it.hurts.sskirillss.relics.configs.ExtendedRelicsConfig;
import it.hurts.sskirillss.relics.init.*;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod(Reference.MODID)
public class Relics {
    public Relics() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueue);

        MinecraftForge.EVENT_BUS.register(this);

        ItemRegistry.registerItems();
        BlockRegistry.registerBlocks();
        TileRegistry.registerTiles();
        EntityRegistry.registerEntities();
        RecipeRegistry.registerRecipes();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RelicsConfig.getConfig());
    }

    private void enqueue(final InterModEnqueueEvent event) {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.BACK.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.HANDS.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.HEAD.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.RING.getMessageBuilder().size(2).build());

        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("talisman").priority(220)
                        .icon(RemoteRegistry.TALISMAN_ICON).size(0).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("feet").priority(240)
                        .icon(RemoteRegistry.FEET_ICON).build());
    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        NetworkHandler.registerMessages();
        JSONManager.setupRunesConfig();
        ItemRegistry.syncItemLists();
        ExtendedRelicsConfig.setupConfig();
    }
}