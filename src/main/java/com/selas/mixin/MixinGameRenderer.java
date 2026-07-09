package com.selas.mixin;

import com.selas.lighting.LightmapEngine;
import com.selas.lighting.LightmapAccess;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * On each frame, if the lightmap is dirty, recompute the target luminance table before it uploads.
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private LightTexture lightTexture;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void onRenderLevel(float tickDelta, long nanos, PoseStack poseStack, CallbackInfo ci) {
        LightmapAccess lightmap = (LightmapAccess) lightTexture;
        if (lightmap.selas_isDirty()) {
            LightmapEngine.updateLuminance(minecraft, (GameRenderer) (Object) this, tickDelta, lightmap.selas_prevFlicker());
        }
    }
}
