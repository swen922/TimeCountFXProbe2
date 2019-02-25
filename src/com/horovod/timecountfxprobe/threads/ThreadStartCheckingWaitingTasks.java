package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Updater;
import javafx.concurrent.Task;

import java.util.concurrent.TimeUnit;

public class ThreadStartCheckingWaitingTasks extends Task<Boolean> {

    @Override
    protected Boolean call() throws Exception {

        try {
            ThreadCheckWaitingTasks task = new ThreadCheckWaitingTasks();
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            Updater.getRepeatUpdater().scheduleWithFixedDelay(thread, 10, 120, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            AllData.status = ThreadStartCheckingWaitingTasks.class.getSimpleName() + " - Не удалось запустить регулярную обработку списка неисполненных обновлений базы.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return false;
    }
}