package com.selas.lighting;

/**
 * Contract implemented by {@code MixinLightTexture} so the engine can read the lightmap state
 * without referencing Minecraft internals directly.
 */
public interface LightmapAccess {
    /** Whether the lightmap texture needs to be recomputed this frame. */
    boolean selas_isDirty();

    /** The previous block-light red flicker value, used by the engine for stable lighting. */
    float selas_prevFlicker();
}
