package com.sxilverr.spawnconditions.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.sxilverr.spawnconditions.SpawnConditions;
import com.sxilverr.spawnconditions.config.SpawnConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final Path PATH =
            FabricLoader.getInstance().getConfigDir().resolve(SpawnConditions.MOD_ID + ".json");

    private static Data data = new Data();

    private Config() {
    }

    public static Data data() {
        return data;
    }

    public static void load() {
        if (Files.isRegularFile(PATH)) {
            try (BufferedReader source = Files.newBufferedReader(PATH, StandardCharsets.UTF_8);
                 JsonReader reader = new JsonReader(source)) {
                reader.setLenient(true);
                Data parsed = GSON.fromJson(reader, Data.class);
                if (parsed != null) {
                    data = parsed;
                }
            } catch (IOException | RuntimeException e) {
                data = new Data();
            }
        } else {
            writeDefaults();
        }
        data.bake();
    }

    public static void persist() {
        save();
        data.bake();
    }

    private static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(data), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private static void writeDefaults() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, DEFAULT_FILE, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    public static final class WorldSpawnData {
        public boolean randomize = false;
        public String area = "RING";
        public int centerX = 0;
        public int centerZ = 0;
        public int minRadius = 0;
        public int maxRadius = 10000;
        public int minX = -10000;
        public int maxX = 10000;
        public int minZ = -10000;
        public int maxZ = 10000;
        public int attempts = 32;
    }

    public static final class PlacementData {
        public boolean fluidBlocksColumn = true;
        public String liquidLanding = "SURFACE";
        public String groundRequirement = "FULL_FACE";
        public int requiredHeadroom = 0;
        public boolean headroomAllowsFluid = true;
        public int maxSearchDepth = 0;
        public int minY = -2048;
        public int maxY = 2048;
        public boolean limitHorizontally = false;
        public int minX = -30000000;
        public int maxX = 30000000;
        public int minZ = -30000000;
        public int maxZ = 30000000;
        public List<String> biomeAllowList = new ArrayList<>();
        public List<String> biomeDenyList = new ArrayList<>();
        public List<String> blockAllowList = new ArrayList<>();
        public List<String> blockDenyList = new ArrayList<>();
        public List<String> fluidAllowList = new ArrayList<>();
        public List<String> fluidDenyList = new ArrayList<>();
    }

    public static final class RespawnData {
        public boolean preventSpawnSetting = false;
        public String spawnBlockedMessage = "&cYou cannot set your spawn.";
    }

    public static final class Data {
        public boolean enabled = true;
        public WorldSpawnData worldSpawn = new WorldSpawnData();
        public PlacementData placement = new PlacementData();
        public RespawnData respawn = new RespawnData();

        public void bake() {
            if (this.worldSpawn == null) {
                this.worldSpawn = new WorldSpawnData();
            }
            if (this.placement == null) {
                this.placement = new PlacementData();
            }
            if (this.respawn == null) {
                this.respawn = new RespawnData();
            }

            SpawnConfig.enabled = this.enabled;

            SpawnConfig.worldSpawn.randomize = this.worldSpawn.randomize;
            SpawnConfig.worldSpawn.area = SpawnConfig.parseEnum(
                    this.worldSpawn.area, SpawnConfig.WorldSpawnArea.values(), SpawnConfig.WorldSpawnArea.RING);
            SpawnConfig.worldSpawn.centerX = this.worldSpawn.centerX;
            SpawnConfig.worldSpawn.centerZ = this.worldSpawn.centerZ;
            SpawnConfig.worldSpawn.minRadius = this.worldSpawn.minRadius;
            SpawnConfig.worldSpawn.maxRadius = this.worldSpawn.maxRadius;
            SpawnConfig.worldSpawn.minX = this.worldSpawn.minX;
            SpawnConfig.worldSpawn.maxX = this.worldSpawn.maxX;
            SpawnConfig.worldSpawn.minZ = this.worldSpawn.minZ;
            SpawnConfig.worldSpawn.maxZ = this.worldSpawn.maxZ;
            SpawnConfig.worldSpawn.attempts = this.worldSpawn.attempts;
            SpawnConfig.worldSpawn.normalise();

            SpawnConfig.placement.fluidBlocksColumn = this.placement.fluidBlocksColumn;
            SpawnConfig.placement.liquidLanding = SpawnConfig.parseEnum(
                    this.placement.liquidLanding, SpawnConfig.LiquidLanding.values(), SpawnConfig.LiquidLanding.SURFACE);
            SpawnConfig.placement.groundRequirement = SpawnConfig.parseEnum(
                    this.placement.groundRequirement, SpawnConfig.GroundRequirement.values(), SpawnConfig.GroundRequirement.FULL_FACE);
            SpawnConfig.placement.requiredHeadroom = this.placement.requiredHeadroom;
            SpawnConfig.placement.headroomAllowsFluid = this.placement.headroomAllowsFluid;
            SpawnConfig.placement.maxSearchDepth = this.placement.maxSearchDepth;
            SpawnConfig.placement.minY = this.placement.minY;
            SpawnConfig.placement.maxY = this.placement.maxY;
            SpawnConfig.placement.limitHorizontally = this.placement.limitHorizontally;
            SpawnConfig.placement.minX = this.placement.minX;
            SpawnConfig.placement.maxX = this.placement.maxX;
            SpawnConfig.placement.minZ = this.placement.minZ;
            SpawnConfig.placement.maxZ = this.placement.maxZ;
            SpawnConfig.placement.setBiomeLists(
                    this.placement.biomeAllowList == null ? List.of() : this.placement.biomeAllowList,
                    this.placement.biomeDenyList == null ? List.of() : this.placement.biomeDenyList);
            SpawnConfig.placement.setBlockLists(
                    this.placement.blockAllowList == null ? List.of() : this.placement.blockAllowList,
                    this.placement.blockDenyList == null ? List.of() : this.placement.blockDenyList);
            SpawnConfig.placement.setFluidLists(
                    this.placement.fluidAllowList == null ? List.of() : this.placement.fluidAllowList,
                    this.placement.fluidDenyList == null ? List.of() : this.placement.fluidDenyList);
            SpawnConfig.placement.normalise();

            SpawnConfig.respawn.preventSpawnSetting = this.respawn.preventSpawnSetting;
            SpawnConfig.respawn.spawnBlockedMessage =
                    this.respawn.spawnBlockedMessage == null ? "" : this.respawn.spawnBlockedMessage;
            SpawnConfig.respawn.normalise();
        }
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
                // Message shown when a player cannot set their spawn. Leave empty for no message.
                // Use & for colour and format codes, like &c for red or &l for bold.
                "spawnBlockedMessage": "&cYou cannot set your spawn."
              }
            }
            """;
}
