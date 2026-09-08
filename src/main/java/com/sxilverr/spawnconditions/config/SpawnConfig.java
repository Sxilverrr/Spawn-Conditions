package com.sxilverr.spawnconditions.config;

import com.sxilverr.spawnconditions.core.BiomeMatcher;
import com.sxilverr.spawnconditions.core.BlockMatcher;
import com.sxilverr.spawnconditions.core.FluidMatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

public final class SpawnConfig {
    public static boolean enabled = true;
    public static final WorldSpawn worldSpawn = new WorldSpawn();
    public static final Placement placement = new Placement();
    public static final Respawn respawn = new Respawn();

    private SpawnConfig() {
    }

    private static final String FORMAT_CODES = "0123456789abcdefklmnorABCDEFKLMNOR";

    public static String translateFormatCodes(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        char[] chars = raw.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && FORMAT_CODES.indexOf(chars[i + 1]) >= 0) {
                chars[i] = ChatFormatting.PREFIX_CODE;
                i++;
            }
        }
        return new String(chars);
    }

    public static <T extends Enum<T>> T parseEnum(String raw, T[] values, T fallback) {
        if (raw == null) {
            return fallback;
        }
        String trimmed = raw.trim();
        for (T candidate : values) {
            if (candidate.name().equalsIgnoreCase(trimmed)) {
                return candidate;
            }
        }
        return fallback;
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

        public void normalise() {
            if (this.minRadius > this.maxRadius) {
                int swap = this.minRadius;
                this.minRadius = this.maxRadius;
                this.maxRadius = swap;
            }
            if (this.minX > this.maxX) {
                int swap = this.minX;
                this.minX = this.maxX;
                this.maxX = swap;
            }
            if (this.minZ > this.maxZ) {
                int swap = this.minZ;
                this.minZ = this.maxZ;
                this.maxZ = swap;
            }
            this.minRadius = Math.max(0, this.minRadius);
            this.maxRadius = Math.max(0, this.maxRadius);
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

        private BiomeMatcher biomeAllowMatcher = BiomeMatcher.compile(List.of());
        private BiomeMatcher biomeDenyMatcher = BiomeMatcher.compile(List.of());
        private BlockMatcher blockAllowMatcher = BlockMatcher.compile(List.of());
        private BlockMatcher blockDenyMatcher = BlockMatcher.compile(List.of());
        private FluidMatcher fluidAllowMatcher = FluidMatcher.compile(List.of());
        private FluidMatcher fluidDenyMatcher = FluidMatcher.compile(List.of());

        public void normalise() {
            if (this.minY > this.maxY) {
                int swap = this.minY;
                this.minY = this.maxY;
                this.maxY = swap;
            }
            if (this.minX > this.maxX) {
                int swap = this.minX;
                this.minX = this.maxX;
                this.maxX = swap;
            }
            if (this.minZ > this.maxZ) {
                int swap = this.minZ;
                this.minZ = this.maxZ;
                this.maxZ = swap;
            }
            this.requiredHeadroom = Math.max(0, this.requiredHeadroom);
            this.maxSearchDepth = Math.max(0, this.maxSearchDepth);
        }

        public void setBiomeLists(List<? extends String> allow, List<? extends String> deny) {
            this.biomeAllowMatcher = BiomeMatcher.compile(allow);
            this.biomeDenyMatcher = BiomeMatcher.compile(deny);
        }

        public void setBlockLists(List<? extends String> allow, List<? extends String> deny) {
            this.blockAllowMatcher = BlockMatcher.compile(allow);
            this.blockDenyMatcher = BlockMatcher.compile(deny);
        }

        public void setFluidLists(List<? extends String> allow, List<? extends String> deny) {
            this.fluidAllowMatcher = FluidMatcher.compile(allow);
            this.fluidDenyMatcher = FluidMatcher.compile(deny);
        }

        public boolean fluidAllowed(FluidState state) {
            if (this.fluidDenyMatcher.matches(state)) {
                return false;
            }
            return !this.fluidAllowMatcher.isEmpty() && this.fluidAllowMatcher.matches(state);
        }

        public boolean withinHorizontalBounds(int x, int z) {
            if (!this.limitHorizontally) {
                return true;
            }
            return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ;
        }

        public boolean biomeAllowed(Holder<Biome> biome) {
            if (this.biomeDenyMatcher.matches(biome)) {
                return false;
            }
            return this.biomeAllowMatcher.isEmpty() || this.biomeAllowMatcher.matches(biome);
        }

        public boolean blockAllowed(BlockState state) {
            if (this.blockDenyMatcher.matches(state)) {
                return false;
            }
            return this.blockAllowMatcher.isEmpty() || this.blockAllowMatcher.matches(state);
        }
    }

    public static final class Respawn {
        public boolean preventSpawnSetting = false;
        public String spawnBlockedMessage = "&cYou cannot set your spawn.";

        private transient String resolvedMessage = "";

        public void normalise() {
            this.resolvedMessage = translateFormatCodes(this.spawnBlockedMessage);
        }

        public String message() {
            return this.resolvedMessage;
        }
    }
}
