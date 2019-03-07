package com.horovod.timecountfxprobe.threads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.BaseToServerWrapper;
import javafx.concurrent.Task;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadSendBaseToServer extends Task<Boolean> {

    @Override
    protected Boolean call() throws Exception {

        boolean result = false;

        BaseToServerWrapper wrapper = new BaseToServerWrapper();

        /*JAXBContext context = JAXBContext.newInstance(BaseToServerTest.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(wrapper, writer);
        String baseToSend = writer.toString();*/

        String baseToSend = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            baseToSend = mapper.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            AllData.status = "Ошибка сериализации объекта!";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }

        System.out.println(baseToSend);

        URL url = new URL(AllData.httpSendBaseToServer);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        out.write(baseToSend);
        out.close();

        int responceCode = 0;
        try {
            responceCode = connection.getResponseCode();
        } catch (ConnectException e) {
            AllData.status = ThreadSendBaseToServer.class.getSimpleName() + " - Ошибка соединения: java.net.ConnectException";
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
                AllData.status = ThreadSendBaseToServer.class.getSimpleName() + " - База успешно отправлена на сервер. ";
                AllData.updateAllStatus();
                AllData.logger.info(AllData.status);
                result = true;
            }
            else if (received.equalsIgnoreCase("false pass")) {
                AllData.status = ThreadSendBaseToServer.class.getSimpleName() + " - Ошибка отправки базы на сервер. Пароль пользователя неверен.";
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);
            }
            else if (received.equalsIgnoreCase("false user")) {
                AllData.status = ThreadSendBaseToServer.class.getSimpleName() + " - Ошибка отправки базы на сервер. Пользователь отсутствует или уволен.";
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);
            }
            else if (received.equalsIgnoreCase("false wrapper")) {
                AllData.status = ThreadSendBaseToServer.class.getSimpleName() + " - Ошибка отправки базы на сервер. Ошибка чтения объекта BaseToServerWrapper.";
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);
            }
            else if (received.equalsIgnoreCase("false input")) {
                AllData.status = ThreadSendBaseToServer.class.getSimpleName() + " - Ошибка отправки базы на сервер. Полученная сервером строка равна null или пуста.";
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);
            }
            else {
                AllData.status = ThreadSendBaseToServer.class.getSimpleName() + " - Ошибка отправки базы на сервер. Ответ сервера = " + received;
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);
            }
        }
        else {
            AllData.status = "Не удалось отправить базу на сервер. ResponceCode = " + responceCode;
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
        }

        return result;
    }
}
