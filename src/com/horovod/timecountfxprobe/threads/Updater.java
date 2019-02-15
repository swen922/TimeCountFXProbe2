package com.horovod.timecountfxprobe.threads;


import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import javafx.concurrent.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.*;

public class Updater {

    private static ExecutorService service = Executors.newFixedThreadPool(1);

    private ScheduledExecutorService repeatUpdater = Executors.newSingleThreadScheduledExecutor();
    //repeatUpdater.schedule(new Runnable() { ... }, 5, TimeUnit.SECONDS);


    public static ExecutorService getService() {
        return service;
    }

    public ScheduledExecutorService getRepeatUpdater() {
        return repeatUpdater;

    }

    public static void update(Task<WorkTime> task) {
        service.submit(task);
    }


}
