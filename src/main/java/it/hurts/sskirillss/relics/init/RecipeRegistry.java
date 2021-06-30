package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.crafting.RelicOwnerRecipe;
import it.hurts.sskirillss.relics.crafting.RunicAltarRecipe;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistry {
    private static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MODID);

    public static final RegistryObject<SpecialRecipeSerializer<RelicOwnerRecipe>> RELIC_OWNER = RECIPES.register("relic_owner", () -> new SpecialRecipeSerializer<>(RelicOwnerRecipe::new));

    public static void registerRecipes() {
        RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void onRecipeRegistry(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().registerAll(new RunicAltarRecipe.Serializer().setRegistryName("runic_altar"));
    }
}