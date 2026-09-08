package com.sxilverr.spawnconditions.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class BlockMatcher {
    private static final BlockMatcher EMPTY = new BlockMatcher(List.of(), List.of());

    private final List<ResourceLocation> ids;
    private final List<TagKey<Block>> tags;
    private List<Block> resolved;

    private BlockMatcher(List<ResourceLocation> ids, List<TagKey<Block>> tags) {
        this.ids = ids;
        this.tags = tags;
    }

    public static BlockMatcher compile(List<? extends String> entries) {
        if (entries == null || entries.isEmpty()) {
            return EMPTY;
        }
        List<ResourceLocation> ids = new ArrayList<>();
        List<TagKey<Block>> tags = new ArrayList<>();
        for (String entry : entries) {
            if (entry == null) {
                continue;
            }
            String trimmed = entry.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            boolean tag = trimmed.charAt(0) == '#';
            ResourceLocation id = ResourceLocation.tryParse(tag ? trimmed.substring(1) : trimmed);
            if (id == null) {
                continue;
            }
            if (tag) {
                tags.add(TagKey.create(Registries.BLOCK, id));
            } else {
                ids.add(id);
            }
        }
        return ids.isEmpty() && tags.isEmpty() ? EMPTY : new BlockMatcher(List.copyOf(ids), List.copyOf(tags));
    }

    public boolean isEmpty() {
        return this.ids.isEmpty() && this.tags.isEmpty();
    }

    public boolean matches(BlockState state) {
        for (Block block : blocks()) {
            if (state.is(block)) {
                return true;
            }
        }
        for (TagKey<Block> tag : this.tags) {
            if (state.is(tag)) {
                return true;
            }
        }
        return false;
    }

    private List<Block> blocks() {
        List<Block> cached = this.resolved;
        if (cached == null) {
            List<Block> found = new ArrayList<>(this.ids.size());
            for (ResourceLocation id : this.ids) {
                BuiltInRegistries.BLOCK.getOptional(id).ifPresent(found::add);
            }
            cached = List.copyOf(found);
            this.resolved = cached;
        }
        return cached;
    }
}
