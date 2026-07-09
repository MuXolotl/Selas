package com.selas.mixin;

import com.selas.lighting.LightmapEngine;
import com.selas.lighting.TextureAccess;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.renderer.texture.DynamicTexture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts {@link DynamicTexture#upload()} to rewrite the lightmap texels right before they are
 * sent to the GPU. The hook is only enabled for the lightmap instance (see {@link MixinLightTexture}).
 */
@Mixin(DynamicTexture.class)
public class MixinDynamicTexture implements TextureAccess {
    @Shadow
    NativeImage pixels;

    private boolean selasHook = false;

    @Inject(method = "upload", at = @At("HEAD"))
    private void onUpload(CallbackInfo ci) {
        if (selasHook && LightmapEngine.enabled && pixels != null) {
            for (int b = 0; b < 16; b++) {
                for (int s = 0; s < 16; s++) {
                    int color = LightmapEngine.darken(pixels.getPixelRGBA(b, s), b, s);
                    pixels.setPixelRGBA(b, s, color);
                }
            }
        }
    }

    @Override
    public void selas_enableUploadHook() {
        selasHook = true;
    }
}
