package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.AllDataWrapper;
import com.horovod.timecountfxprobe.serialize.BaseToServerWrapper;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadSendBaseToServer extends Task<Boolean> {

    @Override
    protected Boolean call() throws Exception {

        boolean result = false;

        BaseToServerWrapper wrapper = new BaseToServerWrapper();
        JAXBContext context = JAXBContext.newInstance(AllDataWrapper.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();

        marshaller.marshal(wrapper, writer);
        String baseToSend = writer.toString();


        URL url = new URL(AllData.httpSendBaseToServer);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        out.write(baseToSend);
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
                AllData.status = ThreadSendBaseToServer.class.getSimpleName() + " - База успешно отправлена на сервер. ";
                AllData.updateAllStatus();
                AllData.logger.info(AllData.status);
                result = true;
            }
            else if (received.equalsIgnoreCase("false pass")) {
                AllData.status = ThreadSendBaseToServer.class.getSimpleName() + " - Ошибка обновления! Пароль пользователя неверен.";
                AllData.updateAllStatus();
                AllData.logger.error(AllData.status);

            }




        }
        else {
            AllData.status = "Не удалось отправить базу на сервер. ";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
        }

        return result;
    }
}
