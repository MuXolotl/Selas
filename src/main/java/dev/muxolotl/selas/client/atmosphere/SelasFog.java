package dev.muxolotl.selas.client.atmosphere;

import dev.muxolotl.selas.Selas;
import dev.muxolotl.selas.client.compat.ShaderPackCompat;
import dev.muxolotl.selas.config.SelasClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = Selas.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class SelasFog {
    private SelasFog() {
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        ClientLevel level = resolveActiveLevel();
        if (level == null) {
            return;
        }

        // Scaffold only: no color changes yet. Fog color modification arrives in a later commit.
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getType() != FogType.NONE) {
            return;
        }

        ClientLevel level = resolveActiveLevel();
        if (level == null) {
            return;
        }

        // Scaffold only: no distance changes yet. Fog density modification arrives in a later commit.
        // Distance changes require event.setCanceled(true) to take effect.
    }

    private static ClientLevel resolveActiveLevel() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !isEnabled()) {
            return null;
        }

        if (SelasClientConfig.ATMOSPHERE_DISABLE_WITH_SHADERS.getAsBoolean() && ShaderPackCompat.isShaderPackInUse()) {
            return null;
        }

        return shouldAffectDimension(level) ? level : null;
    }

    private static boolean isEnabled() {
        return SelasClientConfig.ATMOSPHERE_ENABLED.getAsBoolean();
    }

    private static boolean shouldAffectDimension(ClientLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        if (dimension.equals(Level.OVERWORLD)) {
            return SelasClientConfig.ATMOSPHERE_AFFECT_OVERWORLD.getAsBoolean();
        }
        if (dimension.equals(Level.NETHER)) {
            return SelasClientConfig.ATMOSPHERE_AFFECT_NETHER.getAsBoolean();
        }
        if (dimension.equals(Level.END)) {
            return SelasClientConfig.ATMOSPHERE_AFFECT_END.getAsBoolean();
        }
        return false;
    }
}
