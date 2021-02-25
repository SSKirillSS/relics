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

    public static class ElytraBooster {
        public static ForgeConfigSpec.DoubleValue MOVEMENT_SPEED_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue RAM_DAMAGE_AMOUNT;
        public static ForgeConfigSpec.DoubleValue RAM_KNOCKBACK_POWER;
        public static ForgeConfigSpec.DoubleValue BREATH_CONSUMPTION_RADIUS;
        public static ForgeConfigSpec.IntValue BREATH_CAPACITY;
        public static ForgeConfigSpec.DoubleValue BREATH_CONSUMPTION_AMOUNT;

        private static void setupElytraBoosterConfig(ForgeConfigSpec.Builder builder) {
            builder.push("elytra_booster");
            MOVEMENT_SPEED_MULTIPLIER = builder.defineInRange("movement_speed_multiplier", 1.5, 0, Integer.MAX_VALUE);
            RAM_DAMAGE_AMOUNT = builder.defineInRange("ram_damage_amount", 5.0, 0, Integer.MAX_VALUE);
            RAM_KNOCKBACK_POWER = builder.defineInRange("ram_knockback_power", 3.0, 0, Integer.MAX_VALUE);
            BREATH_CONSUMPTION_RADIUS = builder.defineInRange("breath_consumption_radius", 10.0, 0, Integer.MAX_VALUE);
            BREATH_CAPACITY = builder.defineInRange("breath_capacity", 1000, 0, Integer.MAX_VALUE);
            BREATH_CONSUMPTION_AMOUNT = builder.defineInRange("breath_consumption_amount", 0.02, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class EndersHand {
        public static ForgeConfigSpec.IntValue TIME_BEFORE_TELEPORTING;
        public static ForgeConfigSpec.IntValue MAX_TELEPORT_DISTANCE;
        public static ForgeConfigSpec.IntValue TELEPORT_COOLDOWN;

        private static void setupEndersHandConfig(ForgeConfigSpec.Builder builder) {
            builder.push("enders_hand");
            TIME_BEFORE_TELEPORTING = builder.defineInRange("time_before_teleporting", 1, 0, Integer.MAX_VALUE);
            MAX_TELEPORT_DISTANCE = builder.defineInRange("max_teleport_instance", 64, 0, Integer.MAX_VALUE);
            TELEPORT_COOLDOWN = builder.defineInRange("teleport_cooldown", 10, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class FragrantFlower {
        public static ForgeConfigSpec.IntValue BEE_LURING_RADIUS;
        public static ForgeConfigSpec.IntValue BEE_AGGRO_RADIUS;
        public static ForgeConfigSpec.DoubleValue BEE_DAMAGE_MULTIPLIER;
        public static ForgeConfigSpec.IntValue NECTAR_CAPACITY;
        public static ForgeConfigSpec.DoubleValue NECTAR_CONSUMPTION_RADIUS;
        public static ForgeConfigSpec.IntValue EFFECT_RADIUS;
        public static ForgeConfigSpec.IntValue GROW_EFFICIENCY;
        public static ForgeConfigSpec.DoubleValue HEAL_AMOUNT;

        private static void setupFragrantFlowerConfig(ForgeConfigSpec.Builder builder) {
            builder.push("fragrant_flower");
            BEE_LURING_RADIUS = builder.defineInRange("bee_luring_radius", 16, 0, Integer.MAX_VALUE);
            BEE_AGGRO_RADIUS = builder.defineInRange("bee_aggro_radius", 32, 0, Integer.MAX_VALUE);
            BEE_DAMAGE_MULTIPLIER = builder.defineInRange("bee_damage_multiplier", 3.0, 0, Integer.MAX_VALUE);
            NECTAR_CAPACITY = builder.defineInRange("nectar_capacity", 10, 0, Integer.MAX_VALUE);
            NECTAR_CONSUMPTION_RADIUS = builder.defineInRange("nectar_consumption_radius", 3.0F, 0, Integer.MAX_VALUE);
            EFFECT_RADIUS = builder.defineInRange("effect_radius", 5, 0, Integer.MAX_VALUE);
            GROW_EFFICIENCY = builder.defineInRange("grow_efficiency", 2, 0, Integer.MAX_VALUE);
            HEAL_AMOUNT = builder.defineInRange("heal_amount", 10.0, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class HolyLocket {
        public static ForgeConfigSpec.DoubleValue DAMAGE_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue ARSON_CHANCE;
        public static ForgeConfigSpec.IntValue BURN_DURATION;

        private static void setupHolyLocketConfig(ForgeConfigSpec.Builder builder) {
            builder.push("holy_locket");
            DAMAGE_MULTIPLIER = builder.defineInRange("damage_multiplier", 1.5, Integer.MIN_VALUE, Integer.MAX_VALUE);
            ARSON_CHANCE = builder.defineInRange("arson_chance", 0.25, 0, Integer.MAX_VALUE);
            BURN_DURATION = builder.defineInRange("burn_duration", 4, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class HunterBelt {
        public static ForgeConfigSpec.DoubleValue PLAYER_DAMAGE_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue PET_DAMAGE_MULTIPLIER;
        public static ForgeConfigSpec.IntValue ADDITIONAL_LOOTING;

        private static void setupHunterBeltConfig(ForgeConfigSpec.Builder builder) {
            builder.push("hunter_belt");
            PLAYER_DAMAGE_MULTIPLIER = builder.defineInRange("player_damage_multiplier", 2.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            PET_DAMAGE_MULTIPLIER = builder.defineInRange("pet_damage_multiplier", 3.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            ADDITIONAL_LOOTING = builder.defineInRange("additional_looting", 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class IceBreaker {
        public static ForgeConfigSpec.IntValue MIN_FALL_DISTANCE;
        public static ForgeConfigSpec.DoubleValue FALL_MOTION_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue STOMP_COOLDOWN_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue DEALT_DAMAGE_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue MAX_DEALT_DAMAGE;
        public static ForgeConfigSpec.DoubleValue STOMP_RADIUS_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue STOMP_MOTION_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue INCOMING_FALL_DAMAGE_MULTIPLIER;

        private static void setupIceBreakerConfig(ForgeConfigSpec.Builder builder) {
            builder.push("ice_breaker");
            MIN_FALL_DISTANCE = builder.defineInRange("min_fall_distance", 3, 0, Integer.MAX_VALUE);
            FALL_MOTION_MULTIPLIER = builder.defineInRange("fall_motion_multiplier", 1.05, 0, Integer.MAX_VALUE);
            STOMP_COOLDOWN_MULTIPLIER = builder.defineInRange("stomp_cooldown_multiplier", 3.0, 0, Integer.MAX_VALUE);
            DEALT_DAMAGE_MULTIPLIER = builder.defineInRange("dealt_damage_multiplier", 1.0, 0, Integer.MAX_VALUE);
            MAX_DEALT_DAMAGE = builder.defineInRange("max_dealt_damage", 100.0, 0, Integer.MAX_VALUE);
            STOMP_RADIUS_MULTIPLIER = builder.defineInRange("stomp_radius_multiplier", 0.5, 0, Integer.MAX_VALUE);
            STOMP_MOTION_MULTIPLIER = builder.defineInRange("stomp_motion_multiplier", 1.01, 0, Integer.MAX_VALUE);
            INCOMING_FALL_DAMAGE_MULTIPLIER = builder.defineInRange("incoming_fall_damage_multiplier", 0.0, 0, Integer.MAX_VALUE);
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
        public static ForgeConfigSpec.DoubleValue ATTACK_RADIUS_MULTIPLIER;

        private static void setupJellyfishNecklaceConfig(ForgeConfigSpec.Builder builder) {
            builder.push("jellyfish_necklace");
            TIME_PER_CHARGE = builder.defineInRange("time_per_charge", 60, Integer.MIN_VALUE, Integer.MAX_VALUE);
            MAX_CHARGES_AMOUNT = builder.defineInRange("max_charges_amount", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
            DAMAGE_PER_CHARGE = builder.defineInRange("damage_per_charge", 10.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            HEALING_MULTIPLIER = builder.defineInRange("healing_multiplier", 2.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            ATTACK_RADIUS_MULTIPLIER = builder.defineInRange("attack_radius_multiplier", 1.2, 0, Integer.MAX_VALUE);
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

    public static class MagicMirror {
        public static ForgeConfigSpec.IntValue USAGE_COOLDOWN;

        private static void setupMagicMirrorConfig(ForgeConfigSpec.Builder builder) {
            builder.push("magic_mirror");
            USAGE_COOLDOWN = builder.defineInRange("usage_cooldown", 60, 0, Integer.MAX_VALUE);
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
            DEALING_DAMAGE_MULTIPLIER_PER_STACK = builder.defineInRange("dealing_damage_multiplier_per_stack", 0.1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            INCOMING_DAMAGE_MULTIPLIER_PER_STACK = builder.defineInRange("incoming_damage_multiplier_per_stack", 0.05, Integer.MIN_VALUE, Integer.MAX_VALUE);
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

    public static class SoulDevourer {
        public static ForgeConfigSpec.IntValue TIME_PER_SOUL_DECREASE;
        public static ForgeConfigSpec.IntValue MIN_SOUL_DECREASE_AMOUNT;
        public static ForgeConfigSpec.DoubleValue SOUL_DECREASE_MULTIPLIER_PER_SOUL;
        public static ForgeConfigSpec.IntValue MIN_SOUL_AMOUNT_FOR_EXPLOSION;
        public static ForgeConfigSpec.IntValue EXPLOSION_PREPARING_TIME;
        public static ForgeConfigSpec.DoubleValue EXPLOSION_RADIUS;
        public static ForgeConfigSpec.DoubleValue EXPLOSION_VELOCITY_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue MIN_EXPLOSION_DAMAGE_AMOUNT;
        public static ForgeConfigSpec.DoubleValue EXPLOSION_DAMAGE_PER_SOUL_MULTIPLIER;
        public static ForgeConfigSpec.IntValue EXPLOSION_COOLDOWN;
        public static ForgeConfigSpec.IntValue SOUL_CAPACITY;
        public static ForgeConfigSpec.DoubleValue SOUL_PER_HEALTH_MULTIPLIER;
        public static ForgeConfigSpec.DoubleValue ADDITIONAL_DAMAGE_PER_SOUL_MULTIPLIER;

        private static void setupSoulDevourerConfig(ForgeConfigSpec.Builder builder) {
            builder.push("soul_devourer");
            TIME_PER_SOUL_DECREASE = builder.defineInRange("time_per_soul_decrease", 10, 0, Integer.MAX_VALUE);
            MIN_SOUL_DECREASE_AMOUNT = builder.defineInRange("min_soul_decrease_amount", 5, 0, Integer.MAX_VALUE);
            SOUL_DECREASE_MULTIPLIER_PER_SOUL = builder.defineInRange("soul_decrease_multiplier_per_soul", 0.1F, 0, Integer.MAX_VALUE);
            MIN_SOUL_AMOUNT_FOR_EXPLOSION = builder.defineInRange("min_soul_amount_for_explosion", 50, 0, Integer.MAX_VALUE);
            EXPLOSION_PREPARING_TIME = builder.defineInRange("explosion_preparing_time", 12, 0, Integer.MAX_VALUE);
            EXPLOSION_RADIUS = builder.defineInRange("explosion_radius", 10.0F, 0, Integer.MAX_VALUE);
            EXPLOSION_VELOCITY_MULTIPLIER = builder.defineInRange("explosion_velocity_multiplier", 5.0F, 0, Integer.MAX_VALUE);
            MIN_EXPLOSION_DAMAGE_AMOUNT = builder.defineInRange("min_explosion_damage_amount", 2.0, 0, Integer.MAX_VALUE);
            EXPLOSION_DAMAGE_PER_SOUL_MULTIPLIER = builder.defineInRange("explosion_damage_per_soul_multiplier", 0.25, 0, Integer.MAX_VALUE);
            EXPLOSION_COOLDOWN = builder.defineInRange("explosion_cooldown", 60, 0, Integer.MAX_VALUE);
            SOUL_CAPACITY = builder.defineInRange("soul_capacity", 100, 0, Integer.MAX_VALUE);
            SOUL_PER_HEALTH_MULTIPLIER = builder.defineInRange("soul_per_health_multiplier", 0.25, 0, Integer.MAX_VALUE);
            ADDITIONAL_DAMAGE_PER_SOUL_MULTIPLIER = builder.defineInRange("additional_damage_per_soul_multiplier", 0.05, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class SpaceDissector {
        public static ForgeConfigSpec.DoubleValue MOVEMENT_SPEED;
        public static ForgeConfigSpec.IntValue MAX_THROWN_TIME;
        public static ForgeConfigSpec.IntValue TIME_BEFORE_RETURN;
        public static ForgeConfigSpec.IntValue DISTANCE_FOR_TELEPORT;
        public static ForgeConfigSpec.IntValue COOLDOWN_AFTER_TELEPORT;
        public static ForgeConfigSpec.IntValue COOLDOWN_AFTER_RETURN;
        public static ForgeConfigSpec.IntValue MAX_BOUNCES_AMOUNT;
        public static ForgeConfigSpec.IntValue ADDITIONAL_TIME_PER_BOUNCE;
        public static ForgeConfigSpec.DoubleValue BASE_DAMAGE_AMOUNT;
        public static ForgeConfigSpec.DoubleValue DAMAGE_MULTIPLIER_PER_BOUNCE;

        private static void setupSpaceDissectorConfig(ForgeConfigSpec.Builder builder) {
            builder.push("space_dissector");
            MOVEMENT_SPEED = builder.defineInRange("movement_speed", 0.75, 0, Integer.MAX_VALUE);
            MAX_THROWN_TIME = builder.defineInRange("max_thrown_time", 60, 0, Integer.MAX_VALUE);
            TIME_BEFORE_RETURN = builder.defineInRange("time_before_return", 10, 0, Integer.MAX_VALUE);
            DISTANCE_FOR_TELEPORT = builder.defineInRange("distance_for_teleport", 5, 0, Integer.MAX_VALUE);
            COOLDOWN_AFTER_TELEPORT = builder.defineInRange("cooldown_after_teleport", 30, 0, Integer.MAX_VALUE);
            COOLDOWN_AFTER_RETURN = builder.defineInRange("cooldown_after_return", 5, 0, Integer.MAX_VALUE);
            MAX_BOUNCES_AMOUNT = builder.defineInRange("max_bounces_amount", 10, 0, Integer.MAX_VALUE);
            ADDITIONAL_TIME_PER_BOUNCE = builder.defineInRange("additional_time_per_bounce", 4, 0, Integer.MAX_VALUE);
            BASE_DAMAGE_AMOUNT = builder.defineInRange("base_damage_amount", 4.0, 0, Integer.MAX_VALUE);
            DAMAGE_MULTIPLIER_PER_BOUNCE = builder.defineInRange("damage_multiplier_per_bounce", 2.0, 0, Integer.MAX_VALUE);
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
            FALLING_STAR_SUMMON_CHANCE = builder.defineInRange("falling_star_summon_chance", 0.15, 0, 1);
            FALLING_STAR_DAMAGE_MULTIPLIER = builder.defineInRange("falling_star_damage_multiplier", 0.5, Integer.MIN_VALUE, Integer.MAX_VALUE);
            FALLING_STAR_DAMAGE_RADIUS = builder.defineInRange("falling_star_damage_radius", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
            MIN_DAMAGE_AMOUNT = builder.defineInRange("min_damage_amount", 3.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class RelicsWorldgen {
        public static ForgeConfigSpec.BooleanValue RELICS_WORLDGEN_ENABLED;
        public static ForgeConfigSpec.DoubleValue ARROW_QUIVER_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue BASTION_RING_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue CAMOUFLAGE_RING_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue CHORUS_INHIBITOR_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue DELAY_RING_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue DROWNED_BELT_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue ELYTRA_BOOSTER_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue ENDERS_HAND_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue FRAGRANT_FLOWER_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue HOLY_LOCKET_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue HUNTER_BELT_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue ICE_BREAKER_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue ICE_SKATES_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue JELLYFISH_NECKLACE_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue LUCKY_HORSESHOE_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue MAGIC_MIRROR_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue MAGMA_WALKER_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue MIDNIGHT_ROBE_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue RAGE_GLOVE_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue REFLECTION_NECKLACE_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue SCARAB_TALISMAN_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue SOUL_DEVOURER_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue SPACE_DISSECTOR_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue SPATIAL_SIGN_GEN_CHANCE;
        public static ForgeConfigSpec.DoubleValue STELLAR_CATALYST_GEN_CHANCE;

        private static void setupWorldgenConfig(ForgeConfigSpec.Builder builder) {
            RELICS_WORLDGEN_ENABLED = builder.define("relics_worldgen_enabled", true);
            ARROW_QUIVER_GEN_CHANCE = builder.defineInRange("arrow_quiver_gen_chance", 0.2, 0, 1);
            BASTION_RING_GEN_CHANCE = builder.defineInRange("bastion_ring_gen_chance", 0.15, 0, 1);
            CAMOUFLAGE_RING_GEN_CHANCE = builder.defineInRange("camouflage_ring_gen_chance", 0.2, 0, 1);
            CHORUS_INHIBITOR_GEN_CHANCE = builder.defineInRange("chorus_inhibitor_gen_chance", 0.15, 0, 1);
            DELAY_RING_GEN_CHANCE = builder.defineInRange("delay_ring_gen_chance", 0.05, 0, 1);
            DROWNED_BELT_GEN_CHANCE = builder.defineInRange("drowned_belt_gen_chance", 0.2, 0, 1);
            ELYTRA_BOOSTER_GEN_CHANCE = builder.defineInRange("elytra_booster_gen_chance", 0.15, 0, 1);
            ENDERS_HAND_GEN_CHANCE = builder.defineInRange("enders_hand_gen_chance", 0.3, 0, 1);
            FRAGRANT_FLOWER_GEN_CHANCE = builder.defineInRange("fragrant_flower_gen_chance", 0.2, 0, 1);
            HOLY_LOCKET_GEN_CHANCE = builder.defineInRange("drowned_belt_gen_chance", 0.3, 0, 1);
            HUNTER_BELT_GEN_CHANCE = builder.defineInRange("hunter_belt_gen_chance", 0.2, 0, 1);
            ICE_BREAKER_GEN_CHANCE = builder.defineInRange("ice_breaker_gen_chance", 0.2, 0, 1);
            ICE_SKATES_GEN_CHANCE = builder.defineInRange("ice_skates_gen_chance", 0.2, 0, 1);
            JELLYFISH_NECKLACE_GEN_CHANCE = builder.defineInRange("jellyfish_necklace_gen_chance", 0.2, 0, 1);
            LUCKY_HORSESHOE_GEN_CHANCE = builder.defineInRange("lucky_horseshoe_gen_chance", 0.15, 0, 1);
            MAGIC_MIRROR_GEN_CHANCE = builder.defineInRange("magic_mirror_gen_chance", 0.15, 0, 1);
            MAGMA_WALKER_GEN_CHANCE = builder.defineInRange("magma_walker_gen_chance", 0.12, 0, 1);
            MIDNIGHT_ROBE_GEN_CHANCE = builder.defineInRange("midnight_robe_gen_chance", 0.15, 0, 1);
            RAGE_GLOVE_GEN_CHANCE = builder.defineInRange("rage_glove_gen_chance", 0.05, 0, 1);
            REFLECTION_NECKLACE_GEN_CHANCE = builder.defineInRange("reflection_necklace_gen_chance", 0.05, 0, 1);
            SCARAB_TALISMAN_GEN_CHANCE = builder.defineInRange("scarab_talisman_gen_chance", 0.14, 0, 1);
            SOUL_DEVOURER_GEN_CHANCE = builder.defineInRange("soul_devourer_gen_chance", 0.05, 0, 1);
            SPACE_DISSECTOR_GEN_CHANCE = builder.defineInRange("space_dissector_gen_chance", 0.05, 0, 1);
            SPATIAL_SIGN_GEN_CHANCE = builder.defineInRange("spatial_sign_gen_chance", 0.25, 0, 1);

            builder.pop();
        }
    }

    public static class RelicsCompatibility {
        public static ForgeConfigSpec.BooleanValue WARN_ABOUT_OLD_FORGE;

        private static void setupCompatibilityConfig(ForgeConfigSpec.Builder builder) {
            WARN_ABOUT_OLD_FORGE = builder.define("warn_about_old_forge", true);
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
        ElytraBooster.setupElytraBoosterConfig(builder);
        EndersHand.setupEndersHandConfig(builder);
        FragrantFlower.setupFragrantFlowerConfig(builder);
        HolyLocket.setupHolyLocketConfig(builder);
        HunterBelt.setupHunterBeltConfig(builder);
        IceBreaker.setupIceBreakerConfig(builder);
        IceSkates.setupIceSkatesConfig(builder);
        JellyfishNecklace.setupJellyfishNecklaceConfig(builder);
        LuckyHorseshoe.setupLuckyHorseshoeConfig(builder);
        MagicMirror.setupMagicMirrorConfig(builder);
        MidnightRobe.setupMidnightRobeConfig(builder);
        RageGlove.setupRageGloveConfig(builder);
        ReflectionNecklace.setupReflectionNecklaceConfig(builder);
        ScarabTalisman.setupScarabTalismanConfig(builder);
        SoulDevourer.setupSoulDevourerConfig(builder);
        SpaceDissector.setupSpaceDissectorConfig(builder);
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

        builder.push("compatibility");
        RelicsCompatibility.setupCompatibilityConfig(builder);
        builder.pop();

        RELICS_CONFIG = builder.build();
    }
}