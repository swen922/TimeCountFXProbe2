package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.LoginWrapper;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;
import javafx.concurrent.Task;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class ThreadGetProjectID implements Callable<Integer> {

    @Override
    public Integer call() {

        Integer result = null;

        try {
            User user = AllUsers.getOneUser(AllUsers.getCurrentUser());
            LoginWrapper loginWrapper = new LoginWrapper(user.getNameLogin(), user.getSecurePassword());

            String jsonSerialize = Updater.getJsonString(loginWrapper);

            if (!jsonSerialize.isEmpty())  {
                HttpURLConnection connection = null;
                int responceCode = 0;
                try {
                    URL url = new URL(AllData.httpGetProjectID);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    responceCode = connection.getResponseCode();
                } catch (ConnectException e) {
                    AllData.status = ThreadGetProjectID.class.getSimpleName() + " - Ошибка соединения: java.net.ConnectException";
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
                        result = Integer.parseInt(received);
                    }

                    if (result == null) {
                        AllData.status = "ThreadGetProjectID - Ошибка получения нового ID-номера для проекта.";
                        AllData.updateAllStatus();
                        AllData.logger.error(AllData.status);
                    }
                }
            }
            else {
                AllData.status = "Отмена получения id-номера проекта из-за ошибки сериализации объекта LoginWrapper в JSON-string.";
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AllData.status = ThreadGetProjectID.class.getSimpleName() + " - Ошибка получения нового ID-номера для проекта. Выброшено исключение!";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }

        return result;
    }
}
