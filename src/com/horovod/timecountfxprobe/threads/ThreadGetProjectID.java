package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.concurrent.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class ThreadGetProjectID implements Callable<Integer> {

    @Override
    public Integer call() {

        Integer result = null;

        try {
            URL url = new URL(AllData.httpUpdate);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write("projectid");
            out.close();

            int responceCode = connection.getResponseCode();

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
                    result = Integer.parseInt(received);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AllData.status = "Ошибка получения нового ID-номера для проекта. Выброшено исключение! " +
                    "Инициатор = userID-" + AllUsers.getCurrentUser() + "  " +
                    AllUsers.getOneUser(AllUsers.getCurrentUser()).getNameLogin();
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return result;
    }
}
