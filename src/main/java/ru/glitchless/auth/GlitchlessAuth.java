package ru.glitchless.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.glitchless.auth.config.GlitchlessGroupConfig;
import ru.glitchless.auth.loopers.MainLooper;
import ru.glitchless.auth.loopers.UpdateLooper;

@Mod(GlitchlessAuth.MODID)
public class GlitchlessAuth {
    public static final String MODID = "glitchlessauth";

    private static Logger logger = LogManager.getLogger("GlitchlessAuth");
    private static GlitchlessAuth INSTANCE;
    private static final Gson gson = new GsonBuilder().create();
    private static final OkHttpClient client = new OkHttpClient.Builder().build();

    private MainLooper mainLooper = new MainLooper();
    private UpdateLooper updateLooper = new UpdateLooper();

    public GlitchlessAuth() {
        this.INSTANCE = this;
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
                GlitchlessGroupConfig.INSTANCE_SPEC);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    public static Logger getLogger() {
        return logger;
    }

    @SubscribeEvent
    public void preInit(FMLServerStartingEvent event) {
        MinecraftForge.EVENT_BUS.register(mainLooper);
        updateLooper.start();
    }

    public static GlitchlessAuth getInstance() {
        return INSTANCE;
    }

    public MainLooper getMainLooper() {
        return mainLooper;
    }

    public static Gson getGson() {
        return gson;
    }

    public static OkHttpClient getClient() {
        return client;
    }

    public UpdateLooper getUpdateLooper() {
        return updateLooper;
    }
}
