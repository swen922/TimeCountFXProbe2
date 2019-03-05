package com.horovod.timecountfxprobe.threads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.serialize.SerializeWrapper;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.concurrent.Task;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
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
                            AllData.status = "Обновление успешно отправлено на сервер. Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.info(AllData.status);
                            result = true;
                        }
                        else if (received.equalsIgnoreCase("false input")) {
                            AllData.status = "Не удалось обновить данные на сервере: Отправленная строка равна null или пуста. Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false wrapper")) {
                            AllData.status = "Не удалось обновить данные на сервере: Не удалось восстановить serializeWrapper. Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false login")) {
                            AllData.status = "Не удалось обновить данные на сервере: Инициатор обновления не существует, либо уволен. Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false pass")) {
                            AllData.status = "Не удалось обновить данные на сервере: Пароль пользователя неверен. Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false project")) {
                            AllData.status = "Не удалось обновить данные на сервере: Проект равен null или имеет место другая ошибка в объекте проекта. Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false addtime")) {
                            AllData.status = "Не удалось обновить данные на сервере: Метод AllData.addWorkTime на сервере вернул false. Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false user")) {
                            AllData.status = "Не удалось обновить данные на сервере: Пользователь отсутствует/присутствует в списке. Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else if (received.equalsIgnoreCase("false user null")) {
                            AllData.status = "Не удалось обновить данные на сервере: Пользователь равен null. Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else {
                            AllData.status = "Не удалось обновить данные на сервере. Объект = " + serializeWrapper;
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
                    AllData.status = "Ошибка обновления. Выброшено исключение! Объект = " + serializeWrapper;
                    AllData.updateAllStatus();
                    AllData.logger.error(AllData.status);
                    AllData.logger.error(e.getMessage(), e);
                }
            }
            else {
                AllData.status = "Отмена отправки обновления на сервер из-за ошибки сериализации объекта serializeWrapper.";
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);
            }
        }
        else {
            AllData.status = "Ошибка обновления. Не удалось создать объект serializeWrapper.";
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
