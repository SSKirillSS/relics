package it.hurts.sskirillss.relics.configs;

import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;

public class RelicsConfig {
    @Getter
    private static final ForgeConfigSpec config;

    public static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_EXTENDED_CONFIG;
    public static ForgeConfigSpec.BooleanValue ENABLE_RELICS_DURABILITY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("general");

        ENABLE_EXTENDED_CONFIG = builder
                .comment("Enable advanced configurations with tons of options to customize.",
                        "Note that mod configurations change with each new release, so you should keep track of changes and update extended config if you want to use new options.",
                        "Requires game restart to apply. Use only if you know what are you doing!")
                .define("enable_extended_config", false);
        ENABLE_RELICS_DURABILITY = builder
                .define("enable_relics_durability", true);

        config = builder.build();
    }
}