package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.entities.ThrownRelicExperienceBottle;
import it.hurts.sskirillss.relics.entities.*;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MODID);

    public static final RegistryObject<EntityType<ShadowGlaiveEntity>> SHADOW_GLAIVE = ENTITIES.register("shadow_glaive", () ->
            EntityType.Builder.<ShadowGlaiveEntity>of(ShadowGlaiveEntity::new, MobCategory.MISC)
                    .sized(0.9F, 0.1F)
                    .build("shadow_glaive")
    );

    public static final RegistryObject<EntityType<BlockSimulationEntity>> BLOCK_SIMULATION = ENTITIES.register("block_simulation", () ->
            EntityType.Builder.<BlockSimulationEntity>of(BlockSimulationEntity::new, MobCategory.MISC)
                    .sized(1F, 1F)
                    .build("block_simulation")
    );

    public static final RegistryObject<EntityType<ShockwaveEntity>> SHOCKWAVE = ENTITIES.register("shockwave", () ->
            EntityType.Builder.<ShockwaveEntity>of(ShockwaveEntity::new, MobCategory.MISC)
                    .sized(1F, 0.1F)
                    .build("shockwave")
    );

    public static final RegistryObject<EntityType<LifeEssenceEntity>> LIFE_ESSENCE = ENTITIES.register("life_essence", () ->
            EntityType.Builder.<LifeEssenceEntity>of(LifeEssenceEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("life_essence")
    );

    public static final RegistryObject<EntityType<StalactiteEntity>> STALACTITE = ENTITIES.register("stalactite", () ->
            EntityType.Builder.<StalactiteEntity>of(StalactiteEntity::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .build("stalactite")
    );

    public static final RegistryObject<EntityType<DissectionEntity>> DISSECTION = ENTITIES.register("dissection", () ->
            EntityType.Builder.<DissectionEntity>of(DissectionEntity::new, MobCategory.MISC)
                    .sized(3F, 3F)
                    .build("dissection")
    );

    public static final RegistryObject<EntityType<SporeEntity>> SPORE = ENTITIES.register("spore", () ->
            EntityType.Builder.<SporeEntity>of(SporeEntity::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F)
                    .build("spore")
    );

    public static final RegistryObject<EntityType<ShadowSawEntity>> SHADOW_SAW = ENTITIES.register("shadow_saw", () ->
            EntityType.Builder.<ShadowSawEntity>of(ShadowSawEntity::new, MobCategory.MISC)
                    .sized(2F, 0.5F)
                    .build("shadow_saw")
    );

    public static final RegistryObject<EntityType<SolidSnowballEntity>> SOLID_SNOWBALL = ENTITIES.register("solid_snowball", () ->
            EntityType.Builder.<SolidSnowballEntity>of(SolidSnowballEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("solid_snowball")
    );

    public static final RegistryObject<EntityType<ArrowRainEntity>> ARROW_RAIN = ENTITIES.register("arrow_rain", () ->
            EntityType.Builder.<ArrowRainEntity>of(ArrowRainEntity::new, MobCategory.MISC)
                    .sized(1F, 1F)
                    .build("arrow_rain")
    );

    public static final RegistryObject<EntityType<RelicExperienceOrbEntity>> RELIC_EXPERIENCE_ORB = ENTITIES.register("relic_experience_orb", () ->
            EntityType.Builder.of(RelicExperienceOrbEntity::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .build("relic_experience_orb")
    );

    public static final RegistryObject<EntityType<ThrownRelicExperienceBottle>> THROWN_RELIC_EXPERIENCE_BOTTLE = ENTITIES.register("thrown_relic_experience_bottle", () ->
            EntityType.Builder.of(ThrownRelicExperienceBottle::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .build("thrown_relic_experience_bottle")
    );

    public static void register() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}