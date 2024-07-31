package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.items.relics.base.ICreativeTabEntry;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MODID);

    public static final RegistryObject<CreativeModeTab> RELICS_TAB = CREATIVE_TABS.register("relics", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.relics"))
            .icon(() -> ItemRegistry.BASTION_RING.get().getDefaultInstance())
            .build());

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        CREATIVE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());

        bus.addListener(CreativeTabRegistry::fillCreativeTabs);
    }

    private static void fillCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == RELICS_TAB.get()) {
            for (Item item : ForgeRegistries.ITEMS.getValues()) {
                if (item instanceof ICreativeTabEntry entry)
                    event.acceptAll(entry.processCreativeTab());
            }
        }
    }
}