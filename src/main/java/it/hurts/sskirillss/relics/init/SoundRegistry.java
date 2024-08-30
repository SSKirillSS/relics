package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Reference.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> RICOCHET = SOUNDS.register("ricochet", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "ricochet")));
    public static final DeferredHolder<SoundEvent, SoundEvent> THROW = SOUNDS.register("throw", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "throw")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ARROW_RAIN = SOUNDS.register("arrow_rain", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "arrow_rain")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SPURT = SOUNDS.register("spurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "spurt")));
    public static final DeferredHolder<SoundEvent, SoundEvent> POWERED_ARROW = SOUNDS.register("powered_arrow", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "powered_arrow")));
    public static final DeferredHolder<SoundEvent, SoundEvent> LEAP = SOUNDS.register("leap", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "leap")));

    public static final DeferredHolder<SoundEvent, SoundEvent> TABLE_UPGRADE = SOUNDS.register("table_upgrade", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "table_upgrade")));
    public static final DeferredHolder<SoundEvent, SoundEvent> TABLE_REROLL = SOUNDS.register("table_reroll", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "table_reroll")));
    public static final DeferredHolder<SoundEvent, SoundEvent> TABLE_RESET = SOUNDS.register("table_reset", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "table_reset")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ABILITY_LOCKED = SOUNDS.register("ability_locked", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "ability_locked")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ABILITY_COOLDOWN = SOUNDS.register("ability_cooldown", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "ability_cooldown")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ABILITY_CAST = SOUNDS.register("ability_cast", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "ability_cast")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CONNECT_STARS = SOUNDS.register("connect_stars", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "connect_stars")));
    public static final DeferredHolder<SoundEvent, SoundEvent> DISCONNECT_STARS = SOUNDS.register("disconnect_stars", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "disconnect_stars")));
    public static final DeferredHolder<SoundEvent, SoundEvent> FINISH_RESEARCH = SOUNDS.register("finish_research", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "finish_research")));

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}