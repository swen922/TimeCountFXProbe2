package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadSetUserID extends Task<Integer> {

    @Override
    public Integer call() {

        System.out.println("inside ThreadSetUserID.call");

        Integer result = null;

        Updater updater = new Updater();
        String received = updater.getReceivedFromServer(AllData.httpGetUserID);

        System.out.println("received = " + received);

        if (!received.isEmpty() && !received.startsWith("false")) {
            result = Integer.parseInt(received);
        }

        if (result != null) {
            AllUsers.createUserID.set(result);
        }
        else {
            AllData.updateAllStatus("ThreadSetUserID - Ошибка получения нового ID-номера для юзера.");
            AllData.logger.error(AllData.status);
        }

        return result;
    }
}
