package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadSetProjectID extends Task<Integer> {

    @Override
    public Integer call() {

        Integer result = null;

        try {

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
                AllData.status = "ThreadSetProjectID - Ошибка соединения: java.net.ConnectException";
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


                if (result != null) {
                    AllData.createProjectID.set(result);
                }
                else {
                    AllData.status = "ThreadSetProjectID - Ошибка получения нового ID-номера для проекта.";
                    AllData.updateAllStatus();
                    AllData.logger.error(AllData.status);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AllData.status = "ThreadSetProjectID - Ошибка получения нового ID-номера для проекта. Выброшено исключение!";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return result;
    }
}
