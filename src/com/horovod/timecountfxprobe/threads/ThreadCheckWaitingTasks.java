package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.serialize.SerializeWrapper;
import com.horovod.timecountfxprobe.serialize.Updater;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.concurrent.Callable;

public class ThreadCheckWaitingTasks implements Runnable {

    @Override
    public void run() {

        System.out.println("starting ThreadCheckWaitingTasks...");

        try {
            if (!AllData.waitingTasks.isEmpty()) {

                AllData.status = ThreadCheckWaitingTasks.class.getSimpleName() + " - Список неисполненных обновлений базы составляет " + AllData.waitingTasks.size() + " задач. Активирую неисполненные обновления.";
                AllData.updateAllStatus();
                AllData.logger.info(AllData.status);

                int counter = 0;

                for (int i = 0; i < AllData.waitingTasks.size(); i++) {
                    SerializeWrapper wrapper = AllData.waitingTasks.poll();
                    if (wrapper != null) {
                        Updater.update(wrapper.getUpdateType(), wrapper);
                        counter++;
                    }
                }

                Loader loader = new Loader();
                loader.saveWatingTasks();

                AllData.status = ThreadCheckWaitingTasks.class.getSimpleName() + " - активировано " + counter + " задач из списка неисполненных обновлений базы.";
                AllData.updateAllStatus();
                AllData.logger.info(AllData.status);

                System.out.println("waiting tasks OK");

            }
        } catch (Exception e) {
            e.printStackTrace();
            AllData.status = ThreadCheckWaitingTasks.class.getSimpleName() + " - Не удалось заново запустить невыполненные задачи: выброшено исключение.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
    }
}
