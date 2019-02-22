package com.horovod.timecountfxprobe.threads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.SerializeWrapper;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.serialize.Updater;

public class ThreadUtil {

    public static synchronized String getJsonString(Object object) {
        String jsonSerialize = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonSerialize = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            AllData.status = "Ошибка сериализации объекта! Объект = " + object;
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return jsonSerialize;
    }

    // Ненужный метод для запуска чтения базы сервером,
    // сделал через ServletContextListener на старте и выходе
    /*public static synchronized void readBaseOnServer() {
        ReadBaseOnServer readBaseOnServer = new ReadBaseOnServer(new SerializeWrapper(UpdateType.UPDATE_BASE_ON_SERVER, null));
        Updater.getService().submit(readBaseOnServer);
    }*/
}
