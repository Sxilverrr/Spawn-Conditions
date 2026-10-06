package com.sxilverr.spawnconditions.config;

import com.sxilverr.spawnconditions.core.IdMatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

public final class SpawnConfig {
    public static final String MOD_ID = "spawnconditions";

    public static boolean enabled = true;
    public static WorldSpawn worldSpawn = new WorldSpawn();
    public static Placement placement = new Placement();
    public static Respawn respawn = new Respawn();

    static {
        bake();
    }

    private SpawnConfig() {
    }

    public static void bake() {
        worldSpawn.bake();
        placement.bake();
        respawn.bake();
    }

    public enum LiquidLanding {
        SURFACE,
        FLOOR
    }

    public enum GroundRequirement {
        FULL_FACE,
        ANY_COLLISION,
        ANY_BLOCK
    }

    public enum WorldSpawnArea {
        RING,
        BOX
    }

    public static final class WorldSpawn {
        public boolean randomize = false;
        public WorldSpawnArea area = WorldSpawnArea.RING;
        public int centerX = 0;
        public int centerZ = 0;
        public int minRadius = 0;
        public int maxRadius = 10000;
        public int minX = -10000;
        public int maxX = 10000;
        public int minZ = -10000;
        public int maxZ = 10000;
        public int attempts = 32;

        private void bake() {
            if (this.area == null) {
                this.area = WorldSpawnArea.RING;
            }
            int radius = this.minRadius;
            this.minRadius = Math.max(0, Math.min(radius, this.maxRadius));
            this.maxRadius = Math.max(0, Math.max(radius, this.maxRadius));
            int x = this.minX;
            this.minX = Math.min(x, this.maxX);
            this.maxX = Math.max(x, this.maxX);
            int z = this.minZ;
            this.minZ = Math.min(z, this.maxZ);
            this.maxZ = Math.max(z, this.maxZ);
            this.attempts = Math.max(1, this.attempts);
        }
    }

    public static final class Placement {
        public boolean fluidBlocksColumn = true;
        public LiquidLanding liquidLanding = LiquidLanding.SURFACE;
        public GroundRequirement groundRequirement = GroundRequirement.FULL_FACE;
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
        public List<String> biomeAllowList = List.of();
        public List<String> biomeDenyList = List.of();
        public List<String> blockAllowList = List.of();
        public List<String> blockDenyList = List.of();
        public List<String> fluidAllowList = List.of();
        public List<String> fluidDenyList = List.of();

        private transient IdMatcher<Biome> biomeAllow;
        private transient IdMatcher<Biome> biomeDeny;
        private transient IdMatcher<Block> blockAllow;
        private transient IdMatcher<Block> blockDeny;
        private transient IdMatcher<Fluid> fluidAllow;
        private transient IdMatcher<Fluid> fluidDeny;

        private void bake() {
            if (this.liquidLanding == null) {
                this.liquidLanding = LiquidLanding.SURFACE;
            }
            if (this.groundRequirement == null) {
                this.groundRequirement = GroundRequirement.FULL_FACE;
            }
            this.requiredHeadroom = Math.max(0, this.requiredHeadroom);
            this.maxSearchDepth = Math.max(0, this.maxSearchDepth);
            int y = this.minY;
            this.minY = Math.min(y, this.maxY);
            this.maxY = Math.max(y, this.maxY);
            int x = this.minX;
            this.minX = Math.min(x, this.maxX);
            this.maxX = Math.max(x, this.maxX);
            int z = this.minZ;
            this.minZ = Math.min(z, this.maxZ);
            this.maxZ = Math.max(z, this.maxZ);
            this.biomeAllow = IdMatcher.of(Registries.BIOME, this.biomeAllowList);
            this.biomeDeny = IdMatcher.of(Registries.BIOME, this.biomeDenyList);
            this.blockAllow = IdMatcher.of(Registries.BLOCK, this.blockAllowList);
            this.blockDeny = IdMatcher.of(Registries.BLOCK, this.blockDenyList);
            this.fluidAllow = IdMatcher.of(Registries.FLUID, this.fluidAllowList);
            this.fluidDeny = IdMatcher.of(Registries.FLUID, this.fluidDenyList);
        }

        public boolean withinHorizontalBounds(int x, int z) {
            return !this.limitHorizontally || (x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ);
        }

        public boolean fluidAllowed(FluidState state) {
            return !this.fluidDeny.matches(state.holder()) && this.fluidAllow.matches(state.holder());
        }

        public boolean biomeAllowed(Holder<Biome> biome) {
            return !this.biomeDeny.matches(biome) && (this.biomeAllow.isEmpty() || this.biomeAllow.matches(biome));
        }

        public boolean blockAllowed(BlockState state) {
            Holder<Block> block = state.getBlockHolder();
            return !this.blockDeny.matches(block) && (this.blockAllow.isEmpty() || this.blockAllow.matches(block));
        }
    }

    public static final class Respawn {
        public boolean preventSpawnSetting = false;
        public List<String> preventSpawnSettingDimensions = List.of();
        public String spawnBlockedMessage = "&cYou cannot set your spawn.";
        public boolean bedSpawnRequiresSleep = false;
        public List<String> stayInDimensions = List.of();

        private transient String message;
        private transient IdMatcher<Level> blockedDimensions;
        private transient IdMatcher<Level> stayDimensions;

        private void bake() {
            this.message = this.spawnBlockedMessage == null ? ""
                    : this.spawnBlockedMessage.replaceAll("(?i)&(?=[0-9a-fk-or])", String.valueOf(ChatFormatting.PREFIX_CODE));
            this.blockedDimensions = IdMatcher.of(Registries.DIMENSION, this.preventSpawnSettingDimensions);
            this.stayDimensions = IdMatcher.of(Registries.DIMENSION, this.stayInDimensions);
        }

        public String message() {
            return this.message;
        }

        public boolean blocksSpawnSetting(ResourceKey<Level> dimension) {
            return this.preventSpawnSetting || this.blockedDimensions.matches(dimension);
        }

        public boolean staysIn(ResourceKey<Level> dimension) {
            return this.stayDimensions.matches(dimension);
        }
    }
}
