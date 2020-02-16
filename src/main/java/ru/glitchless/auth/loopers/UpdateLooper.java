package ru.glitchless.auth.loopers;

import ru.glitchless.auth.GlitchlessAuth;
import ru.glitchless.auth.tasks.UpdateWhiteListTask;

import java.util.concurrent.TimeUnit;

public class UpdateLooper extends Thread {
    private UpdateWhiteListTask updateWhiteListTask = new UpdateWhiteListTask();
    private long updateWhiteListDelay = TimeUnit.SECONDS.convert(30, TimeUnit.MILLISECONDS);
    private final Object lockWait = new Object();

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                updateWhiteListTask.run();
                synchronized (lockWait) {
                    lockWait.wait(updateWhiteListDelay);
                }
            } catch (Exception ex) {
                GlitchlessAuth.getLogger().error("Exception in UpdateLooper", ex);
            }
        }
    }

    public void forceUpdate() {
        synchronized (lockWait) {
            lockWait.notifyAll();
        }
    }
}
