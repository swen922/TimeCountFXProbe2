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
            Updater.getRepeatWaitingTaskService().scheduleAtFixedRate(task, 20, 30, TimeUnit.SECONDS);

            ThreadGlobalUpdate globalUpdate = new ThreadGlobalUpdate();
            Updater.getGlobalUpdateTaskService().scheduleAtFixedRate(globalUpdate, 30, 120, TimeUnit.SECONDS);

            return true;
        } catch (Exception e) {
            AllData.updateAllStatus("ThreadStartCheckingWaitingTasks - Не удалось запустить регулярную обработку списка неисполненных обновлений базы.");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return false;
    }
}
