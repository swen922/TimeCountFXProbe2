package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.LoginWrapper;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadGlobalUpdate implements Runnable {
    @Override
    public void run() {

        AllData.status = "Запускаю глобальное обновление...";
        AllData.updateAllStatus();

        try {
            if (AllData.tasksQueue.isEmpty() && AllData.waitingTasks.isEmpty()) {

                User user = AllUsers.getOneUser(AllUsers.getCurrentUser());
                LoginWrapper loginWrapper = new LoginWrapper(user.getNameLogin(), user.getSecurePassword());

                String jsonSerialize = Updater.getJsonString(loginWrapper);

                if (!jsonSerialize.isEmpty()) {

                    int responceCode = 0;
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(AllData.httpGlobalUpdate);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                        out.write(jsonSerialize);
                        out.flush();
                        out.close();
                        responceCode = connection.getResponseCode();
                    } catch (ConnectException e) {
                        AllData.status = "ThreadGlobalUpdate - Ошибка соединения: java.net.ConnectException";
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

                        if (!received.isEmpty() && !received.startsWith("false")) {

                            boolean success = Updater.globalUpdate(received);

                            if (success) {
                                AllData.updateAllWindows();

                                AllData.status = "ThreadGlobalUpdate - Обновление базы с сервера проведено успешно.";
                                AllData.updateAllStatus();
                                AllData.logger.info(AllData.status);

                            }
                            else {
                                AllData.status = "ThreadGlobalUpdate - Ошибка обновления базы с сервера либо отказ из-за отсутствия базы на сервере.";
                                AllData.updateAllStatus();
                                AllData.logger.error(AllData.status);
                            }
                        }
                        else {
                            AllData.status = "ThreadGlobalUpdate - Ошибка обновления базы с сервера.";
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                    }
                }
                else {
                    AllData.status = "ThreadGlobalUpdate - Отмена глобального обновления из-за ошибки сериализации объекта LoginWrapper в JSON-string.";
                    AllData.updateAllStatus();
                    AllData.logger.error(AllData.status);
                }
            }
            else if (!AllData.waitingTasks.isEmpty()) {
                ThreadCheckWaitingTasks threadCheckWaitingTasks = new ThreadCheckWaitingTasks();
                Updater.update(threadCheckWaitingTasks);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AllData.status = "ThreadGlobalUpdate - Ошибка обновления базы с сервера: выброшено исключение " + e.getMessage();
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
    }
}