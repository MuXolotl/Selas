package dev.muxolotl.selas;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Selas.MODID)
public final class Selas {
    public static final String MODID = "selas";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Selas() {
        LOGGER.info("Selas initialized.");
    }
}
