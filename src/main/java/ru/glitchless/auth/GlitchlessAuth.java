package ru.glitchless.auth;

import com.feed_the_beast.ftblib.FTBLibCommon;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = GlitchlessAuth.MODID, name = GlitchlessAuth.NAME, version = GlitchlessAuth.VERSION,
        acceptableRemoteVersions = "*")
public class GlitchlessAuth {
    public static final String MODID = "glitchlessauth";
    public static final String NAME = "Glitchless Auth Control";
    public static final String VERSION = "1.0";

    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code

    }
}
