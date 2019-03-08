package com.horovod.timecountfxprobe.threads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.serialize.SerializeWrapper;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.concurrent.Task;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

@XmlRootElement(name = "threadupdate")
public class ThreadUpdate extends Task<Boolean> {

    private SerializeWrapper serializeWrapper;

    public ThreadUpdate(SerializeWrapper wrapper) {
        this.serializeWrapper = wrapper;
    }

    @XmlElement(name = "serializewrapper")
    public SerializeWrapper getSerializeWrapper() {
        return serializeWrapper;
    }

    public void setSerializeWrapper(SerializeWrapper serializeWrapper) {
        this.serializeWrapper = serializeWrapper;
    }

    protected Boolean call() {

        boolean result = false;

        AllData.status = "Начинаю отправку обновления на сервер...";
        AllData.updateAllStatus();

        if (serializeWrapper != null) {

            String jsonSerialize = Updater.getJsonString(serializeWrapper);

            if (jsonSerialize != null && !jsonSerialize.isEmpty()) {

                try {

                    URL url = new URL(AllData.httpUpdate);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                    out.write(jsonSerialize);
                    out.flush();
                    out.close();

                    int responceCode = 0;
                    try {
                        responceCode = connection.getResponseCode();
                    } catch (ConnectException e) {
                        AllData.status = ThreadUpdate.class.getSimpleName() + " - Ошибка соединения: java.net.ConnectException";
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

                        if (received.equalsIgnoreCase("true")) {
                            AllData.status = "Обновление успешно отправлено на сервер. Update type = " + serializeWrapper.getUpdateType();
                            AllData.updateAllStatus();
                            AllData.logger.info(AllData.status);
                            result = true;
                        }
                        else if (received.equalsIgnoreCase("false input")) {
                            AllData.status = "Не удалось обновить данные на сервере: Отправленная строка равна null или пуста. SerializeWrapper = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false wrapper")) {
                            AllData.status = "Не удалось обновить данные на сервере: Не удалось восстановить serializeWrapper. SerializeWrapper = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false login")) {
                            AllData.status = "Не удалось обновить данные на сервере: Инициатор обновления не существует, либо уволен. SerializeWrapper = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false pass")) {
                            AllData.status = "Не удалось обновить данные на сервере: Пароль пользователя неверен. SerializeWrapper = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false project")) {
                            AllData.status = "Не удалось обновить данные на сервере: Проект равен null или имеет место другая ошибка в объекте проекта. SerializeWrapper = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false deleteprojectid")){
                            AllData.status = "Не удалось обновить данные на сервере: Полученный id проекта равен null или имеет место другая ошибка в объекте Integer id. SerializeWrapper = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false addtime")) {
                            AllData.status = "Не удалось обновить данные на сервере: Метод AllData.addWorkTime на сервере вернул false. SerializeWrapper = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false user")) {
                            AllData.status = "Не удалось обновить данные на сервере: Пользователь отсутствует/присутствует в списке. SerializeWrapper = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false user null")) {
                            AllData.status = "Не удалось обновить данные на сервере: Пользователь равен null. SerializeWrapper = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else {
                            AllData.status = "Не удалось обновить данные на сервере. SerializeWrapper = " + serializeWrapper;
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
                    AllData.status = "Ошибка обновления. Выброшено исключение! SerializeWrapper = " + serializeWrapper;
                    AllData.updateAllStatus();
                    AllData.logger.error(AllData.status);
                    AllData.logger.error(e.getMessage(), e);
                }
            }
            else {
                AllData.status = "Отмена отправки обновления на сервер из-за ошибки сериализации объекта SerializeWrapper в JSON-string.";
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);
            }
        }
        else {
            AllData.status = "Ошибка обновления. Полученный объект SerializeWrapper равен null.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
        }
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
