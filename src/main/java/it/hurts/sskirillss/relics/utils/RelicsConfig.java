package it.hurts.sskirillss.relics.utils;

import net.minecraftforge.common.ForgeConfigSpec;

public class RelicsConfig {
    public static class ArrowQuiver {
        public static ForgeConfigSpec.DoubleValue MULTISHOT_CHANCE;
        public static ForgeConfigSpec.IntValue ADDITIONAL_ARROW_AMOUNT;

        private static void setupArrowQuiverConfig(ForgeConfigSpec.Builder builder) {
            builder.push("arrow_quiver");
            MULTISHOT_CHANCE = builder.defineInRange("multishot_chance", 0.2, 0, 1);
            ADDITIONAL_ARROW_AMOUNT = builder.defineInRange("additional_arrow_count", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class BastionRing {
        public static ForgeConfigSpec.DoubleValue LOCATE_CHANCE;

        private static void setupBastionRingConfig(ForgeConfigSpec.Builder builder) {
            builder.push("bastion_ring");
            LOCATE_CHANCE = builder.defineInRange("locate_chance", 0.3, 0, 1);
            builder.pop();
        }
    }

    public static class CamouflageRing {
        public static ForgeConfigSpec.IntValue MAX_INVISIBILITY_TIME;
        public static ForgeConfigSpec.DoubleValue STEALTH_DAMAGE_MULTIPLIER;

        private static void setupCamouflageRingConfig(ForgeConfigSpec.Builder builder) {
            builder.push("camouflage_ring");
            MAX_INVISIBILITY_TIME = builder.defineInRange("max_invisibility_time", 60, Integer.MIN_VALUE, Integer.MAX_VALUE);
            STEALTH_DAMAGE_MULTIPLIER = builder.defineInRange("stealth_damage_multiplier", 2.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class ChorusInhibitor {
        public static ForgeConfigSpec.IntValue TIME_PER_CHORUS;

        private static void setupChorusInhibitorConfig(ForgeConfigSpec.Builder builder) {
            builder.push("chorus_inhibitor");
            TIME_PER_CHORUS = builder.defineInRange("time_per_chorus", 30, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class DelayRing {
        public static ForgeConfigSpec.IntValue USAGE_COOLDOWN;
        public static ForgeConfigSpec.IntValue DELAY_DURATION;
        public static ForgeConfigSpec.DoubleValue DAMAGE_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue HEALING_MULTIPLIER;

        private static void setupDelayRingConfig(ForgeConfigSpec.Builder builder) {
            builder.push("delay_ring");
            USAGE_COOLDOWN = builder.defineInRange("usage_cooldown", 60, Integer.MIN_VALUE, Integer.MAX_VALUE);
            DELAY_DURATION = builder.defineInRange("delay_duration", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
            DAMAGE_MULTIPLIER = builder.defineInRange("damage_multiplier", 1.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            HEALING_MULTIPLIER = builder.defineInRange("healing_multiplier", 2.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class DrownedBelt {
        public static ForgeConfigSpec.DoubleValue UNDERWATER_SPEED_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue INCOMING_DAMAGE_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue DEALT_DAMAGE_MULTIPLIER;

        private static void setupDrownedBeltConfig(ForgeConfigSpec.Builder builder) {
            builder.push("drowned_belt");
            UNDERWATER_SPEED_MULTIPLIER = builder.defineInRange("underwater_speed_multiplier", -0.25, Integer.MIN_VALUE, Integer.MAX_VALUE);
            INCOMING_DAMAGE_MULTIPLIER = builder.defineInRange("incoming_damage_multiplier", 1.5, Integer.MIN_VALUE, Integer.MAX_VALUE);
            DEALT_DAMAGE_MULTIPLIER = builder.defineInRange("dealt_damage_multiplier", 3.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class HunterBelt {
        public static ForgeConfigSpec.DoubleValue DAMAGE_MULTIPLIER;
        public static ForgeConfigSpec.IntValue ADDITIONAL_LOOTING;

        private static void setupHunterBeltConfig(ForgeConfigSpec.Builder builder) {
            builder.push("hunter_belt");
            DAMAGE_MULTIPLIER = builder.defineInRange("damage_multiplier", 2.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            ADDITIONAL_LOOTING = builder.defineInRange("additional_looting", 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class IceSkates {
        public static ForgeConfigSpec.DoubleValue MOVEMENT_SPEED_MULTIPLIER;
        public static ForgeConfigSpec.IntValue MAX_SPEEDUP_TIME;
        public static ForgeConfigSpec.IntValue SPEEDUP_TIME_PER_RAM;
        public static ForgeConfigSpec.DoubleValue RAM_RADIUS;
        public static ForgeConfigSpec.DoubleValue BASE_RAM_DAMAGE_AMOUNT;
        public static ForgeConfigSpec.DoubleValue FALLING_DAMAGE_MULTIPLIER;

        private static void setupIceSkatesConfig(ForgeConfigSpec.Builder builder) {
            builder.push("ice_skates");
            MOVEMENT_SPEED_MULTIPLIER = builder.defineInRange("movement_speed_multiplier", 1.1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            MAX_SPEEDUP_TIME = builder.defineInRange("max_speedup_time", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
            SPEEDUP_TIME_PER_RAM = builder.defineInRange("speedup_time_per_ram", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
            RAM_RADIUS = builder.defineInRange("ram_radius", 1.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            BASE_RAM_DAMAGE_AMOUNT = builder.defineInRange("base_ram_damage_amount", 2.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            FALLING_DAMAGE_MULTIPLIER = builder.defineInRange("falling_damage_multiplier", 0.250, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class JellyfishNecklace {
        public static ForgeConfigSpec.IntValue TIME_PER_CHARGE;
        public static ForgeConfigSpec.IntValue MAX_CHARGES_AMOUNT;
        public static ForgeConfigSpec.DoubleValue DAMAGE_PER_CHARGE;
        public static ForgeConfigSpec.DoubleValue HEALING_MULTIPLIER;

        private static void setupJellyfishNecklaceConfig(ForgeConfigSpec.Builder builder) {
            builder.push("jellyfish_necklace");
            TIME_PER_CHARGE = builder.defineInRange("time_per_charge", 60, Integer.MIN_VALUE, Integer.MAX_VALUE);
            MAX_CHARGES_AMOUNT = builder.defineInRange("max_charges_amount", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
            DAMAGE_PER_CHARGE = builder.defineInRange("damage_per_charge", 10.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            HEALING_MULTIPLIER = builder.defineInRange("healing_multiplier", 2.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class LuckyHorseshoe {
        public static ForgeConfigSpec.DoubleValue LOOTING_CHANCE;
        public static ForgeConfigSpec.DoubleValue FORTUNA_CHANCE;
        public static ForgeConfigSpec.IntValue ADDITIONAL_LOOTING;
        public static ForgeConfigSpec.IntValue ADDITIONAL_FORTUNE;

        private static void setupLuckyHorseshoeConfig(ForgeConfigSpec.Builder builder) {
            builder.push("lucky_horseshoe");
            LOOTING_CHANCE = builder.defineInRange("looting_chance", 0.1, 0, 1);
            FORTUNA_CHANCE = builder.defineInRange("fortuna_chance", 0.1, 0, 1);
            ADDITIONAL_LOOTING = builder.defineInRange("additional_looting", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
            ADDITIONAL_FORTUNE = builder.defineInRange("additional_fortune", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class MidnightRobe {
        public static ForgeConfigSpec.DoubleValue MOVEMENT_SPEED_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue HEALTH_PERCENTAGE;
        public static ForgeConfigSpec.DoubleValue STEALTH_DAMAGE_MULTIPLIER;
        public static ForgeConfigSpec.IntValue ATTACK_INVISIBILITY_PENALTY;

        private static void setupMidnightRobeConfig(ForgeConfigSpec.Builder builder) {
            builder.push("midnight_robe");
            MOVEMENT_SPEED_MULTIPLIER = builder.defineInRange("movement_speed_multiplier", 1.25, Integer.MIN_VALUE, Integer.MAX_VALUE);
            HEALTH_PERCENTAGE = builder.defineInRange("health_percentage", 0.2, Integer.MIN_VALUE, Integer.MAX_VALUE);
            STEALTH_DAMAGE_MULTIPLIER = builder.defineInRange("stealth_damage_multiplier", 2.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            ATTACK_INVISIBILITY_PENALTY = builder.defineInRange("attack_invisibility_penalty", 5, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class RageGlove {
        public static ForgeConfigSpec.IntValue STACK_TIME;
        public static ForgeConfigSpec.DoubleValue MIN_DAMAGE_AMOUNT;
        public static ForgeConfigSpec.DoubleValue DEALING_DAMAGE_MULTIPLIER_PER_STACK;
        public static ForgeConfigSpec.DoubleValue INCOMING_DAMAGE_MULTIPLIER_PER_STACK;

        private static void setupRageGloveConfig(ForgeConfigSpec.Builder builder) {
            builder.push("rage_glove");
            STACK_TIME = builder.defineInRange("stack_time", 5, Integer.MIN_VALUE, Integer.MAX_VALUE);
            MIN_DAMAGE_AMOUNT = builder.defineInRange("min_damage_amount", 3.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            DEALING_DAMAGE_MULTIPLIER_PER_STACK = builder.defineInRange("dealing_damage_multiplier_per_stack", 0.05, Integer.MIN_VALUE, Integer.MAX_VALUE);
            INCOMING_DAMAGE_MULTIPLIER_PER_STACK = builder.defineInRange("incoming_damage_multiplier_per_stack", 0.025, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class ReflectionNecklace {
        public static ForgeConfigSpec.IntValue MAX_CHARGES;
        public static ForgeConfigSpec.IntValue MIN_TIME_PER_CHARGE;
        public static ForgeConfigSpec.IntValue MAX_THROW_DISTANCE;
        public static ForgeConfigSpec.DoubleValue REFLECTION_DAMAGE_MULTIPLIER;

        private static void setupReflectionNecklaceConfig(ForgeConfigSpec.Builder builder) {
            builder.push("reflection_necklace");
            MAX_CHARGES = builder.defineInRange("max_charges", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
            MIN_TIME_PER_CHARGE = builder.defineInRange("min_time_per_charge", 60, Integer.MIN_VALUE, Integer.MAX_VALUE);
            MAX_THROW_DISTANCE = builder.defineInRange("max_throw_distance", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
            REFLECTION_DAMAGE_MULTIPLIER = builder.defineInRange("reflection_damage_multiplier", 2.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class ScarabTalisman {
        public static ForgeConfigSpec.DoubleValue SPEED_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue STEP_HEIGHT;

        private static void setupScarabTalismanConfig(ForgeConfigSpec.Builder builder) {
            builder.push("scarab_talisman");
            SPEED_MULTIPLIER = builder.defineInRange("speed_multiplier", 1.5, Integer.MIN_VALUE, Integer.MAX_VALUE);
            STEP_HEIGHT = builder.defineInRange("step_height", 1.1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class SpatialSign {
        public static ForgeConfigSpec.IntValue TIME_BEFORE_ACTIVATION;

        private static void setupSpatialSignConfig(ForgeConfigSpec.Builder builder) {
            builder.push("spatial_sign");
            TIME_BEFORE_ACTIVATION = builder.defineInRange("time_before_activation", 30, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class StellarCatalyst {
        public static ForgeConfigSpec.DoubleValue FALLING_STAR_SUMMON_CHANCE;
        public static ForgeConfigSpec.DoubleValue FALLING_STAR_DAMAGE_MULTIPLIER;
        public static ForgeConfigSpec.IntValue FALLING_STAR_DAMAGE_RADIUS;
        public static ForgeConfigSpec.DoubleValue MIN_DAMAGE_AMOUNT;

        private static void setupStellarCatalystConfig(ForgeConfigSpec.Builder builder) {
            builder.push("stellar_catalyst");
            FALLING_STAR_SUMMON_CHANCE = builder.defineInRange("falling_star_summon_chance", 0.3, 0, 1);
            FALLING_STAR_DAMAGE_MULTIPLIER = builder.defineInRange("falling_star_damage_multiplier", 0.5, Integer.MIN_VALUE, Integer.MAX_VALUE);
            FALLING_STAR_DAMAGE_RADIUS = builder.defineInRange("falling_star_damage_radius", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
            MIN_DAMAGE_AMOUNT = builder.defineInRange("min_damage_amount", 3.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class RelicsWorldgen {
        public static ForgeConfigSpec.DoubleValue ARROW_QUIVER_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue BASTION_RING_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue CAMOUFLAGE_RING_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue CHORUS_INHIBITOR_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue DELAY_RING_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue DROWNED_BELT_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue HUNTER_BELT_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue ICE_SKATES_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue JELLYFISH_NECKLACE_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue LUCKY_HORSESHOE_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue MAGMA_WALKER_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue MIDNIGHT_ROBE_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue RAGE_GLOVE_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue REFLECTION_NECKLACE_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue SCARAB_TALISMAN_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue SPATIAL_SIGN_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue STELLAR_CATALYST_GEN_CHANCE;

        private static void setupWorldgenConfig(ForgeConfigSpec.Builder builder) {
            ARROW_QUIVER_GEN_CHANCE = builder.defineInRange("arrow_quiver_gen_chance", 0.2, 0, 1);
            BASTION_RING_GEN_CHANCE = builder.defineInRange("bastion_ring_gen_chance", 0.15, 0, 1);
            CAMOUFLAGE_RING_GEN_CHANCE = builder.defineInRange("camouflage_ring_gen_chance", 0.2, 0, 1);
            CHORUS_INHIBITOR_GEN_CHANCE = builder.defineInRange("chorus_inhibitor_gen_chance", 0.15, 0, 1);
            DELAY_RING_GEN_CHANCE = builder.defineInRange("delay_ring_gen_chance", 0.05, 0, 1);
            DROWNED_BELT_GEN_CHANCE = builder.defineInRange("drowned_belt_gen_chance", 0.2, 0, 1);
            HUNTER_BELT_GEN_CHANCE = builder.defineInRange("hunter_belt_gen_chance", 0.2, 0, 1);
            ICE_SKATES_GEN_CHANCE = builder.defineInRange("ice_skates_gen_chance", 0.2, 0, 1);
            JELLYFISH_NECKLACE_GEN_CHANCE = builder.defineInRange("jellyfish_necklace_gen_chance", 0.2, 0, 1);
            LUCKY_HORSESHOE_GEN_CHANCE = builder.defineInRange("lucky_horseshoe_gen_chance", 0.15, 0, 1);
            MAGMA_WALKER_GEN_CHANCE = builder.defineInRange("magma_walker_gen_chance", 0.12, 0, 1);
            MIDNIGHT_ROBE_GEN_CHANCE = builder.defineInRange("midnight_robe_gen_chance", 0.15, 0, 1);
            RAGE_GLOVE_GEN_CHANCE = builder.defineInRange("rage_glove_gen_chance", 0.05, 0, 1);
            REFLECTION_NECKLACE_GEN_CHANCE = builder.defineInRange("reflection_necklace_gen_chance", 0.05, 0, 1);
            SCARAB_TALISMAN_GEN_CHANCE = builder.defineInRange("scarab_talisman_gen_chance", 0.14, 0, 1);
            SPATIAL_SIGN_GEN_CHANCE = builder.defineInRange("spatial_sign_gen_chance", 0.25, 0, 1);
            STELLAR_CATALYST_GEN_CHANCE = builder.defineInRange("stellar_catalyst_gen_chance", 0.05, 0, 1);
            builder.pop();
        }
    }

    private static void setupRelicsStatsConfig(ForgeConfigSpec.Builder builder) {
        builder.push("stats");

        ArrowQuiver.setupArrowQuiverConfig(builder);
        BastionRing.setupBastionRingConfig(builder);
        CamouflageRing.setupCamouflageRingConfig(builder);
        ChorusInhibitor.setupChorusInhibitorConfig(builder);
        DelayRing.setupDelayRingConfig(builder);
        DrownedBelt.setupDrownedBeltConfig(builder);
        HunterBelt.setupHunterBeltConfig(builder);
        IceSkates.setupIceSkatesConfig(builder);
        JellyfishNecklace.setupJellyfishNecklaceConfig(builder);
        LuckyHorseshoe.setupLuckyHorseshoeConfig(builder);
        MidnightRobe.setupMidnightRobeConfig(builder);
        RageGlove.setupRageGloveConfig(builder);
        ReflectionNecklace.setupReflectionNecklaceConfig(builder);
        ScarabTalisman.setupScarabTalismanConfig(builder);
        SpatialSign.setupSpatialSignConfig(builder);
        StellarCatalyst.setupStellarCatalystConfig(builder);

        builder.pop();
    }


    public static ForgeConfigSpec RELICS_CONFIG;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("general");

        builder.push("relics");
        setupRelicsStatsConfig(builder);

        builder.push("worldgen");
        RelicsWorldgen.setupWorldgenConfig(builder);
        builder.pop();

        RELICS_CONFIG = builder.build();
    }
}