package it.hurts.sskirillss.relics.utils;

import net.minecraftforge.common.ForgeConfigSpec;

public class RelicsConfig {
    public static class Pedestal {
        public static ForgeConfigSpec.BooleanValue ACCEPT_ANY_ITEM;
        public static ForgeConfigSpec.BooleanValue SPAWN_PARTICLES;

        private static void setupPedestalConfig(ForgeConfigSpec.Builder builder) {
            builder.push("pedestal");
            ACCEPT_ANY_ITEM = builder.define("accept_any_item", true);
            SPAWN_PARTICLES = builder.define("spawn_particles", true);
            builder.pop();
        }
    }

    public static class RelicsCompatibility {
        public static ForgeConfigSpec.BooleanValue WARN_ABOUT_OLD_FORGE;

        private static void setupCompatibilityConfig(ForgeConfigSpec.Builder builder) {
            builder.push("compatibility");

            WARN_ABOUT_OLD_FORGE = builder.define("warn_about_old_forge", true);

            builder.pop();
        }
    }

    public static class RelicsGeneral {
        public static ForgeConfigSpec.BooleanValue ENABLE_RELIC_DURABILITY;
        public static ForgeConfigSpec.ConfigValue<String> LEVELING_BAR_STYLE;
        public static ForgeConfigSpec.ConfigValue<String> LEVELING_BAR_COLOR_NEUTRAL;
        public static ForgeConfigSpec.ConfigValue<String> LEVELING_BAR_COLOR_LOW;
        public static ForgeConfigSpec.ConfigValue<String> LEVELING_BAR_COLOR_MEDIUM;
        public static ForgeConfigSpec.ConfigValue<String> LEVELING_BAR_COLOR_HIGH;

        private static void setupGeneralConfig(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            ENABLE_RELIC_DURABILITY = builder.define("enable_relic_durability", false);
            LEVELING_BAR_STYLE = builder.define("leveling_bar_style", "||||||||||||||||||||||||||||||");
            LEVELING_BAR_COLOR_NEUTRAL = builder.define("leveling_bar_color_neutral", "#808080");
            LEVELING_BAR_COLOR_LOW = builder.define("leveling_bar_color_low", "#FF5555");
            LEVELING_BAR_COLOR_MEDIUM = builder.define("leveling_bar_color_medium", "#FFFF55");
            LEVELING_BAR_COLOR_HIGH = builder.define("leveling_bar_color_high", "#55FF55");

            builder.pop();
        }
    }

    public static void setupRelicsBlockConfig(ForgeConfigSpec.Builder builder) {
        builder.push("blocks");

        Pedestal.setupPedestalConfig(builder);

        builder.pop();
    }

    public static ForgeConfigSpec RELICS_CONFIG;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("general");
        setupRelicsBlockConfig(builder);

        builder.push("relics");

        RelicsGeneral.setupGeneralConfig(builder);
        RelicsCompatibility.setupCompatibilityConfig(builder);

        RELICS_CONFIG = builder.build();
    }
}