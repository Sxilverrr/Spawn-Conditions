package com.sxilverr.spawnconditions.neoforge;

import com.sxilverr.spawnconditions.config.SpawnConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Master switch.")
            .define("enabled", true);

    private static final ModConfigSpec.BooleanValue WS_RANDOMIZE;
    private static final ModConfigSpec.EnumValue<SpawnConfig.WorldSpawnArea> WS_AREA;
    private static final ModConfigSpec.IntValue WS_CENTER_X;
    private static final ModConfigSpec.IntValue WS_CENTER_Z;
    private static final ModConfigSpec.IntValue WS_MIN_RADIUS;
    private static final ModConfigSpec.IntValue WS_MAX_RADIUS;
    private static final ModConfigSpec.IntValue WS_MIN_X;
    private static final ModConfigSpec.IntValue WS_MAX_X;
    private static final ModConfigSpec.IntValue WS_MIN_Z;
    private static final ModConfigSpec.IntValue WS_MAX_Z;
    private static final ModConfigSpec.IntValue WS_ATTEMPTS;

    private static final ModConfigSpec.BooleanValue FLUID_BLOCKS_COLUMN;
    private static final ModConfigSpec.EnumValue<SpawnConfig.LiquidLanding> LIQUID_LANDING;
    private static final ModConfigSpec.EnumValue<SpawnConfig.GroundRequirement> GROUND_REQUIREMENT;
    private static final ModConfigSpec.IntValue REQUIRED_HEADROOM;
    private static final ModConfigSpec.BooleanValue HEADROOM_ALLOWS_FLUID;
    private static final ModConfigSpec.IntValue MAX_SEARCH_DEPTH;
    private static final ModConfigSpec.IntValue MIN_Y;
    private static final ModConfigSpec.IntValue MAX_Y;
    private static final ModConfigSpec.BooleanValue LIMIT_HORIZONTALLY;
    private static final ModConfigSpec.IntValue MIN_X;
    private static final ModConfigSpec.IntValue MAX_X;
    private static final ModConfigSpec.IntValue MIN_Z;
    private static final ModConfigSpec.IntValue MAX_Z;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> BIOME_ALLOW;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> BIOME_DENY;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCK_ALLOW;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCK_DENY;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> FLUID_ALLOW;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> FLUID_DENY;

    private static final ModConfigSpec.BooleanValue PREVENT_SPAWN_SETTING;
    private static final ModConfigSpec.ConfigValue<String> SPAWN_BLOCKED_MESSAGE;

    static {
        BUILDER.comment("World spawn point.")
                .push("World Spawn");
        WS_RANDOMIZE = BUILDER
                .comment("Randomize the world spawn instead of using 0,0.")
                .define("randomize", false);
        WS_AREA = BUILDER
                .comment("Shape to pick the spawn from.")
                .defineEnum("area", SpawnConfig.WorldSpawnArea.RING);
        WS_CENTER_X = BUILDER
                .comment("RING: the x coordinate to start from.")
                .defineInRange("centerX", 0, -30000000, 30000000);
        WS_CENTER_Z = BUILDER
                .comment("RING: the z coordinate to start from.")
                .defineInRange("centerZ", 0, -30000000, 30000000);
        WS_MIN_RADIUS = BUILDER
                .comment("RING: minimum for how far out to spawn.")
                .defineInRange("minRadius", 0, 0, 30000000);
        WS_MAX_RADIUS = BUILDER
                .comment("RING: maximum for how far out to spawn.")
                .defineInRange("maxRadius", 10000, 0, 30000000);
        WS_MIN_X = BUILDER
                .comment("BOX: lowest x coordinate that a spawn can use.")
                .defineInRange("minX", -10000, -30000000, 30000000);
        WS_MAX_X = BUILDER
                .comment("BOX: highest x coordinate that a spawn can use.")
                .defineInRange("maxX", 10000, -30000000, 30000000);
        WS_MIN_Z = BUILDER
                .comment("BOX: lowest z coordinate that a spawn can use.")
                .defineInRange("minZ", -10000, -30000000, 30000000);
        WS_MAX_Z = BUILDER
                .comment("BOX: highest z coordinate that a spawn can use.")
                .defineInRange("maxZ", 10000, -30000000, 30000000);
        WS_ATTEMPTS = BUILDER
                .comment("How many attempts before giving up. Each attempt loads a chunk.")
                .defineInRange("attempts", 32, 1, 1024);
        BUILDER.pop();

        BUILDER.comment("Spawn conditions.")
                .push("Placement");
        FLUID_BLOCKS_COLUMN = BUILDER
                .comment("Skip the whole spot when a fluid is not allowed. Off keeps looking below it.")
                .define("fluidBlocksColumn", true);
        LIQUID_LANDING = BUILDER
                .comment("Land on top of a liquid or on the ground under it.")
                .defineEnum("liquidLanding", SpawnConfig.LiquidLanding.SURFACE);
        GROUND_REQUIREMENT = BUILDER
                .comment("What counts as ground to stand on.")
                .defineEnum("groundRequirement", SpawnConfig.GroundRequirement.FULL_FACE);
        REQUIRED_HEADROOM = BUILDER
                .comment("Empty space needed above the player. 0 is vanilla.")
                .defineInRange("requiredHeadroom", 0, 0, 64);
        HEADROOM_ALLOWS_FLUID = BUILDER
                .comment("Count liquid as empty space above the player.")
                .define("headroomAllowsFluid", true);
        MAX_SEARCH_DEPTH = BUILDER
                .comment("Blocks the search may descend before giving up. 0 is unlimited.")
                .defineInRange("maxSearchDepth", 0, 0, 4096);
        MIN_Y = BUILDER
                .comment("Lowest y coordinate that a spawn can use.")
                .defineInRange("minY", -2048, -2048, 2048);
        MAX_Y = BUILDER
                .comment("Highest y coordinate that a spawn can use. Lower it for cave spawns.")
                .defineInRange("maxY", 2048, -2048, 2048);
        LIMIT_HORIZONTALLY = BUILDER
                .comment("Turn on the x and z limits below.")
                .define("limitHorizontally", false);
        MIN_X = BUILDER
                .comment("Lowest x coordinate that a spawn can use.")
                .defineInRange("minX", -30000000, -30000000, 30000000);
        MAX_X = BUILDER
                .comment("Highest x coordinate that a spawn can use.")
                .defineInRange("maxX", 30000000, -30000000, 30000000);
        MIN_Z = BUILDER
                .comment("Lowest z coordinate that a spawn can use.")
                .defineInRange("minZ", -30000000, -30000000, 30000000);
        MAX_Z = BUILDER
                .comment("Highest z coordinate that a spawn can use.")
                .defineInRange("maxZ", 30000000, -30000000, 30000000);
        BIOME_ALLOW = BUILDER
                .comment("Only spawn in these biomes. Leave empty for any.",
                        "Use modid:biome or #modid:tag")
                .defineListAllowEmpty("biomeAllowList", () -> List.of(), () -> "", Config::isValidListEntry);
        BIOME_DENY = BUILDER
                .comment("Never spawn in these biomes.",
                        "Use modid:biome or #modid:tag")
                .defineListAllowEmpty("biomeDenyList", () -> List.of(), () -> "", Config::isValidListEntry);
        BLOCK_ALLOW = BUILDER
                .comment("Only spawn on these blocks. Leave empty for any.",
                        "Use modid:block or #modid:tag")
                .defineListAllowEmpty("blockAllowList", () -> List.of(), () -> "", Config::isValidListEntry);
        BLOCK_DENY = BUILDER
                .comment("Never spawn on these blocks.",
                        "Use modid:block or #modid:tag")
                .defineListAllowEmpty("blockDenyList", () -> List.of(), () -> "", Config::isValidListEntry);
        FLUID_ALLOW = BUILDER
                .comment("Only spawn in these fluids. Leave empty to never spawn in fluids.",
                        "Use modid:fluid or #modid:tag. Tags also cover flowing fluid.")
                .defineListAllowEmpty("fluidAllowList", () -> List.of(), () -> "", Config::isValidListEntry);
        FLUID_DENY = BUILDER
                .comment("Never spawn in these fluids.",
                        "Use modid:fluid or #modid:tag. Tags also cover flowing fluid.")
                .defineListAllowEmpty("fluidDenyList", () -> List.of(), () -> "", Config::isValidListEntry);
        BUILDER.pop();

        BUILDER.comment("Respawn conditions.")
                .push("Respawn");
        PREVENT_SPAWN_SETTING = BUILDER
                .comment("Player cannot set their spawn. Beds and respawn anchors will not save a spawn point.")
                .define("preventSpawnSetting", false);
        SPAWN_BLOCKED_MESSAGE = BUILDER
                .comment("Message shown when a player cannot set their spawn. Leave empty for no message.",
                        "Use & for colour and format codes, like &c for red or &l for bold.")
                .define("spawnBlockedMessage", "&cYou cannot set your spawn.");
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }

    private static boolean isValidListEntry(Object value) {
        return value instanceof String s && !s.isBlank();
    }

    static void bake() {
        SpawnConfig.enabled = ENABLED.get();

        SpawnConfig.worldSpawn.randomize = WS_RANDOMIZE.get();
        SpawnConfig.worldSpawn.area = WS_AREA.get();
        SpawnConfig.worldSpawn.centerX = WS_CENTER_X.get();
        SpawnConfig.worldSpawn.centerZ = WS_CENTER_Z.get();
        SpawnConfig.worldSpawn.minRadius = WS_MIN_RADIUS.get();
        SpawnConfig.worldSpawn.maxRadius = WS_MAX_RADIUS.get();
        SpawnConfig.worldSpawn.minX = WS_MIN_X.get();
        SpawnConfig.worldSpawn.maxX = WS_MAX_X.get();
        SpawnConfig.worldSpawn.minZ = WS_MIN_Z.get();
        SpawnConfig.worldSpawn.maxZ = WS_MAX_Z.get();
        SpawnConfig.worldSpawn.attempts = WS_ATTEMPTS.get();
        SpawnConfig.worldSpawn.normalise();

        SpawnConfig.placement.fluidBlocksColumn = FLUID_BLOCKS_COLUMN.get();
        SpawnConfig.placement.liquidLanding = LIQUID_LANDING.get();
        SpawnConfig.placement.groundRequirement = GROUND_REQUIREMENT.get();
        SpawnConfig.placement.requiredHeadroom = REQUIRED_HEADROOM.get();
        SpawnConfig.placement.headroomAllowsFluid = HEADROOM_ALLOWS_FLUID.get();
        SpawnConfig.placement.maxSearchDepth = MAX_SEARCH_DEPTH.get();
        SpawnConfig.placement.minY = MIN_Y.get();
        SpawnConfig.placement.maxY = MAX_Y.get();
        SpawnConfig.placement.limitHorizontally = LIMIT_HORIZONTALLY.get();
        SpawnConfig.placement.minX = MIN_X.get();
        SpawnConfig.placement.maxX = MAX_X.get();
        SpawnConfig.placement.minZ = MIN_Z.get();
        SpawnConfig.placement.maxZ = MAX_Z.get();
        SpawnConfig.placement.setBiomeLists(BIOME_ALLOW.get(), BIOME_DENY.get());
        SpawnConfig.placement.setBlockLists(BLOCK_ALLOW.get(), BLOCK_DENY.get());
        SpawnConfig.placement.setFluidLists(FLUID_ALLOW.get(), FLUID_DENY.get());
        SpawnConfig.placement.normalise();

        SpawnConfig.respawn.preventSpawnSetting = PREVENT_SPAWN_SETTING.get();
        SpawnConfig.respawn.spawnBlockedMessage = SPAWN_BLOCKED_MESSAGE.get();
        SpawnConfig.respawn.normalise();
    }
}
