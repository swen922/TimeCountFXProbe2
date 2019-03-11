package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Updater;


public class ThreadGlobalUpdate implements Runnable {
    @Override
    public void run() {

        if (Updater.tasksQueue.isEmpty() && AllData.waitingTasks.isEmpty()) {
            AllData.updateAllStatus("Запускаю глобальное обновление...");

            Updater updater = new Updater();
            String received = updater.getReceivedFromServer(AllData.httpGlobalUpdate);
            if (!received.isEmpty() && !received.startsWith("false")) {
                boolean success = Updater.globalUpdate(received);

                if (success) {
                    AllData.updateAllWindows();
                    AllData.updateAllStatus("ThreadGlobalUpdate - Обновление базы с сервера проведено успешно.");
                    AllData.logger.info(AllData.status);
                }
                else {
                    AllData.updateAllStatus("ThreadGlobalUpdate - Ошибка обновления базы с сервера либо отказ из-за отсутствия базы на сервере.");
                    AllData.logger.error(AllData.status);
                }
            }
            else {
                AllData.updateAllStatus("ThreadGlobalUpdate - Ошибка обновления базы с сервера.");
                AllData.logger.error(AllData.status);
            }
        }
    }
}