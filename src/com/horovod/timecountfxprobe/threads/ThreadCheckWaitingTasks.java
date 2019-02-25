package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.serialize.Updater;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.concurrent.Callable;

public class ThreadCheckWaitingTasks extends Task<Boolean> {

    @Override
    protected Boolean call() throws Exception {

        try {
            if (!AllData.waitingTasks.isEmpty()) {

                AllData.status = ThreadCheckWaitingTasks.class.getSimpleName() + " - Список неисполненных обновлений базы составляет " + AllData.waitingTasks.size() + " задач. Активирую неисполненные обновления.";
                AllData.updateAllStatus();
                AllData.logger.info(AllData.status);

                int counter = 0;

                for (int i = 0; i < AllData.waitingTasks.size(); i++) {
                    Task task = AllData.waitingTasks.poll();
                    if (task != null) {
                        Updater.getService().submit(task);
                        counter++;
                    }
                }

                Loader loader = new Loader();
                loader.saveWatingTasks();

                AllData.status = ThreadCheckWaitingTasks.class.getSimpleName() + " - активировано " + counter + " задач из списка неисполненных обновлений базы";
                AllData.updateAllStatus();
                AllData.logger.info(AllData.status);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            AllData.status = ThreadCheckWaitingTasks.class.getSimpleName() + " - Не удалось записать список неисполненных обновлений базы в файл: IOException";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        } catch (JAXBException e) {
            e.printStackTrace();
            AllData.status = ThreadCheckWaitingTasks.class.getSimpleName() + " - Не удалось записать список неисполненных обновлений базы в файл. Ошибка сериализации в XML: JAXBException";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return false;
    }
}
