package com.sxilverr.spawnconditions.core;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record IdMatcher<T>(Set<ResourceLocation> ids, List<TagKey<T>> tags) {
    public static <T> IdMatcher<T> of(ResourceKey<? extends Registry<T>> registry, List<String> entries) {
        Set<ResourceLocation> ids = new HashSet<>();
        List<TagKey<T>> tags = new ArrayList<>();
        for (String entry : entries == null ? List.<String>of() : entries) {
            if (entry == null || entry.isBlank()) {
                continue;
            }
            String trimmed = entry.trim();
            boolean tag = trimmed.startsWith("#");
            ResourceLocation id = ResourceLocation.tryParse(tag ? trimmed.substring(1) : trimmed);
            if (id == null) {
                continue;
            }
            if (tag) {
                tags.add(TagKey.create(registry, id));
            } else {
                ids.add(id);
            }
        }
        return new IdMatcher<>(ids, tags);
    }

    public boolean isEmpty() {
        return this.ids.isEmpty() && this.tags.isEmpty();
    }

    public boolean matches(ResourceKey<T> key) {
        return this.ids.contains(key.location());
    }

    public boolean matches(Holder<T> holder) {
        return this.ids.stream().anyMatch(holder::is) || this.tags.stream().anyMatch(holder::is);
    }
}
