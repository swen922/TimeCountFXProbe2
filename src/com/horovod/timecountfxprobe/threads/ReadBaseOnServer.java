package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.SerializeWrapper;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.concurrent.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadBaseOnServer extends Task<Boolean> {

    private SerializeWrapper serializeWrapper;

    public ReadBaseOnServer(SerializeWrapper serializeWrapper) {
        this.serializeWrapper = serializeWrapper;
    }

    @Override
    protected Boolean call() throws Exception {

        return null;

        /*System.out.println("inside call");

        AllData.status = "Отправляю команду чтения базы сервером...";
        AllData.updateAllStatus();

        if (serializeWrapper != null) {

            String jsonSerialize = ThreadUtil.getJsonString(serializeWrapper)

            if (jsonSerialize != null && !jsonSerialize.isEmpty()) {
                try {
                    URL url = new URL(AllData.httpReadBaseOnServer);
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
                        String receivedString = null;
                        BufferedReader inn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((receivedString = inn.readLine()) != null) {
                            sb.append(receivedString);
                        }
                        inn.close();

                        String responce = sb.toString();

                        System.out.println(responce);

                        if (responce.equalsIgnoreCase("true")) {
                            AllData.status = "Сервер успешно прочитал базу";
                            AllData.updateAllStatus();
                            AllData.logger.info(AllData.status);
                            return true;
                        }
                        else if (sb.toString().equalsIgnoreCase("false loginpass")) {
                            AllData.status = "Ошибка чтения базы сервером: ошибка логина/пароля " +
                                    "Инициатор = userID-" + AllUsers.getCurrentUser() + "  " +
                                    AllUsers.getOneUser(AllUsers.getCurrentUser()).getNameLogin() +
                                    ", Объект = " + serializeWrapper;
                            AllData.updateAllStatus();
                            AllData.logger.error(AllData.status);
                        }
                        else {
                            AllData.status = "Ошибка чтения базы сервером. " +
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
                    AllData.status = "Ошибка чтения базы сервером. Выброшено исключение! " +
                            "Инициатор = id-" + AllUsers.getCurrentUser() + "  " +
                            AllUsers.getOneUser(AllUsers.getCurrentUser()).getNameLogin() +
                            ", Объект = " + serializeWrapper;
                    AllData.updateAllStatus();
                    AllData.logger.error(AllData.status);
                    AllData.logger.error(e.getMessage(), e);
                }
            }
            else {
                AllData.status = "Отмена команды чтения базы сервером из-за ошибки сериализации объекта serializeWrapper";
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);
            }


        }
        return false;
    }*/
    }
}
