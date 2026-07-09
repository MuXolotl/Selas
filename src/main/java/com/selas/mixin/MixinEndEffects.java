package com.selas.mixin;

import com.selas.lighting.LightmapEngine;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Darkens the End's brightness-dependent fog color per config.
 */
@Mixin(DimensionSpecialEffects.EndEffects.class)
public class MixinEndEffects {
    private static final double MIN = 0.029999999329447746D;

    @Inject(method = "getBrightnessDependentFogColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void onAdjustFogColor(Vec3 fogColor, float daylight, CallbackInfoReturnable<Vec3> ci) {
        double factor = LightmapEngine.darkEndFog();
        if (factor != 1.0) {
            Vec3 result = ci.getReturnValue();
            ci.setReturnValue(new Vec3(
                    Math.max(MIN, result.x * factor),
                    Math.max(MIN, result.y * factor),
                    Math.max(MIN, result.z * factor)));
        }
    }
}
