package com.selas;

import com.selas.lighting.config.Config;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * Selas — realistic lighting for Minecraft.
 *
 * <p>This is the common (both-side) entry point. All rendering logic lives in the client-only
 * {@link SelasClient} class and the {@code com.selas.lighting}/{@code com.selas.mixin} packages.
 * The mod is effectively client-side: it only rewrites the lightmap texture and dimension fog.
 */
@Mod(Selas.MODID)
public class Selas {
    public static final String MODID = "selas";
    public static final String MODNAME = "Selas";

    public Selas(IEventBus modEventBus, ModContainer modContainer) {
        // Rendering config is client-side only.
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }
}
