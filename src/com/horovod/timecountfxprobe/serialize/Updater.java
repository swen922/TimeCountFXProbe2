package com.horovod.timecountfxprobe.serialize;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.threads.ThreadGetProjectID;
import com.horovod.timecountfxprobe.threads.ThreadGetUserID;
import com.horovod.timecountfxprobe.threads.ThreadUpdate;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.*;

public class Updater {

    private static ExecutorService service = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, AllData.tasksQueue);
    //private static ExecutorService service = Executors.newFixedThreadPool(1);

    private static ScheduledExecutorService repeatWaitingTaskService = Executors.newSingleThreadScheduledExecutor();
    //private static ScheduledExecutorService repeatUpdater = Executors.newScheduledThreadPool(5);

    private static ScheduledExecutorService globalUpdateTaskService = Executors.newSingleThreadScheduledExecutor();


    /*public static ExecutorService getService() {
        return service;
    }*/

    public static ScheduledExecutorService getRepeatWaitingTaskService() {
        return repeatWaitingTaskService;
    }

    public static ScheduledExecutorService getGlobalUpdateTaskService() {
        return globalUpdateTaskService;
    }


    public static void update(UpdateType updateType, Object object) {
        SerializeWrapper wrapper = new SerializeWrapper(updateType, object);

        Task task = new ThreadUpdate(wrapper);
        try {
            service.submit(task);
        } catch (Exception e) {
            AllData.status = Updater.class.getSimpleName() + " - Ошибка выполнения новой нити.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
    }

    public static void update(SerializeWrapper wrapper) {
        Task task = new ThreadUpdate(wrapper);
        try {
            service.submit(task);
        } catch (Exception e) {
            AllData.status = Updater.class.getSimpleName() + " - Ошибка выполнения новой нити.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
    }


    public static void update(Task task) {
        try {
            service.submit(task);
        } catch (Exception e) {
            AllData.status = Updater.class.getSimpleName() + " - Ошибка выполнения новой нити.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
    }


    public static void update(Runnable runnable) {
        try {
            service.submit(runnable);
        } catch (Exception e) {
            AllData.status = Updater.class.getSimpleName() + " - Ошибка выполнения новой нити.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
    }



    public static Integer getProjectID() {
        Future<Integer> resultFuture = null;
        try {
            resultFuture = service.submit(new ThreadGetProjectID());
        } catch (Exception e) {
            e.printStackTrace();
            AllData.status = Updater.class.getSimpleName() + " - Ошибка выполнения новой нити.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
            return null;
        }

        Integer result = null;
        try {
            result = resultFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            AllData.status = "Ошибка получения нового ID-номера проекта. Выброшено исключение InterruptedException";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            AllData.status = "Ошибка получения нового ID-номера проекта. Выброшено исключение ExecutionException";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return result;
    }

    public static Integer getUserID() {
        Future<Integer> resultFuture = null;
        try {
            resultFuture = service.submit(new ThreadGetUserID());
        } catch (Exception e) {
            e.printStackTrace();
            AllData.status = Updater.class.getSimpleName() + " - Ошибка выполнения новой нити.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
            return null;
        }

        Integer result = null;
        try {
            result = resultFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            AllData.status = "Ошибка получения нового ID-номера юзера. Выброшено исключение InterruptedException";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            AllData.status = "Ошибка получения нового ID-номера юзера. Выброшено исключение ExecutionException";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return result;
    }


    public static boolean globalUpdate(String updatedBase) {

        ServerToClientWrapper wrapper = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            wrapper = mapper.readValue(updatedBase, ServerToClientWrapper.class);
        } catch (IOException e) {
            AllData.status = "Updater - Ошибка чтения объекта ServerToClientWrapper.";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }


        if (wrapper != null && !wrapper.getAllProjects().isEmpty()) {

            AllData.getAllProjects().clear();
            AllData.getActiveProjects().clear();
            AllData.setWorkSumProjects(0);
            AllUsers.getUsers().clear();

            AllUsers.createUserID.set(wrapper.getIDCounterAllUsers());
            AllData.createProjectID.set(wrapper.getAllProjectsIdNumber());
            AllUsers.getUsers().putAll(wrapper.getSaveDesigners());
            AllUsers.getUsers().putAll(wrapper.getSaveManagers());
            AllUsers.getUsers().putAll(wrapper.getSaveAdmins());
            AllUsers.getUsers().putAll(wrapper.getSaveSurveyors());
            AllData.setAllProjects(wrapper.getAllProjects());

            AllData.rebuildWorkSum();
            AllData.rebuildActiveProjects();

            return true;
        }


        /*try {
            JAXBContext context = JAXBContext.newInstance(AllDataWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            AllDataWrapper allDataWrapper = (AllDataWrapper) unmarshaller.unmarshal(new StringReader(updatedBase));

            if (allDataWrapper != null && !allDataWrapper.getAllProjects().isEmpty()) {

                AllData.getAllProjects().clear();
                AllData.getActiveProjects().clear();
                AllData.setWorkSumProjects(0);
                AllUsers.getUsers().clear();

                AllUsers.createUserID.set(allDataWrapper.getIDCounterAllUsers());
                AllData.createProjectID.set(allDataWrapper.getAllProjectsIdNumber());
                AllUsers.getUsers().putAll(allDataWrapper.getSaveDesigners());
                AllUsers.getUsers().putAll(allDataWrapper.getSaveManagers());
                AllUsers.getUsers().putAll(allDataWrapper.getSaveAdmins());
                AllUsers.getUsers().putAll(allDataWrapper.getSaveSurveyors());
                AllData.setAllProjects(allDataWrapper.getAllProjects());

                AllData.rebuildWorkSum();
                AllData.rebuildActiveProjects();

                return true;
            }
        } catch (JAXBException e) {
            e.printStackTrace();
            AllData.status = Updater.class.getSimpleName() + " - Не удалось провести обновление базы с сервера. Ошибка сериализации из XML: JAXBException";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }*/

        return false;
    }

    public static synchronized String getJsonString(Object object) {
        String jsonSerialize = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonSerialize = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            AllData.status = "Ошибка сериализации объекта!";
            AllData.updateAllStatus();
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return jsonSerialize;
    }

}