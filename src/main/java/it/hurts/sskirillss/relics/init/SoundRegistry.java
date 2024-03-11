package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MODID);

    public static final RegistryObject<SoundEvent> RICOCHET = SOUNDS.register("ricochet", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "ricochet")));
    public static final RegistryObject<SoundEvent> THROW = SOUNDS.register("throw", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "throw")));
    public static final RegistryObject<SoundEvent> ARROW_RAIN = SOUNDS.register("arrow_rain", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "arrow_rain")));
    public static final RegistryObject<SoundEvent> SPURT = SOUNDS.register("spurt", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "spurt")));
    public static final RegistryObject<SoundEvent> POWERED_ARROW = SOUNDS.register("powered_arrow", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "powered_arrow")));
    public static final RegistryObject<SoundEvent> LEAP = SOUNDS.register("leap", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "leap")));

    public static final RegistryObject<SoundEvent> TABLE_UPGRADE = SOUNDS.register("table_upgrade", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "table_upgrade")));
    public static final RegistryObject<SoundEvent> TABLE_REROLL = SOUNDS.register("table_reroll", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "table_reroll")));
    public static final RegistryObject<SoundEvent> TABLE_RESET = SOUNDS.register("table_reset", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "table_reset")));

    public static final RegistryObject<SoundEvent> ABILITY_LOCKED = SOUNDS.register("ability_locked", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "ability_locked")));
    public static final RegistryObject<SoundEvent> ABILITY_COOLDOWN = SOUNDS.register("ability_cooldown", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "ability_cooldown")));
    public static final RegistryObject<SoundEvent> ABILITY_CAST = SOUNDS.register("ability_cast", () -> new SoundEvent(new ResourceLocation(Reference.MODID, "ability_cast")));

    public static void register() {
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}