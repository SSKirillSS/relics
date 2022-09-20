package it.hurts.sskirillss.relics.configs;

import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;

public class RelicsConfig {
    @Getter
    private static final ForgeConfigSpec config;

    public static ForgeConfigSpec.BooleanValue ENABLE_RELICS_DURABILITY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("general");

        ENABLE_RELICS_DURABILITY = builder
                .define("enable_relics_durability", true);

        builder.pop();

        config = builder.build();
    }
}