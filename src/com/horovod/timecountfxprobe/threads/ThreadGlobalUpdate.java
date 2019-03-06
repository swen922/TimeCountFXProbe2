package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.user.AllUsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadGlobalUpdate implements Runnable {
    @Override
    public void run() {

        System.out.println("глобальное обновление запущено...");

        try {
            if (AllData.tasksQueue.isEmpty() && AllData.waitingTasks.isEmpty()) {

                URL url = new URL(AllData.httpGlobalUpdate);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responceCode = 0;
                try {
                    responceCode = connection.getResponseCode();
                } catch (ConnectException e) {
                    AllData.status = ThreadGlobalUpdate.class.getSimpleName() + " - Ошибка соединения: java.net.ConnectException";
                    AllData.updateAllStatus();
                    AllData.logger.error(AllData.status);
                    AllData.logger.error(e.getMessage(), e);
                }

                if (responceCode == 200) {
                    StringBuilder sb = new StringBuilder("");
                    String tmp = null;
                    BufferedReader inn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((tmp = inn.readLine()) != null) {
                        sb.append(tmp);
                    }
                    inn.close();

                    String received = sb.toString();

                    if (!received.isEmpty()) {

                        boolean success = Updater.globalUpdate(received);

                        if (success) {
                            AllData.updateAllWindows();

                            AllData.status = ThreadGlobalUpdate.class.getSimpleName() + " - Обновление базы с сервера успешно проведено.";
                            AllData.updateAllStatus();
                            AllData.logger.info(AllData.status);

                            System.out.println("глобальное обновление успешно!");

                        }
                        else {
                            AllData.status = ThreadGlobalUpdate.class.getSimpleName() + " - Ошибка обновления базы с сервера либо отказ из-за отсутствия базы на сервере.";
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                    }
                    else {
                        AllData.status = ThreadGlobalUpdate.class.getSimpleName() + " - Ошибка обновления базы с сервера.";
                        AllData.updateAllStatus();
                        AllData.logger.error(AllData.status);
                    }
                }
            }
            else if (!AllData.waitingTasks.isEmpty()) {
                ThreadCheckWaitingTasks threadCheckWaitingTasks = new ThreadCheckWaitingTasks();
                Updater.update(threadCheckWaitingTasks);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AllData.status = ThreadGlobalUpdate.class.getSimpleName() + " - Ошибка обновления базы с сервера: выброшено исключение IOException.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
    }
}