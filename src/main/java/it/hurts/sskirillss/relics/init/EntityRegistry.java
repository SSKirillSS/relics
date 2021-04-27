package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.entities.*;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MODID);

    public static final RegistryObject<EntityType<StellarCatalystProjectileEntity>> STELLAR_CATALYST_PROJECTILE = ENTITIES.register("stellar_catalyst_projectile", () ->
            EntityType.Builder.<StellarCatalystProjectileEntity>create(StellarCatalystProjectileEntity::new, EntityClassification.MISC)
                    .size(1.0F, 1.0F)
                    .build("stellar_catalyst_projectile")
    );

    public static final RegistryObject<EntityType<SpaceDissectorEntity>> SPACE_DISSECTOR = ENTITIES.register("space_dissector", () ->
            EntityType.Builder.<SpaceDissectorEntity>create(SpaceDissectorEntity::new, EntityClassification.MISC)
                    .size(0.9F, 0.1F)
                    .build("space_dissector")
    );

    public static final RegistryObject<EntityType<ShadowGlaiveEntity>> SHADOW_GLAIVE = ENTITIES.register("shadow_glaive", () ->
            EntityType.Builder.<ShadowGlaiveEntity>create(ShadowGlaiveEntity::new, EntityClassification.MISC)
                    .size(0.9F, 0.1F)
                    .build("shadow_glaive")
    );

    public static void registerEntities() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}