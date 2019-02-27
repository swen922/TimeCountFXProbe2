package com.horovod.timecountfxprobe.serialize;


import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.threads.ThreadGetProjectID;
import com.horovod.timecountfxprobe.threads.ThreadUpdate;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.concurrent.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.*;

public class Updater {

    private static ExecutorService service = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, AllData.tasksQueue);
    //private static ExecutorService service = Executors.newFixedThreadPool(1);

    private static ScheduledExecutorService repeatUpdater = Executors.newSingleThreadScheduledExecutor();
    //private static ScheduledExecutorService repeatUpdater = Executors.newScheduledThreadPool(5);

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

    public static Integer getProjectID() {
        Future<Integer> resultFuture = service.submit(new ThreadGetProjectID());
        Integer result = null;
        try {
            result = resultFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            AllData.status = "Ошибка получения нового ID-номера проекта. Выброшено исключение InterruptedException";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            AllData.status = "Ошибка получения нового ID-номера проекта. Выброшено исключение ExecutionException";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return result;
    }

}