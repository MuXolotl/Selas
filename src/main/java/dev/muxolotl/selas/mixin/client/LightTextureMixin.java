package dev.muxolotl.selas.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import dev.muxolotl.selas.client.lighting.SelasLightmap;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {
    @Shadow
    private NativeImage lightPixels;

    @Shadow
    private boolean updateLightTexture;

    @Inject(method = "updateLightTexture", at = @At("HEAD"))
    private void selas$forceSmoothLightmapUpdates(float partialTick, CallbackInfo ci) {
        if (SelasLightmap.shouldUpdateEveryFrame()) {
            this.updateLightTexture = true;
        }
    }

    @Inject(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"))
    private void selas$applyNaturalDarkness(float partialTick, CallbackInfo ci) {
        float selasPartialTick = SelasLightmap.shouldUpdateEveryFrame() ? partialTick : 0.0F;
        SelasLightmap.transform(this.lightPixels, selasPartialTick);
    }
}
