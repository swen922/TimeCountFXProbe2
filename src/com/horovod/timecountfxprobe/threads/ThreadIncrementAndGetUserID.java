package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Updater;

import java.util.concurrent.Callable;

public class ThreadIncrementAndGetUserID implements Callable<Integer> {

    @Override
    public Integer call() {

        Integer result = null;

        Updater updater = new Updater();
        String received = updater.getReceivedFromServer(AllData.httpGetUserID);
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

            String timeTMP = received.split(" ")[1];
            if (timeTMP != null && !timeTMP.isEmpty()) {
                AllData.lastUpdateTime = timeTMP;
            }
        }
        else {
            AllData.updateAllStatus("ThreadIncrementAndGetUserID - Ошибка получения нового ID-номера для юзера.");
            AllData.logger.error(AllData.status);
        }

        return result;
    }
}