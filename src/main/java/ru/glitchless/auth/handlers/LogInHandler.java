package ru.glitchless.auth.handlers;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import ru.glitchless.auth.GlitchlessAuth;
import ru.glitchless.auth.tasks.CheckUserTask;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Mod.EventBusSubscriber
public class LogInHandler {
    private static Executor executor = Executors.newCachedThreadPool();

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        executor.execute(new CheckUserTask(event.player));
        GlitchlessAuth.getInstance().getUpdateLooper().forceUpdate();
    }
}
