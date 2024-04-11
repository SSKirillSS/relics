package it.hurts.sskirillss.relics.items.relics.base.data.loot.misc;

public class LootCollections {
    public static final LootCollection VILLAGE = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*village[\\w_\\/]*", 0.025F)
            .build();

    public static final LootCollection BASTION = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*(bastion|piglin)[\\w_\\/]*", 0.025F)
            .build();

    public static final LootCollection NETHER = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*(nether|hell|lava|magma|fire|burn|fortress)[\\w_\\/]*", 0.025F)
            .entry("minecraft:chests/ruined_portal", 0.025F)
            .entry(BASTION)
            .build();

    public static final LootCollection JUNGLE = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*(jungle|temple)[\\w_\\/]*", 0.025F)
            .build();

    public static final LootCollection DESERT = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*(desert|sand|pyramid)[\\w_\\/]*", 0.025F)
            .build();

    public static final LootCollection AQUATIC = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*(water|ocean|river|(?<!air)ship|aqua)[\\w_\\/]*", 0.025F)
            .entry("minecraft:chests/buried_treasure", 0.025F)
            .build();

    public static final LootCollection PILLAGE = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*(pillag|outpost)[\\w_\\/]*", 0.025F)
            .entry("minecraft:chests/woodland_mansion", 0.025F)
            .build();

    public static final LootCollection COLD = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*(frosz?|taiga|cold|winter|snow|icey?|glac)[\\w_\\/]*", 0.025F)
            .entry("minecraft:chests/igloo_chest", 0.025F)
            .entry("minecraft:chests/village/village_fletcher", 0.025F)
            .build();

    public static final LootCollection END = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*(end|warp)[\\w_\\/]*", 0.025F)
            .build();

    public static final LootCollection SCULK = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*(warden|sculk|echo)[\\w_\\/]*", 0.025F)
            .entry("minecraft:chests/ancient_city", 0.025F)
            .entry("minecraft:chests/ancient_city_ice_box", 0.025F)
            .build();

    public static final LootCollection ANTHROPOGENIC = LootCollection.builder()
            .entry("[\\w]+:chests\\/[\\w_\\/]*(mineshaft|city|stronghold)[\\w_\\/]*", 0.025F)
            .entry(PILLAGE)
            .build();
}