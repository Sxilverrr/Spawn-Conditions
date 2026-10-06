package com.sxilverr.spawnconditions.forge;

import com.sxilverr.spawnconditions.config.SpawnConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = SpawnConfig.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {
    private static final int LIMIT = 30000000;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final List<Runnable> BAKERS = new ArrayList<>();
    public static final ForgeConfigSpec SPEC;

    static {
        SpawnConfig.WorldSpawn ws = SpawnConfig.worldSpawn;
        SpawnConfig.Placement pl = SpawnConfig.placement;
        SpawnConfig.Respawn rs = SpawnConfig.respawn;

        bind(BUILDER.comment("Master switch.").define("enabled", SpawnConfig.enabled), v -> SpawnConfig.enabled = v);

        BUILDER.comment("World spawn point.").push("World Spawn");
        bind(BUILDER.comment("Randomize the world spawn instead of using 0,0.")
                .define("randomize", ws.randomize), v -> ws.randomize = v);
        bind(BUILDER.comment("Shape to pick the spawn from.")
                .defineEnum("area", ws.area), v -> ws.area = v);
        bind(BUILDER.comment("RING: the x coordinate to start from.")
                .defineInRange("centerX", ws.centerX, -LIMIT, LIMIT), v -> ws.centerX = v);
        bind(BUILDER.comment("RING: the z coordinate to start from.")
                .defineInRange("centerZ", ws.centerZ, -LIMIT, LIMIT), v -> ws.centerZ = v);
        bind(BUILDER.comment("RING: minimum for how far out to spawn.")
                .defineInRange("minRadius", ws.minRadius, 0, LIMIT), v -> ws.minRadius = v);
        bind(BUILDER.comment("RING: maximum for how far out to spawn.")
                .defineInRange("maxRadius", ws.maxRadius, 0, LIMIT), v -> ws.maxRadius = v);
        bind(BUILDER.comment("BOX: lowest x coordinate that a spawn can use.")
                .defineInRange("minX", ws.minX, -LIMIT, LIMIT), v -> ws.minX = v);
        bind(BUILDER.comment("BOX: highest x coordinate that a spawn can use.")
                .defineInRange("maxX", ws.maxX, -LIMIT, LIMIT), v -> ws.maxX = v);
        bind(BUILDER.comment("BOX: lowest z coordinate that a spawn can use.")
                .defineInRange("minZ", ws.minZ, -LIMIT, LIMIT), v -> ws.minZ = v);
        bind(BUILDER.comment("BOX: highest z coordinate that a spawn can use.")
                .defineInRange("maxZ", ws.maxZ, -LIMIT, LIMIT), v -> ws.maxZ = v);
        bind(BUILDER.comment("How many attempts before giving up. Each attempt loads a chunk.")
                .defineInRange("attempts", ws.attempts, 1, 1024), v -> ws.attempts = v);
        BUILDER.pop();

        BUILDER.comment("Spawn conditions.").push("Placement");
        bind(BUILDER.comment("Skip the whole spot when a fluid is not allowed. Off keeps looking below it.")
                .define("fluidBlocksColumn", pl.fluidBlocksColumn), v -> pl.fluidBlocksColumn = v);
        bind(BUILDER.comment("Land on top of a liquid or on the ground under it.")
                .defineEnum("liquidLanding", pl.liquidLanding), v -> pl.liquidLanding = v);
        bind(BUILDER.comment("What counts as ground to stand on.")
                .defineEnum("groundRequirement", pl.groundRequirement), v -> pl.groundRequirement = v);
        bind(BUILDER.comment("Empty space needed above the player. 0 is vanilla.")
                .defineInRange("requiredHeadroom", pl.requiredHeadroom, 0, 64), v -> pl.requiredHeadroom = v);
        bind(BUILDER.comment("Count liquid as empty space above the player.")
                .define("headroomAllowsFluid", pl.headroomAllowsFluid), v -> pl.headroomAllowsFluid = v);
        bind(BUILDER.comment("Blocks the search may descend before giving up. 0 is unlimited.")
                .defineInRange("maxSearchDepth", pl.maxSearchDepth, 0, 4096), v -> pl.maxSearchDepth = v);
        bind(BUILDER.comment("Lowest y coordinate that a spawn can use.")
                .defineInRange("minY", pl.minY, -2048, 2048), v -> pl.minY = v);
        bind(BUILDER.comment("Highest y coordinate that a spawn can use. Lower it for cave spawns.")
                .defineInRange("maxY", pl.maxY, -2048, 2048), v -> pl.maxY = v);
        bind(BUILDER.comment("Turn on the x and z limits below.")
                .define("limitHorizontally", pl.limitHorizontally), v -> pl.limitHorizontally = v);
        bind(BUILDER.comment("Lowest x coordinate that a spawn can use.")
                .defineInRange("minX", pl.minX, -LIMIT, LIMIT), v -> pl.minX = v);
        bind(BUILDER.comment("Highest x coordinate that a spawn can use.")
                .defineInRange("maxX", pl.maxX, -LIMIT, LIMIT), v -> pl.maxX = v);
        bind(BUILDER.comment("Lowest z coordinate that a spawn can use.")
                .defineInRange("minZ", pl.minZ, -LIMIT, LIMIT), v -> pl.minZ = v);
        bind(BUILDER.comment("Highest z coordinate that a spawn can use.")
                .defineInRange("maxZ", pl.maxZ, -LIMIT, LIMIT), v -> pl.maxZ = v);
        list("biomeAllowList", v -> pl.biomeAllowList = v,
                "Only spawn in these biomes. Leave empty for any.", "Use modid:biome or #modid:tag");
        list("biomeDenyList", v -> pl.biomeDenyList = v,
                "Never spawn in these biomes.", "Use modid:biome or #modid:tag");
        list("blockAllowList", v -> pl.blockAllowList = v,
                "Only spawn on these blocks. Leave empty for any.", "Use modid:block or #modid:tag");
        list("blockDenyList", v -> pl.blockDenyList = v,
                "Never spawn on these blocks.", "Use modid:block or #modid:tag");
        list("fluidAllowList", v -> pl.fluidAllowList = v,
                "Only spawn in these fluids. Leave empty to never spawn in fluids.", "Use modid:fluid or #modid:tag. Tags also cover flowing fluid.");
        list("fluidDenyList", v -> pl.fluidDenyList = v,
                "Never spawn in these fluids.", "Use modid:fluid or #modid:tag. Tags also cover flowing fluid.");
        BUILDER.pop();

        BUILDER.comment("Respawn conditions.").push("Respawn");
        bind(BUILDER.comment("Player cannot set their spawn. Beds and respawn anchors will not save a spawn point.")
                .define("preventSpawnSetting", rs.preventSpawnSetting), v -> rs.preventSpawnSetting = v);
        list("preventSpawnSettingDimensions", v -> rs.preventSpawnSettingDimensions = v,
                "Player cannot set their spawn in these dimensions.", "Use modid:dimension");
        bind(BUILDER.comment("Message shown when a player cannot set their spawn. Leave empty for no message.",
                        "Use & for colour and format codes, like &c for red or &l for bold.")
                .define("spawnBlockedMessage", rs.spawnBlockedMessage), v -> rs.spawnBlockedMessage = v);
        bind(BUILDER.comment("Beds only save a spawn point when the player sleeps in them.")
                .define("bedSpawnRequiresSleep", rs.bedSpawnRequiresSleep), v -> rs.bedSpawnRequiresSleep = v);
        list("stayInDimensions", v -> rs.stayInDimensions = v,
                "Players who die in these dimensions respawn in the same dimension.",
                "Spread out by the spawnRadius gamerule unless the player has a spawn point there.", "Use modid:dimension");
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private Config() {
    }

    private static <T> void bind(ForgeConfigSpec.ConfigValue<T> value, Consumer<T> setter) {
        BAKERS.add(() -> setter.accept(value.get()));
    }

    private static void list(String key, Consumer<List<String>> setter, String... comment) {
        bind(BUILDER.comment(comment).<String>defineListAllowEmpty(key, List.of(), o -> o instanceof String s && !s.isBlank()),
                v -> setter.accept(List.copyOf(v)));
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent.Loading event) {
        bake();
    }

    @SubscribeEvent
    static void onReload(ModConfigEvent.Reloading event) {
        bake();
    }

    private static void bake() {
        BAKERS.forEach(Runnable::run);
        SpawnConfig.bake();
    }
}
