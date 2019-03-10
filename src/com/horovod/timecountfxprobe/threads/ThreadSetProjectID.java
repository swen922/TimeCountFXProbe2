package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.LoginWrapper;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadSetProjectID extends Task<Integer> {

    @Override
    public Integer call() {

        Integer result = null;

        Updater updater = new Updater();
        String received = updater.getReceivedFromServer(AllData.httpGetProjectID);
        if (!received.isEmpty() && !received.startsWith("false")) {
            result = Integer.parseInt(received);
        }

        if (result != null) {
            AllData.createProjectID.set(result);
        }
        else {
            AllData.updateAllStatus("ThreadSetProjectID - Ошибка получения нового ID-номера для проекта.");
            AllData.logger.error(AllData.status);
        }

        return result;
    }
}
