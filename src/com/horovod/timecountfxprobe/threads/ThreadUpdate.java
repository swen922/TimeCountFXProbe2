package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.serialize.SerializeWrapper;
import com.horovod.timecountfxprobe.serialize.Updater;
import javafx.concurrent.Task;


import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;



public class ThreadUpdate extends Task<Boolean> {

    private SerializeWrapper serializeWrapper;

    public ThreadUpdate(SerializeWrapper wrapper) {
        this.serializeWrapper = wrapper;
    }

    protected Boolean call() {

        boolean result = false;

        AllData.updateAllStatus("ThreadUpdate - Начинаю отправку обновления на сервер...");
        if (serializeWrapper != null) {

            Updater updater = new Updater();
            String jsonSerialize = updater.getJsonString(serializeWrapper);

            if (!jsonSerialize.isEmpty()) {

                try {
                    HttpURLConnection connection = null;
                    int responceCode = 0;
                    try {
                        URL url = new URL(AllData.httpUpdate);
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
                        AllData.updateAllStatus("ThreadUpdate - Ошибка соединения: java.net.ConnectException");
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

                        if (received.equalsIgnoreCase("true")) {
                            AllData.updateAllStatus("ThreadUpdate - Обновление успешно отправлено на сервер. Update type = " + serializeWrapper.getUpdateType());
                            AllData.logger.info(AllData.status);
                            result = true;
                        }
                        else if (received.equalsIgnoreCase("false input")) {
                            AllData.updateAllStatus("ThreadUpdate - Не удалось обновить данные на сервере: Отправленная строка равна null или пуста. SerializeWrapper = " + serializeWrapper);
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false wrapper")) {
                            AllData.updateAllStatus("ThreadUpdate - Не удалось обновить данные на сервере: Не удалось восстановить serializeWrapper. SerializeWrapper = " + serializeWrapper);
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false login")) {
                            AllData.updateAllStatus("ThreadUpdate - Не удалось обновить данные на сервере: Инициатор обновления не существует, либо уволен. SerializeWrapper = " + serializeWrapper);
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false pass")) {
                            AllData.updateAllStatus("ThreadUpdate - Не удалось обновить данные на сервере: Пароль пользователя неверен. SerializeWrapper = " + serializeWrapper);
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false project")) {
                            AllData.updateAllStatus("ThreadUpdate - Не удалось обновить данные на сервере: Проект равен null или имеет место другая ошибка в объекте проекта. SerializeWrapper = " + serializeWrapper);
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false deleteprojectid")){
                            AllData.updateAllStatus("ThreadUpdate - Не удалось обновить данные на сервере: Полученный id проекта равен null или имеет место другая ошибка в объекте Integer id. SerializeWrapper = " + serializeWrapper);
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false addtime")) {
                            AllData.updateAllStatus("ThreadUpdate - Не удалось обновить данные на сервере: Метод AllData.addWorkTime на сервере вернул false. SerializeWrapper = " + serializeWrapper);
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false user")) {
                            AllData.updateAllStatus("ThreadUpdate - Не удалось обновить данные на сервере: Пользователь отсутствует/присутствует в списке. SerializeWrapper = " + serializeWrapper);
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false user null")) {
                            AllData.updateAllStatus("ThreadUpdate - Не удалось обновить данные на сервере: Пользователь равен null. SerializeWrapper = " + serializeWrapper);
                            AllData.logger.error(AllData.status);
                        }
                        else {
                            AllData.updateAllStatus("ThreadUpdate - Не удалось обновить данные на сервере. SerializeWrapper = " + serializeWrapper);
                            AllData.logger.error(AllData.status);
                        }

                    }
                    else {
                        AllData.updateAllStatus("ThreadUpdate - Ошибка соединения. Responce code = " + responceCode);
                        AllData.logger.error(AllData.status);
                    }

                } catch (IOException e) {
                    AllData.updateAllStatus("ThreadUpdate - Ошибка обновления. Выброшено исключение! SerializeWrapper = " + serializeWrapper);
                    AllData.logger.error(AllData.status);
                    AllData.logger.error(e.getMessage(), e);
                }
            }
            else {
                AllData.updateAllStatus("ThreadUpdate - Отмена отправки обновления на сервер из-за ошибки сериализации объекта SerializeWrapper в JSON-string.");
                AllData.logger.error(AllData.status);
            }
        }
        else {
            AllData.updateAllStatus("ThreadUpdate - Ошибка обновления. Полученный объект SerializeWrapper равен null.");
            AllData.logger.error(AllData.status);
        }

        /** Если ответ отрицательный, то кладем объект SerializeWrapper в список неисполненных задач AllData.WaitingTasks */
        if (!result) {
            if (!AllData.waitingTasks.contains(serializeWrapper)) {
                AllData.waitingTasks.offer(serializeWrapper);
                Loader loader = new Loader();
                loader.saveWatingTasks();
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreadUpdate that = (ThreadUpdate) o;
        return Objects.equals(serializeWrapper, that.serializeWrapper);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serializeWrapper);
    }
}
