package com.selas.mixin;

import com.selas.lighting.LightmapEngine;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Darkens the Nether's brightness-dependent fog color per config.
 */
@Mixin(DimensionSpecialEffects.NetherEffects.class)
public class MixinNetherEffects {
    private static final double MIN = 0.029999999329447746D;

    @Inject(method = "getBrightnessDependentFogColor", at = @At("RETURN"), cancellable = true)
    private void onAdjustFogColor(CallbackInfoReturnable<Vec3> ci) {
        double factor = LightmapEngine.darkNetherFog();
        if (factor != 1.0) {
            Vec3 result = ci.getReturnValue();
            ci.setReturnValue(new Vec3(
                    Math.max(MIN, result.x * factor),
                    Math.max(MIN, result.y * factor),
                    Math.max(MIN, result.z * factor)));
        }
    }
}
