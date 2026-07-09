package com.selas.mixin;

import com.selas.lighting.LightmapAccess;
import com.selas.lighting.TextureAccess;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the {@link LightTexture} so we know when the lightmap is dirty and can enable the upload
 * hook on the underlying {@link DynamicTexture}.
 */
@Mixin(LightTexture.class)
public class MixinLightTexture implements LightmapAccess {
    @Shadow
    private DynamicTexture lightTexture;
    @Shadow
    private float blockLightRedFlicker;
    @Shadow
    private boolean updateLightTexture;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void afterInit(GameRenderer gameRenderer, Minecraft minecraftClient, CallbackInfo ci) {
        ((TextureAccess) lightTexture).selas_enableUploadHook();
    }

    @Override
    public float selas_prevFlicker() {
        return blockLightRedFlicker;
    }

    @Override
    public boolean selas_isDirty() {
        return updateLightTexture;
    }
}
