package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.serialize.SerializeWrapper;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import javafx.concurrent.Task;

import java.util.Queue;
import java.util.concurrent.*;

public class Updater {
    private static ExecutorService service = Executors.newFixedThreadPool(3);
    private static ScheduledExecutorService repeatUpdater = Executors.newSingleThreadScheduledExecutor();
    //repeatUpdater.schedule(new Runnable() { ... }, 5, TimeUnit.SECONDS);


    public static ExecutorService getService() {
        return service;
    }

    public ScheduledExecutorService getRepeatUpdater() {
        return repeatUpdater;

    }

    public void update(SerializeWrapper serializeWrapper) {
        
    }


}
