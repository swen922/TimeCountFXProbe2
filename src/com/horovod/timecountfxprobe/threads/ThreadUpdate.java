package com.horovod.timecountfxprobe.threads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.serialize.SerializeWrapper;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.concurrent.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ThreadUpdate extends Task<Boolean> {

    private SerializeWrapper serializeWrapper;

    public ThreadUpdate(SerializeWrapper wrapper) {
        this.serializeWrapper = wrapper;
    }


    protected Boolean call() throws Exception {

        System.out.println("insie ThreadUpdate.call");

        AllData.status = "Начинаю отправку обновления на сервер...";
        AllData.updateAllStatus();

        if (serializeWrapper != null) {

            String jsonSerialize = ThreadUtil.getJsonString(serializeWrapper);

            if (jsonSerialize != null && !jsonSerialize.isEmpty()) {

                try {

                    URL url = new URL(AllData.httpUpdate);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                    out.write(jsonSerialize);
                    out.close();

                    int responceCode = connection.getResponseCode();

                    System.out.println(responceCode);

                    if (responceCode == 200) {

                        StringBuilder sb = new StringBuilder("");
                        String tmp = null;
                        BufferedReader inn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((tmp = inn.readLine()) != null) {
                            sb.append(tmp);
                        }
                        inn.close();

                        if (sb.toString().equalsIgnoreCase("true")) {
                            AllData.status = "Обновление успешно отправлено на сервер.";
                            AllData.updateAllStatus();
                            AllData.logger.info(AllData.status);
                            return true;
                        }
                        else if (sb.toString().equalsIgnoreCase("false loginpass")) {
                            AllData.status = "Не удалось обновить данные на сервере: ошибка логина/пароля " +
                                    "Инициатор = userID-" + AllUsers.getCurrentUser() + "  " +
                                    AllUsers.getOneUser(AllUsers.getCurrentUser()).getNameLogin() +
                                    ", Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else {
                            AllData.status = "Не удалось обновить данные на сервере. " +
                                    "Инициатор = userID-" + AllUsers.getCurrentUser() + "  " +
                                    AllUsers.getOneUser(AllUsers.getCurrentUser()).getNameLogin() +
                                    ", Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }

                    }
                    else {
                        AllData.status = "Ошибка соединения. Responce code = " + responceCode;
                        AllData.updateAllStatus();
                        AllData.logger.error(AllData.status);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    AllData.status = "Ошибка обновления. Выброшено исключение! " +
                            "Инициатор = userID-" + AllUsers.getCurrentUser() + "  " +
                            AllUsers.getOneUser(AllUsers.getCurrentUser()).getNameLogin() +
                            ", Объект = " + serializeWrapper;
                    AllData.updateAllStatus();
                    AllData.logger.error(AllData.status);
                    AllData.logger.error(e.getMessage(), e);
                }
            }
            else {
                AllData.status = "Отмена отправки обновления на сервер из-за ошибки сериализации объекта serializeWrapper";
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);
            }
        }
        else {
            AllData.status = "Ошибка обновления. Объект serializeWrapper = null";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
        }
        return false;
    }
}
