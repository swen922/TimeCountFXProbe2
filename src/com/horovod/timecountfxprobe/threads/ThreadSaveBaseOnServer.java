package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Updater;
import javafx.concurrent.Task;

public class ThreadSaveBaseOnServer extends Task<Boolean> {

    @Override
    protected Boolean call() throws Exception {
        AllData.result = false;

        Updater updater = new Updater();
        String received = updater.getReceivedFromServer(AllData.httpSaveBaseOnServer);
        if (received.equalsIgnoreCase("true")) {
            AllData.result = true;
        }
        return AllData.result;
    }
}
