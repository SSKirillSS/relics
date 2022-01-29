package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.crafting.RelicOwnerRecipe;
import it.hurts.sskirillss.relics.crafting.RunicAltarRecipe;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistry {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MODID);

    public static final RegistryObject<SimpleRecipeSerializer<RelicOwnerRecipe>> RELIC_OWNER = RECIPES.register("relic_owner", () -> new SimpleRecipeSerializer<>(RelicOwnerRecipe::new));

    public static void registerRecipes() {
        RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void onRecipeRegistry(RegistryEvent.Register<RecipeSerializer<?>> event) {
        event.getRegistry().registerAll(new RunicAltarRecipe.Serializer().setRegistryName("runic_altar"));
    }
}