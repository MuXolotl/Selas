package dev.muxolotl.selas;

import dev.muxolotl.selas.config.SelasClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Selas.MODID, dist = Dist.CLIENT)
public final class SelasClient {
    public SelasClient(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SelasClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        Selas.LOGGER.info("Selas client initialized.");
    }
}
