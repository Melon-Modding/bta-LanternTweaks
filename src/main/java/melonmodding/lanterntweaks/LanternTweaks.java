package melonmodding.lanterntweaks;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LanternTweaks implements ModInitializer {
    public static final String MOD_ID = "lanterntweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        LOGGER.info("LanternTweaks initialized.");
    }


}
