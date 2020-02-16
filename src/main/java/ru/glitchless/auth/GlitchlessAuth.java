package ru.glitchless.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.Logger;
import ru.glitchless.auth.loopers.MainLooper;
import ru.glitchless.auth.loopers.UpdateLooper;

@Mod(modid = GlitchlessAuth.MODID, name = GlitchlessAuth.NAME, version = GlitchlessAuth.VERSION,
        acceptableRemoteVersions = "*")
public class GlitchlessAuth {
    public static final String MODID = "glitchlessauth";
    public static final String NAME = "Glitchless Auth Control";
    public static final String VERSION = "1.0";

    private static Logger logger;
    private static GlitchlessAuth INSTANCE;
    private static final Gson gson = new GsonBuilder().create();
    private static final OkHttpClient client = new OkHttpClient.Builder().build();

    private MainLooper mainLooper = new MainLooper();
    private UpdateLooper updateLooper = new UpdateLooper();

    public GlitchlessAuth() {
        this.INSTANCE = this;
    }

    public static Logger getLogger() {
        return logger;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(mainLooper);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
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
