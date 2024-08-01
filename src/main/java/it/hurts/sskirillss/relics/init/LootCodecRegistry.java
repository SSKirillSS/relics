package it.hurts.sskirillss.relics.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.hurts.sskirillss.relics.level.RelicLootModifier;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class LootCodecRegistry {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> CODECS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Reference.MODID);

    public static final RegistryObject<Codec<RelicLootModifier>> RELIC_LOOT = CODECS.register("relic_loot", () -> RelicLootModifier.CODEC);

    public static void register() {
        CODECS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}