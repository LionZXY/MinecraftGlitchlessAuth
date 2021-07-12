package ru.glitchless.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.Logger;
import ru.glitchless.auth.utils.ReplaceServerHelper;

@Mod(
        modid = GlitchlessAuth.MODID,
        name = GlitchlessAuth.NAME,
        version = "${version}",
        acceptableRemoteVersions = "*"
)
public class GlitchlessAuth {
    public static final String MODID = "glitchlessauth";
    public static final String NAME = "Glitchless Auth Control";

    private static Logger logger;
    private static GlitchlessAuth INSTANCE;
    private static final Gson gson = new GsonBuilder().create();
    private static final OkHttpClient client = new OkHttpClient.Builder().build();

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
        try {
            new ReplaceServerHelper().replaceAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("I can't replace auth urls", ex);
        }
    }

    public static GlitchlessAuth getInstance() {
        return INSTANCE;
    }

    public static Gson getGson() {
        return gson;
    }

    public static OkHttpClient getClient() {
        return client;
    }
}
