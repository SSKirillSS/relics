package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.crafting.RunicAltarRecipe;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistry {
    @SubscribeEvent
    public static void onRecipeRegistry(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().registerAll(new RunicAltarRecipe.Serializer().setRegistryName("runic_altar"));
    }
}