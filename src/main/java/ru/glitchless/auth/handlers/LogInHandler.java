package ru.glitchless.auth.handlers;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.glitchless.auth.GlitchlessAuth;
import ru.glitchless.auth.tasks.CheckUserTask;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Mod.EventBusSubscriber
public class LogInHandler {
    private static Executor executor = Executors.newCachedThreadPool();

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        executor.execute(new CheckUserTask((ServerPlayerEntity) event.getPlayer()));
        GlitchlessAuth.getInstance().getUpdateLooper().forceUpdate();
    }
}
