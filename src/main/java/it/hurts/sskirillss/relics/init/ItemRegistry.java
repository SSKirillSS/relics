package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.items.*;
import it.hurts.sskirillss.relics.items.relics.*;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MODID);

    public static final RegistryObject<Item> AQUASTEEL_INGOT = ITEMS.register("aquasteel_ingot", ItemBase::new);
    public static final RegistryObject<Item> FIRESTEEL_INGOT = ITEMS.register("firesteel_ingot", ItemBase::new);
    public static final RegistryObject<Item> TERRASTEEL_INGOT = ITEMS.register("terrasteel_ingot", ItemBase::new);
    public static final RegistryObject<Item> AIRSTEEL_INGOT = ITEMS.register("airsteel_ingot", ItemBase::new);
    public static final RegistryObject<Item> MITHRIL_INGOT = ITEMS.register("mithril_ingot", ItemBase::new);
    public static final RegistryObject<Item> OBSIDIAN_INGOT = ITEMS.register("obsidian_ingot", ItemBase::new);
    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register("steel_ingot", ItemBase::new);
    public static final RegistryObject<Item> FLAMING_AMBER_GEODE = ITEMS.register("flaming_amber_geode", ItemBase::new);
    public static final RegistryObject<Item> FLAMING_AMBER_DUST = ITEMS.register("flaming_amber_dust", ItemBase::new);
    public static final RegistryObject<Item> CHRYSOLITE = ITEMS.register("chrysolite", ItemBase::new);

    public static final RegistryObject<Item> SPATIAL_SIGN = ITEMS.register("spatial_sign", SpatialSignItem::new);
    public static final RegistryObject<Item> CHALK = ITEMS.register("chalk", ChalkItem::new);
    public static final RegistryObject<Item> REFLECTION_NECKLACE = ITEMS.register("reflection_necklace", ReflectionNecklaceItem::new);
    public static final RegistryObject<Item> MAGMA_WALKER = ITEMS.register("magma_walker", MagmaWalkerItem::new);
    public static final RegistryObject<Item> MIDNIGHT_ROBE = ITEMS.register("midnight_robe", MidnightRobeItem::new);
    public static final RegistryObject<Item> SCARAB_TALISMAN = ITEMS.register("scarab_talisman", ScarabTalismanItem::new);
    public static final RegistryObject<Item> STELLAR_CATALYST = ITEMS.register("stellar_catalyst", StellarCatalystItem::new);
    public static final RegistryObject<Item> DROWNED_BELT = ITEMS.register("drowned_belt", DrownedBeltItem::new);
    public static final RegistryObject<Item> JELLYFISH_NECKLACE = ITEMS.register("jellyfish_necklace", JellyfishNecklaceItem::new);
    public static final RegistryObject<Item> HUNTER_BELT = ITEMS.register("hunter_belt", HunterBeltItem::new);
    public static final RegistryObject<Item> RAGE_GLOVE = ITEMS.register("rage_glove", RageGloveItem::new);
    public static final RegistryObject<Item> ICE_SKATES = ITEMS.register("ice_skates", IceSkatesItem::new);
    public static final RegistryObject<Item> CAMOUFLAGE_RING = ITEMS.register("camouflage_ring", CamouflageRingItem::new);
    public static final RegistryObject<Item> DELAY_RING = ITEMS.register("delay_ring", DelayRingItem::new);
    public static final RegistryObject<Item> BASTION_RING = ITEMS.register("bastion_ring", BastionRingItem::new);
    public static final RegistryObject<Item> CHORUS_INHIBITOR = ITEMS.register("chorus_inhibitor", ChorusInhibitorItem::new);
    public static final RegistryObject<Item> ARROW_QUIVER = ITEMS.register("arrow_quiver", ArrowQuiverItem::new);
    public static final RegistryObject<Item> LUCKY_HORSESHOE = ITEMS.register("lucky_horseshoe", LuckyHorseshoeItem::new);
    public static final RegistryObject<Item> SPACE_DISSECTOR = ITEMS.register("space_dissector", SpaceDissectorItem::new);
    public static final RegistryObject<Item> SOUL_DEVOURER = ITEMS.register("soul_devourer", SoulDevourerItem::new);
    public static final RegistryObject<Item> HOLY_LOCKET = ITEMS.register("holy_locket", HolyLocketItem::new);
    public static final RegistryObject<Item> ENDERS_HAND = ITEMS.register("enders_hand", EndersHandItem::new);
    public static final RegistryObject<Item> ELYTRA_BOOSTER = ITEMS.register("elytra_booster", ElytraBoosterItem::new);
    public static final RegistryObject<Item> FRAGRANT_FLOWER = ITEMS.register("fragrant_flower", FragrantFlowerItem::new);
    public static final RegistryObject<Item> MAGIC_MIRROR = ITEMS.register("magic_mirror", MagicMirrorItem::new);
    public static final RegistryObject<Item> ICE_BREAKER = ITEMS.register("ice_breaker", IceBreakerItem::new);

    public static void registerItems() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}