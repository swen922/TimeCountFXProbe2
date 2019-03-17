package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.IntegerLoginWrapper;
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

public class ThreadSetServerProjectID extends Task<Boolean> {

    private int newProjectID;

    public ThreadSetServerProjectID(int newProjectID) {
        this.newProjectID = newProjectID;
    }

    @Override
    public Boolean call() throws Exception {

        AllData.result = false;

        User user = AllUsers.getOneUser(AllUsers.getCurrentUser());

        IntegerLoginWrapper wrapper = new IntegerLoginWrapper(this.newProjectID, user.getNameLogin(), user.getSecurePassword());
        Updater updater = new Updater();
        String jsonString = updater.getJsonString(wrapper);

        if (!jsonString.isEmpty()) {
            HttpURLConnection connection = null;
            int responceCode = 0;
            try {
                URL url = new URL(AllData.httpSetProjectID);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                out.write(jsonString);
                out.flush();
                out.close();
                responceCode = connection.getResponseCode();
            } catch (ConnectException e) {
                AllData.updateAllStatus("ThreadSetServerProjectID - Ошибка соединения: java.net.ConnectException");
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
                int num = 0;

                if (!received.isEmpty() && !received.startsWith("false")) {

                    String intTMP = received.split(" ")[0];
                    if (intTMP != null && !intTMP.isEmpty()) {
                        try {
                            num = Integer.parseInt(intTMP);
                        } catch (NumberFormatException e) {

                        }
                    }

                }

                if (num == newProjectID) {

                    AllData.result = true;
                    AllData.createProjectID.set(num);
                    AllData.updateAllStatus("ThreadSetServerProjectID - Новое значение счетчика проектов успешно установлено на сервере = " + newProjectID);
                    AllData.logger.info(AllData.status);

                    String timeTMP = received.split(" ")[1];
                    if (timeTMP != null && !timeTMP.isEmpty()) {
                        AllData.lastUpdateTime = timeTMP;
                    }
                }
                else {
                    AllData.updateAllStatus("ThreadSetServerProjectID - Ошибка при установке нового значения счетчика проектов на сервере. Полученное число не равно отправленному.");
                    AllData.logger.error(AllData.status);
                }
            }
            else {
                AllData.updateAllStatus("ThreadSetServerProjectID - Не удалось установить новое значение счетчика проектов на сервере. ResponceCode = " + responceCode);
                AllData.logger.error(AllData.status);
            }
        }
        else {
            AllData.updateAllStatus("ThreadSetServerProjectID - Не удалось установить новое значение счетчика проектов на сервере. Созданная JSON-строка пуста.");
            AllData.logger.error(AllData.status);
        }

        return AllData.result;
    }
}
