package com.horovod.timecountfxprobe.threads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.BaseToServerWrapper;
import com.horovod.timecountfxprobe.serialize.Updater;
import javafx.concurrent.Task;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadSendBaseToServer extends Task<Boolean> {

    @Override
    protected Boolean call() throws Exception {

        AllData.result = false;

        BaseToServerWrapper wrapper = new BaseToServerWrapper();
        Updater updater = new Updater();
        String baseToSend = updater.getJsonString(wrapper);

        if (!baseToSend.isEmpty()) {

            HttpURLConnection connection = null;
            int responceCode = 0;
            try {
                URL url = new URL(AllData.httpSendBaseToServer);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                out.write(baseToSend);
                out.flush();
                out.close();
                responceCode = connection.getResponseCode();
            } catch (ConnectException e) {
                AllData.updateAllStatus("ThreadSendBaseToServer - Ошибка соединения: java.net.ConnectException");
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

                if (received.startsWith("true")) {

                    AllData.result = true;

                    String timeTMP = received.split(" ")[1];
                    if (timeTMP != null && !timeTMP.isEmpty()) {
                        AllData.lastUpdateTime = timeTMP;
                    }

                    AllData.updateAllStatus("ThreadSendBaseToServer - База успешно отправлена на сервер.");
                    AllData.logger.info(AllData.status);
                }
                else if (received.equalsIgnoreCase("false pass")) {
                    AllData.updateAllStatus("ThreadSendBaseToServer - Ошибка отправки базы на сервер. Пароль пользователя неверен.");
                    AllData.logger.error(AllData.status);
                }
                else if (received.equalsIgnoreCase("false user")) {
                    AllData.updateAllStatus("ThreadSendBaseToServer - Ошибка отправки базы на сервер. Пользователь отсутствует или уволен.");
                    AllData.logger.error(AllData.status);
                }
                else if (received.equalsIgnoreCase("false wrapper")) {
                    AllData.updateAllStatus("ThreadSendBaseToServer - Ошибка отправки базы на сервер. Ошибка чтения объекта BaseToServerWrapper.");
                    AllData.logger.error(AllData.status);
                }
                else if (received.equalsIgnoreCase("false input")) {
                    AllData.updateAllStatus("ThreadSendBaseToServer - Ошибка отправки базы на сервер. Полученная сервером строка равна null или пуста.");
                    AllData.logger.error(AllData.status);
                }
                else {
                    AllData.updateAllStatus("ThreadSendBaseToServer - Ошибка отправки базы на сервер. Ответ сервера = " + received);
                    AllData.logger.error(AllData.status);
                }
            }
            else {
                AllData.updateAllStatus("ThreadSendBaseToServer - Не удалось отправить базу на сервер. ResponceCode = " + responceCode);
                AllData.logger.error(AllData.status);
            }
        }
        else {
            AllData.updateAllStatus("ThreadSendBaseToServer - Ошибка отправки базы на сервер. Созданная JSON-строка пуста.");
            AllData.logger.error(AllData.status);
        }
        return AllData.result;
    }
}
