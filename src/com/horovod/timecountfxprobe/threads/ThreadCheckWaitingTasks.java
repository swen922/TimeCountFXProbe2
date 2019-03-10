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

        AllData.updateAllStatus("Starting ThreadCheckWaitingTasks...");

        try {
            if (!AllData.waitingTasks.isEmpty()) {
                AllData.updateAllStatus("ThreadCheckWaitingTasks - Список неисполненных обновлений базы составляет " + AllData.waitingTasks.size() + " задач. Активирую неисполненные обновления.");
                AllData.logger.info(AllData.status);

                int counter = 0;

                for (int i = 0; i < AllData.waitingTasks.size(); i++) {
                    SerializeWrapper wrapper = AllData.waitingTasks.poll();
                    if (wrapper != null) {
                        Updater.update(wrapper);
                        counter++;
                    }
                }

                // Запускаем процесс сохранения. Если все задачи ушли на исполнение, то файл списка неисполненных задач с диска удалится.
                Loader loader = new Loader();
                loader.saveWatingTasks();

                AllData.updateAllStatus("ThreadCheckWaitingTasks - активировано " + counter + " задач из списка неисполненных обновлений базы.");
                AllData.logger.info(AllData.status);

            }
        } catch (Exception e) {
            e.printStackTrace();
            AllData.updateAllStatus("ThreadCheckWaitingTasks - Не удалось заново запустить невыполненные задачи: выброшено исключение.");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
    }
}
