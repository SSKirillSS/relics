package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.entities.ShadowGlaiveEntity;
import it.hurts.sskirillss.relics.entities.SpaceDissectorEntity;
import it.hurts.sskirillss.relics.entities.StellarCatalystProjectileEntity;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MODID);

    public static final RegistryObject<EntityType<StellarCatalystProjectileEntity>> STELLAR_CATALYST_PROJECTILE = ENTITIES.register("stellar_catalyst_projectile", () ->
            EntityType.Builder.<StellarCatalystProjectileEntity>of(StellarCatalystProjectileEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build("stellar_catalyst_projectile")
    );

    public static final RegistryObject<EntityType<SpaceDissectorEntity>> SPACE_DISSECTOR = ENTITIES.register("space_dissector", () ->
            EntityType.Builder.<SpaceDissectorEntity>of(SpaceDissectorEntity::new, MobCategory.MISC)
                    .sized(0.9F, 0.1F)
                    .build("space_dissector")
    );

    public static final RegistryObject<EntityType<ShadowGlaiveEntity>> SHADOW_GLAIVE = ENTITIES.register("shadow_glaive", () ->
            EntityType.Builder.<ShadowGlaiveEntity>of(ShadowGlaiveEntity::new, MobCategory.MISC)
                    .sized(0.9F, 0.1F)
                    .build("shadow_glaive")
    );

    public static void registerEntities() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}