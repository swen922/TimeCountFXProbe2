package com.horovod.timecountfxprobe.serialize;


import com.horovod.timecountfxprobe.project.AllData;
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


    //private static ExecutorService service = Executors.newFixedThreadPool(1);
    private static ExecutorService service = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, AllData.tasksQueue);

    // TODO не запускается повторное выполнение, надо попробовать запустить этот эзекутор в отдельной нитке

    //private static ScheduledExecutorService repeatUpdater = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledExecutorService repeatUpdater = Executors.newScheduledThreadPool(5);

    //repeatUpdater.schedule(new Runnable() { ... }, 5, TimeUnit.SECONDS);


    public static ExecutorService getService() {
        return service;
    }

    public static ScheduledExecutorService getRepeatUpdater() {
        return repeatUpdater;

    }

    public static void update(UpdateType updateType, Object object) {

        SerializeWrapper wrapper = new SerializeWrapper(updateType, object);

        Task task = new ThreadUpdate(wrapper);
        service.submit(task);
    }

    public static void startSheduledUpdate() {

    }

}