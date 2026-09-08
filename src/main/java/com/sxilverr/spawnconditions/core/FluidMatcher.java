package com.sxilverr.spawnconditions.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.List;

public final class FluidMatcher {
    private static final FluidMatcher EMPTY = new FluidMatcher(List.of(), List.of());

    private final List<ResourceLocation> ids;
    private final List<TagKey<Fluid>> tags;
    private List<Fluid> resolved;

    private FluidMatcher(List<ResourceLocation> ids, List<TagKey<Fluid>> tags) {
        this.ids = ids;
        this.tags = tags;
    }

    public static FluidMatcher compile(List<? extends String> entries) {
        if (entries == null || entries.isEmpty()) {
            return EMPTY;
        }
        List<ResourceLocation> ids = new ArrayList<>();
        List<TagKey<Fluid>> tags = new ArrayList<>();
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
                tags.add(TagKey.create(Registries.FLUID, id));
            } else {
                ids.add(id);
            }
        }
        return ids.isEmpty() && tags.isEmpty() ? EMPTY : new FluidMatcher(List.copyOf(ids), List.copyOf(tags));
    }

    public boolean isEmpty() {
        return this.ids.isEmpty() && this.tags.isEmpty();
    }

    public boolean matches(FluidState state) {
        for (Fluid fluid : fluids()) {
            if (state.is(fluid)) {
                return true;
            }
        }
        for (TagKey<Fluid> tag : this.tags) {
            if (state.is(tag)) {
                return true;
            }
        }
        return false;
    }

    private List<Fluid> fluids() {
        List<Fluid> cached = this.resolved;
        if (cached == null) {
            List<Fluid> found = new ArrayList<>(this.ids.size());
            for (ResourceLocation id : this.ids) {
                BuiltInRegistries.FLUID.getOptional(id).ifPresent(found::add);
            }
            cached = List.copyOf(found);
            this.resolved = cached;
        }
        return cached;
    }
}
