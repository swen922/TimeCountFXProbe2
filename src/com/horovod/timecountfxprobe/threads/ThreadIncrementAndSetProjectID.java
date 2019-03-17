package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Updater;
import javafx.concurrent.Task;

public class ThreadIncrementAndSetProjectID extends Task<Integer> {
    @Override
    protected Integer call() throws Exception {
        Integer result = null;

        Updater updater = new Updater();
        String received = updater.getReceivedFromServer(AllData.httpGetProjectID);

        if (!received.isEmpty() && !received.startsWith("false")) {
            String intTMP = received.split(" ")[0];
            if (intTMP != null && !intTMP.isEmpty()) {
                try {
                    result = Integer.parseInt(intTMP);
                } catch (NumberFormatException e) {

                }
            }
        }

        if (result != null) {

            AllData.createProjectID.set(result);

            String timeTMP = received.split(" ")[1];
            if (timeTMP != null && !timeTMP.isEmpty()) {
                AllData.lastUpdateTime = timeTMP;
            }
        }
        else {
            AllData.updateAllStatus("ThreadIncrementAndSetProjectID - Ошибка получения нового ID-номера для проекта.");
            AllData.logger.error(AllData.status);
        }

        return result;
    }
}
