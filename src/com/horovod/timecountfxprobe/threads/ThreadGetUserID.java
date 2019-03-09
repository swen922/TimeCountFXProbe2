package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class ThreadGetUserID implements Callable<Integer> {

    @Override
    public Integer call() {

        int result = 0;

        try {

            HttpURLConnection connection = null;
            int responceCode = 0;
            try {
                URL url = new URL(AllData.httpGetUserID);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                responceCode = connection.getResponseCode();
            } catch (ConnectException e) {
                AllData.status = "ThreadGetUserID - Ошибка соединения: java.net.ConnectException";
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

                if (result == 0) {
                    AllData.status = "ThreadGetUserID - Ошибка получения нового ID-номера для юзера.";
                    AllData.updateAllStatus();
                    AllData.logger.error(AllData.status);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AllData.status = ThreadGetProjectID.class.getSimpleName() + " - Ошибка получения нового ID-номера для пользователя. Выброшено исключение! " +
                    "Инициатор = userID-" + AllUsers.getCurrentUser() + "  " +
                    AllUsers.getOneUser(AllUsers.getCurrentUser()).getNameLogin();
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }

        return result;
    }
}
