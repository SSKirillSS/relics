package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.effects.*;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Reference.MODID);

    public static final RegistryObject<MobEffect> STUN = EFFECTS.register("stun", StunEffect::new);
    public static final RegistryObject<MobEffect> CONFUSION = EFFECTS.register("confusion", ConfusionEffect::new);
    public static final RegistryObject<MobEffect> PARALYSIS = EFFECTS.register("paralysis", ParalysisEffect::new);
    public static final RegistryObject<MobEffect> VANISHING = EFFECTS.register("vanishing", VanishingEffect::new);
    public static final RegistryObject<MobEffect> ANTI_HEAL = EFFECTS.register("anti_heal", AntiHealEffect::new);
    public static final RegistryObject<MobEffect> BLEEDING = EFFECTS.register("bleeding", BleedingEffect::new);

    public static void register() {
        EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}