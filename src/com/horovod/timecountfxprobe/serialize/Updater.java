package com.horovod.timecountfxprobe.serialize;


import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.threads.ThreadUpdate;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.concurrent.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.*;

public class Updater {

    public static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    //private static ExecutorService service = Executors.newFixedThreadPool(1);
    private static ExecutorService service = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, queue);

    private ScheduledExecutorService repeatUpdater = Executors.newSingleThreadScheduledExecutor();
    //repeatUpdater.schedule(new Runnable() { ... }, 5, TimeUnit.SECONDS);


    public static ExecutorService getService() {
        return service;
    }

    public ScheduledExecutorService getRepeatUpdater() {
        return repeatUpdater;

    }

    public static void update(UpdateType updateType, Object object) {

        System.out.println("inside Updater.update");

        SerializeWrapper wrapper = new SerializeWrapper(updateType, object);

        System.out.println(wrapper);

        Task task = new ThreadUpdate(wrapper);
        service.submit(task);
        //queue.add(task);


    }

}