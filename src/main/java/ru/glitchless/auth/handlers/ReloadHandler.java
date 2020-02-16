package ru.glitchless.auth.handlers;

import com.feed_the_beast.ftblib.events.FTBLibPreInitRegistryEvent;
import com.feed_the_beast.ftblib.events.IReloadHandler;
import com.feed_the_beast.ftblib.events.ServerReloadEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.glitchless.auth.GlitchlessAuth;

@Mod.EventBusSubscriber
public class ReloadHandler {

    @SubscribeEvent
    public static void onFTBLibPreInitRegistry(FTBLibPreInitRegistryEvent event) {
        event.getRegistry().registerServerReloadHandler(new ResourceLocation(GlitchlessAuth.MODID, "gl_map"), new IReloadHandler() {
            @Override
            public boolean onReload(ServerReloadEvent serverReloadEvent) throws Exception {
                ConfigManager.sync(GlitchlessAuth.MODID, Config.Type.INSTANCE);
                return true;
            }
        });

        event.getRegistry().registerServerReloadHandler(new ResourceLocation(GlitchlessAuth.MODID, "gl_wl"), new IReloadHandler() {
            @Override
            public boolean onReload(ServerReloadEvent serverReloadEvent) throws Exception {
                GlitchlessAuth.getInstance().getUpdateLooper().forceUpdate();
                return true;
            }
        });
    }
}
