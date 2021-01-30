package ru.glitchless.auth.loopers;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber
public class MainLooper {
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.ServerTickEvent event) {
        while (!queue.isEmpty()) {
            queue.poll().run();
        }
    }

    public void handle(Runnable runnable) {
        queue.add(runnable);
    }
}
