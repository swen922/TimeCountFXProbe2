package com.horovod.timecountfxprobe.threads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.AllDataWrapper;
import com.horovod.timecountfxprobe.serialize.AllDataWrapperServer;
import com.horovod.timecountfxprobe.serialize.ServerToClientWrapper;
import com.horovod.timecountfxprobe.serialize.Updater;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ThreadGetBaseFromServer extends Task<String> {

    //private String downloadFolder;
    private File file;

    public ThreadGetBaseFromServer(File file) {
        this.file = file;
    }

    @Override
    public String call() throws Exception {

        String result = "false";

        Updater updater = new Updater();
        String received = updater.getReceivedFromServer(AllData.httpGetBaseFromServer);
        if (!received.isEmpty() && !received.startsWith("false")) {

            ObjectMapper mapper = new ObjectMapper();
            ServerToClientWrapper serverToClientWrapper = null;
            serverToClientWrapper = mapper.readValue(received, ServerToClientWrapper.class);

            if (serverToClientWrapper != null) {

                AllDataWrapperServer allDataWrapperServer = new AllDataWrapperServer(serverToClientWrapper);

                JAXBContext context = JAXBContext.newInstance(AllDataWrapperServer.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                marshaller.marshal(allDataWrapperServer, this.file);

                AllData.updateAllStatus("Файл базы успешно получен с сервера и сохранен на диск.");
                AllData.logger.info(AllData.status);

                return "true";
            }
            else {
                AllData.updateAllStatus("ThreadGetBaseFromServer - Ошибка восстановления объекта ServerToClientWrapper.");
                AllData.logger.error(AllData.status);
            }


            /*AllData.updateTimeStamp();
            String baseName = "/base_" + AllData.timeStamp + ".txt";

            try {
                File file = new File(downloadFolder + baseName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
                writer.write(received);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                AllData.updateAllStatus("ThreadGetBaseFromServer - Ошибка скачивания базы с сервера. Выброшено исключение IOException");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
            }*/

        }
        else {
            AllData.updateAllStatus("ThreadGetBaseFromServer - Ошибка скачивания базы с сервера.");
            AllData.logger.error(AllData.status);
        }

        return result;
    }
}
