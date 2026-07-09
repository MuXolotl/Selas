package com.selas.lighting;

/**
 * Contract implemented by {@code MixinDynamicTexture} so only the lightmap's {@code DynamicTexture}
 * instance gets its upload hook enabled.
 */
public interface TextureAccess {
    void selas_enableUploadHook();
}
