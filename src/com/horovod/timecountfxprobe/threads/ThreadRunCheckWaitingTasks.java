package com.horovod.timecountfxprobe.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadRunCheckWaitingTasks implements Runnable {

    @Override
    public void run() {
       ScheduledExecutorService repeatUpdater = Executors.newSingleThreadScheduledExecutor();

        ThreadCheckWaitingTasks task = new ThreadCheckWaitingTasks();
        /*Thread thread = new Thread(task);
        thread.setDaemon(true);*/
        repeatUpdater.scheduleWithFixedDelay(task, 10, 20, TimeUnit.SECONDS);

    }
}
