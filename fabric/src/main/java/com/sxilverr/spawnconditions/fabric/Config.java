package com.sxilverr.spawnconditions.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.sxilverr.spawnconditions.config.SpawnConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(SpawnConfig.MOD_ID + ".json");

    private static Data data = new Data();

    private Config() {
    }

    public static Data data() {
        return data;
    }

    public static void load() {
        if (!Files.isRegularFile(PATH)) {
            write(DEFAULT_FILE);
        }
        try (JsonReader reader = new JsonReader(Files.newBufferedReader(PATH))) {
            reader.setLenient(true);
            Data parsed = GSON.fromJson(reader, Data.class);
            if (parsed != null && parsed.worldSpawn != null && parsed.placement != null && parsed.respawn != null) {
                data = parsed;
            }
        } catch (IOException | RuntimeException ignored) {
        }
        apply();
    }

    public static void save() {
        write(GSON.toJson(data));
        apply();
    }

    private static void apply() {
        SpawnConfig.enabled = data.enabled;
        SpawnConfig.worldSpawn = data.worldSpawn;
        SpawnConfig.placement = data.placement;
        SpawnConfig.respawn = data.respawn;
        SpawnConfig.bake();
    }

    private static void write(String text) {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, text);
        } catch (IOException ignored) {
        }
    }

    public static final class Data {
        public boolean enabled = true;
        public SpawnConfig.WorldSpawn worldSpawn = new SpawnConfig.WorldSpawn();
        public SpawnConfig.Placement placement = new SpawnConfig.Placement();
        public SpawnConfig.Respawn respawn = new SpawnConfig.Respawn();
    }

    private static final String DEFAULT_FILE = """
            {
              // Master switch.
              "enabled": true,

              // World spawn point.
              "worldSpawn": {
                // Randomize the world spawn instead of using 0,0.
                "randomize": false,
                // Shape to pick the spawn from.
                "area": "RING",
                // RING: the x and z coordinates to start from.
                "centerX": 0,
                "centerZ": 0,
                // RING: minimum and maximum for how far out to spawn.
                "minRadius": 0,
                "maxRadius": 10000,
                // BOX: lowest and highest x and z coordinates a spawn can use.
                "minX": -10000,
                "maxX": 10000,
                "minZ": -10000,
                "maxZ": 10000,
                // How many attempts before giving up. Each attempt loads a chunk.
                "attempts": 32
              },

              // Spawn conditions.
              "placement": {
                // Skip the whole spot when a fluid is not allowed. False keeps looking below it.
                "fluidBlocksColumn": true,
                // Land on top of a liquid or on the ground under it.
                "liquidLanding": "SURFACE",
                // What counts as ground to stand on.
                "groundRequirement": "FULL_FACE",
                // Empty space needed above the player. 0 is vanilla.
                "requiredHeadroom": 0,
                // Count liquid as empty space above the player.
                "headroomAllowsFluid": true,
                // Blocks the search may descend before giving up. 0 is unlimited.
                "maxSearchDepth": 0,
                // Lowest y coordinate that a spawn can use.
                "minY": -2048,
                // Highest y coordinate that a spawn can use. Lower it for cave spawns.
                "maxY": 2048,
                // Turn on the x and z limits below.
                "limitHorizontally": false,
                // Lowest and highest x and z coordinates a spawn can use.
                "minX": -30000000,
                "maxX": 30000000,
                "minZ": -30000000,
                "maxZ": 30000000,
                // Only spawn in these biomes. Leave empty for any. Use modid:biome or #modid:tag
                "biomeAllowList": [],
                // Never spawn in these biomes.
                "biomeDenyList": [],
                // Only spawn on these blocks. Leave empty for any.
                "blockAllowList": [],
                // Never spawn on these blocks.
                "blockDenyList": [],
                // Only spawn in these fluids. Leave empty to never spawn in fluids. Use modid:fluid or #modid:tag. Tags also cover flowing fluid.
                "fluidAllowList": [],
                // Never spawn in these fluids.
                "fluidDenyList": []
              },

              // Respawn conditions.
              "respawn": {
                // Player cannot set their spawn. Beds and respawn anchors will not save a spawn point.
                "preventSpawnSetting": false,
                // Player cannot set their spawn in these dimensions. Use modid:dimension
                "preventSpawnSettingDimensions": [],
                // Message shown when a player cannot set their spawn. Leave empty for no message.
                // Use & for colour and format codes, like &c for red or &l for bold.
                "spawnBlockedMessage": "&cYou cannot set your spawn.",
                // Beds only save a spawn point when the player sleeps in them.
                "bedSpawnRequiresSleep": false,
                // Players who die in these dimensions respawn in the same dimension. Use modid:dimension
                // Spread out by the spawnRadius gamerule unless the player has a spawn point there.
                "stayInDimensions": []
              }
            }
            """;
}
