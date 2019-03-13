package com.horovod.timecountfxprobe.threads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.*;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ThreadGetBaseFromServer extends Task<String> {

    private File file;
    String stat = "false";

    public ThreadGetBaseFromServer(File file) {
        this.file = file;
    }

    @Override
    public String call() throws Exception {

        AllData.result = false;

        Updater updater = new Updater();
        String received = updater.getReceivedFromServer(AllData.httpGlobalUpdate);
        if (!received.isEmpty() && !received.startsWith("false")) {

            ServerToClientWrapper wrapper = null;
            ObjectMapper mapper = new ObjectMapper();
            try {
                wrapper = mapper.readValue(received, ServerToClientWrapper.class);
            } catch (IOException e) {
                AllData.updateAllStatus("ThreadGetBaseFromServer - Ошибка чтения объекта ServerToClientWrapper. Выброшено исключение.");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
                stat = "false ServerToClientWrapper";
            }

            if (wrapper != null) {
                Loader loader = new Loader();
                boolean saved = loader.save(wrapper, file);

                if (saved) {
                    AllData.result = true;
                    return "true";
                }
            }
        }
        else {
            AllData.updateAllStatus("ThreadGetBaseFromServer - Ошибка получения базы от сервера.");
            AllData.logger.error(AllData.status);
            stat = "false received";
        }

        return stat;


        /*System.out.println("inside ThreadGetBaseFromServer.call");

        String result = "false";

        Updater updater = new Updater();
        String received = updater.getReceivedFromServer(AllData.httpGetBaseFromServer);

        System.out.println("received.isEmpty() = " + received.isEmpty());
        System.out.println(received.substring(0, 200));

        if (!received.isEmpty() && !received.startsWith("false")) {

            System.out.println("inside if");

            ObjectMapper mapper = new ObjectMapper();

            System.out.println("mapper == null = " + mapper == null);

            ServerToClientWrapper serverToClientWrapper = null;
            try {
                serverToClientWrapper = mapper.readValue(received, ServerToClientWrapper.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(serverToClientWrapper.getAllProjectsIdNumber());

            if (serverToClientWrapper != null) {

                System.out.println("serverToClientWrapper != null");

                if (!this.file.exists()) {
                    Files.createFile(Paths.get(file.getAbsolutePath()));
                }

                AllDataWrapperServer allDataWrapperServer = new AllDataWrapperServer(serverToClientWrapper);

                System.out.println(allDataWrapperServer.getIDCounterAllUsers());

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


            *//*AllData.updateTimeStamp();
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
            }*//*

        }
        else {
            AllData.updateAllStatus("ThreadGetBaseFromServer - Ошибка скачивания базы с сервера.");
            AllData.logger.error(AllData.status);
        }*/

    }
}
