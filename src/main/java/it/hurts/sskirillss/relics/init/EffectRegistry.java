package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.effects.*;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Reference.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> STUN = EFFECTS.register("stun", StunEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> CONFUSION = EFFECTS.register("confusion", ConfusionEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> PARALYSIS = EFFECTS.register("paralysis", ParalysisEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> VANISHING = EFFECTS.register("vanishing", VanishingEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> ANTI_HEAL = EFFECTS.register("anti_heal", AntiHealEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> BLEEDING = EFFECTS.register("bleeding", BleedingEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> FROSTBITE = EFFECTS.register("frostbite", FrostbiteEffect::new);

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}