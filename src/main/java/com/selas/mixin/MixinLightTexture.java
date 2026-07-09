package com.selas.mixin;

import com.selas.lighting.LightmapEngine;
import com.selas.lighting.config.Config;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Rewrites the lightmap texture just before it is uploaded to the GPU.
 *
 * <p>Hooking directly inside {@link LightTexture#updateLightTexture} (right before the
 * {@link net.minecraft.client.renderer.texture.DynamicTexture#upload()} call) guarantees we run
 * after vanilla has filled the 16x16 texture and before it leaves for the GPU, independent of
 * frame ordering elsewhere. We recompute the target luminance table and darken every texel when
 * the current dimension is configured for realistic darkness.
 *
 * <p>NOTE: a few diagnostic lines are printed on the first frames (prefixed "[Selas DBG]").
 * They will be removed before release.
 */
@Mixin(LightTexture.class)
public class MixinLightTexture {
    @Shadow
    private NativeImage lightPixels;
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private GameRenderer renderer;
    @Shadow
    private float blockLightRedFlicker;

    private static int dbg = 0;

    static {
        System.out.println("[Selas DBG] MixinLightTexture applied to LightTexture");
    }

    @Inject(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"))
    private void onUpload(CallbackInfo ci) {
        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        LightmapEngine.updateLuminance(minecraft, renderer, partialTick, blockLightRedFlicker);

        // Read the vanilla (pre-darken) values BEFORE we modify the texture.
        int before00 = (lightPixels != null) ? lightPixels.getPixelRGBA(0, 0) : -1;
        int before88 = (lightPixels != null) ? lightPixels.getPixelRGBA(8, 8) : -1;
        int before1515 = (lightPixels != null) ? lightPixels.getPixelRGBA(15, 15) : -1;

        // Brute-force proof mode: zero the whole lightmap to confirm the data reaches the GPU.
        if (Boolean.getBoolean("selas.bruteforce") && lightPixels != null) {
            for (int b = 0; b < 16; b++) {
                for (int s = 0; s < 16; s++) {
                    lightPixels.setPixelRGBA(b, s, 0xFF000000);
                }
            }
        } else if (LightmapEngine.enabled && lightPixels != null) {
            for (int b = 0; b < 16; b++) {
                for (int s = 0; s < 16; s++) {
                    lightPixels.setPixelRGBA(b, s, LightmapEngine.darken(lightPixels.getPixelRGBA(b, s), b, s));
                }
            }
        }

        if (dbg < 5) {
            int after00 = (lightPixels != null) ? lightPixels.getPixelRGBA(0, 0) : -1;
            int after88 = (lightPixels != null) ? lightPixels.getPixelRGBA(8, 8) : -1;
            int after1515 = (lightPixels != null) ? lightPixels.getPixelRGBA(15, 15) : -1;
            String dim = (minecraft.level != null) ? minecraft.level.dimension().location().toString() : "null";
            System.out.println("[Selas DBG] #" + dbg
                    + " enabled=" + LightmapEngine.enabled
                    + " darkOverworld=" + Config.DARK_OVERWORLD.get()
                    + " dim=" + dim
                    + " before(0,0)=" + before00 + " after(0,0)=" + after00 + (before00 != after00 ? " CHANGED" : "")
                    + " before(8,8)=" + before88 + " after(8,8)=" + after88 + (before88 != after88 ? " CHANGED" : "")
                    + " before(15,15)=" + before1515 + " after(15,15)=" + after1515 + (before1515 != after1515 ? " CHANGED" : ""));
            dbg++;
        }
    }
}
