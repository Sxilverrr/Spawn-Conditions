package com.sxilverr.spawnconditions.core;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public final class BiomeMatcher {
    private static final BiomeMatcher EMPTY = new BiomeMatcher(List.of(), List.of());

    private final List<ResourceKey<Biome>> keys;
    private final List<TagKey<Biome>> tags;

    private BiomeMatcher(List<ResourceKey<Biome>> keys, List<TagKey<Biome>> tags) {
        this.keys = keys;
        this.tags = tags;
    }

    public static BiomeMatcher compile(List<? extends String> entries) {
        if (entries == null || entries.isEmpty()) {
            return EMPTY;
        }
        List<ResourceKey<Biome>> keys = new ArrayList<>();
        List<TagKey<Biome>> tags = new ArrayList<>();
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
                tags.add(TagKey.create(Registries.BIOME, id));
            } else {
                keys.add(ResourceKey.create(Registries.BIOME, id));
            }
        }
        return keys.isEmpty() && tags.isEmpty() ? EMPTY : new BiomeMatcher(List.copyOf(keys), List.copyOf(tags));
    }

    public boolean isEmpty() {
        return this.keys.isEmpty() && this.tags.isEmpty();
    }

    public boolean matches(Holder<Biome> biome) {
        for (ResourceKey<Biome> key : this.keys) {
            if (biome.is(key)) {
                return true;
            }
        }
        for (TagKey<Biome> tag : this.tags) {
            if (biome.is(tag)) {
                return true;
            }
        }
        return false;
    }
}
