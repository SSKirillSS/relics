package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.items.RelicExperienceBottleItem;
import it.hurts.sskirillss.relics.items.SolidSnowballItem;
import it.hurts.sskirillss.relics.items.relics.*;
import it.hurts.sskirillss.relics.items.relics.back.ArrowQuiverItem;
import it.hurts.sskirillss.relics.items.relics.back.ElytraBoosterItem;
import it.hurts.sskirillss.relics.items.relics.back.MidnightRobeItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.belt.DrownedBeltItem;
import it.hurts.sskirillss.relics.items.relics.belt.HunterBeltItem;
import it.hurts.sskirillss.relics.items.relics.belt.LeatherBeltItem;
import it.hurts.sskirillss.relics.items.relics.feet.*;
import it.hurts.sskirillss.relics.items.relics.hands.EnderHandItem;
import it.hurts.sskirillss.relics.items.relics.hands.RageGloveItem;
import it.hurts.sskirillss.relics.items.relics.hands.WoolMittenItem;
import it.hurts.sskirillss.relics.items.relics.necklace.HolyLocketItem;
import it.hurts.sskirillss.relics.items.relics.necklace.JellyfishNecklaceItem;
import it.hurts.sskirillss.relics.items.relics.necklace.ReflectionNecklaceItem;
import it.hurts.sskirillss.relics.items.relics.ring.BastionRingItem;
import it.hurts.sskirillss.relics.items.relics.ring.ChorusInhibitorItem;
import it.hurts.sskirillss.relics.items.relics.talisman.SporeSackItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MODID);

    public static final RegistryObject<Item> SOLID_SNOWBALL = ITEMS.register("solid_snowball", SolidSnowballItem::new);
    public static final RegistryObject<Item> RELIC_EXPERIENCE_BOTTLE = ITEMS.register("relic_experience_bottle", RelicExperienceBottleItem::new);

    public static final RegistryObject<RelicItem> SPATIAL_SIGN = ITEMS.register("spatial_sign", SpatialSignItem::new);
    public static final RegistryObject<RelicItem> REFLECTION_NECKLACE = ITEMS.register("reflection_necklace", ReflectionNecklaceItem::new);
    public static final RegistryObject<RelicItem> MAGMA_WALKER = ITEMS.register("magma_walker", MagmaWalkerItem::new);
    public static final RegistryObject<RelicItem> AQUA_WALKER = ITEMS.register("aqua_walker", AquaWalkerItem::new);
    public static final RegistryObject<RelicItem> MIDNIGHT_ROBE = ITEMS.register("midnight_robe", MidnightRobeItem::new);
    public static final RegistryObject<RelicItem> DROWNED_BELT = ITEMS.register("drowned_belt", DrownedBeltItem::new);
    public static final RegistryObject<RelicItem> JELLYFISH_NECKLACE = ITEMS.register("jellyfish_necklace", JellyfishNecklaceItem::new);
    public static final RegistryObject<RelicItem> HUNTER_BELT = ITEMS.register("hunter_belt", HunterBeltItem::new);
    public static final RegistryObject<RelicItem> RAGE_GLOVE = ITEMS.register("rage_glove", RageGloveItem::new);
    public static final RegistryObject<RelicItem> ICE_SKATES = ITEMS.register("ice_skates", IceSkatesItem::new);
    public static final RegistryObject<RelicItem> BASTION_RING = ITEMS.register("bastion_ring", BastionRingItem::new);
    public static final RegistryObject<RelicItem> CHORUS_INHIBITOR = ITEMS.register("chorus_inhibitor", ChorusInhibitorItem::new);
    public static final RegistryObject<RelicItem> ARROW_QUIVER = ITEMS.register("arrow_quiver", ArrowQuiverItem::new);
    public static final RegistryObject<RelicItem> SPACE_DISSECTOR = ITEMS.register("space_dissector", SpaceDissectorItem::new);
    public static final RegistryObject<RelicItem> HOLY_LOCKET = ITEMS.register("holy_locket", HolyLocketItem::new);
    public static final RegistryObject<RelicItem> ENDER_HAND = ITEMS.register("enders_hand", EnderHandItem::new);
    public static final RegistryObject<RelicItem> ELYTRA_BOOSTER = ITEMS.register("elytra_booster", ElytraBoosterItem::new);
    public static final RegistryObject<RelicItem> MAGIC_MIRROR = ITEMS.register("magic_mirror", MagicMirrorItem::new);
    public static final RegistryObject<RelicItem> ICE_BREAKER = ITEMS.register("ice_breaker", IceBreakerItem::new);
    public static final RegistryObject<RelicItem> BLAZING_FLASK = ITEMS.register("blazing_flask", BlazingFlaskItem::new);
    public static final RegistryObject<RelicItem> SPORE_SACK = ITEMS.register("spore_sack", SporeSackItem::new);
    public static final RegistryObject<RelicItem> SHADOW_GLAIVE = ITEMS.register("shadow_glaive", ShadowGlaiveItem::new);
    public static final RegistryObject<RelicItem> ROLLER_SKATES = ITEMS.register("roller_skates", RollerSkatesItem::new);
    public static final RegistryObject<RelicItem> INFINITY_HAM = ITEMS.register("infinity_ham", InfinityHamItem::new);
    public static final RegistryObject<RelicItem> LEATHER_BELT = ITEMS.register("leather_belt", LeatherBeltItem::new);
    public static final RegistryObject<RelicItem> HORSE_FLUTE = ITEMS.register("horse_flute", HorseFluteItem::new);
    public static final RegistryObject<RelicItem> WOOL_MITTEN = ITEMS.register("wool_mitten", WoolMittenItem::new);
    public static final RegistryObject<RelicItem> AMPHIBIAN_BOOT = ITEMS.register("amphibian_boot", AmphibianBootItem::new);

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}