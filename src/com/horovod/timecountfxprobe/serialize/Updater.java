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
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.*;

public class Updater {

    private static final ExecutorService service = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, AllData.tasksQueue);
    //private static ExecutorService service = Executors.newFixedThreadPool(1);

    private static final ScheduledExecutorService repeatWaitingTaskService = Executors.newSingleThreadScheduledExecutor();
    //private static ScheduledExecutorService repeatUpdater = Executors.newScheduledThreadPool(5);

    private static final ScheduledExecutorService globalUpdateTaskService = Executors.newSingleThreadScheduledExecutor();


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
        synchronized (service) {
            try {
                service.submit(task);
            } catch (Exception e) {
                AllData.updateAllStatus("Updater.update(UpdateType updateType, Object object) - Ошибка выполнения новой нити. Выброшено исключение.");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
            }
        }

    }

    public static void update(SerializeWrapper wrapper) {
        Task task = new ThreadUpdate(wrapper);

        synchronized (service) {
            try {
                service.submit(task);
            } catch (Exception e) {
                AllData.updateAllStatus("Updater.update(SerializeWrapper wrapper) - Ошибка выполнения новой нити. Выброшено исключение.");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
            }
        }
    }

    public static void update(Task task) {

        synchronized (service) {
            try {
                service.submit(task);
            } catch (Exception e) {
                AllData.updateAllStatus("Updater.update(Task task) - Ошибка выполнения новой нити. Выброшено исключение.");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
            }
        }
    }

    public static void update(Runnable runnable) {

        synchronized (service) {
            try {
                service.submit(runnable);
            } catch (Exception e) {
                AllData.updateAllStatus("Updater.update(Runnable runnable) - Ошибка выполнения новой нити. Выброшено исключение.");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
            }
        }
    }


    public static synchronized Integer getProjectID() {
        Future<Integer> resultFuture = null;
        try {
            resultFuture = service.submit(new ThreadGetProjectID());
        } catch (Exception e) {
            AllData.updateAllStatus("Updater.getProjectID - Ошибка выполнения новой нити. Выброшено исключение.");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
            return null;
        }

        Integer result = null;
        try {
            result = resultFuture.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            AllData.updateAllStatus("Updater.getProjectID - Ошибка получения нового ID-номера проекта. Выброшено исключение InterruptedException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        } catch (ExecutionException e) {
            AllData.updateAllStatus("Updater.getProjectID - Ошибка получения нового ID-номера проекта. Выброшено исключение ExecutionException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        } catch (TimeoutException e) {
            AllData.updateAllStatus("Updater.getProjectID - Ошибка получения нового ID-номера проекта. Выброшено исключение TimeoutException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return result;
    }

    public static synchronized Integer getUserID() {
        Future<Integer> resultFuture = null;
        try {
            resultFuture = service.submit(new ThreadGetUserID());
        } catch (Exception e) {
            AllData.updateAllStatus("Updater.getUserID - Ошибка выполнения новой нити. Выброшено исключение.");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
            return null;
        }

        Integer result = null;
        try {
            result = resultFuture.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            AllData.updateAllStatus("Updater.getUserID - Ошибка получения нового ID-номера юзера. Выброшено исключение InterruptedException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        } catch (ExecutionException e) {
            AllData.updateAllStatus("Updater.getUserID - Ошибка получения нового ID-номера юзера. Выброшено исключение ExecutionException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        } catch (TimeoutException e) {
            AllData.updateAllStatus("Updater.getUserID - Ошибка получения нового ID-номера юзера. Выброшено исключение TimeoutException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return result;
    }


    public static synchronized boolean globalUpdate(String updatedBase) {

        ServerToClientWrapper wrapper = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            wrapper = mapper.readValue(updatedBase, ServerToClientWrapper.class);
        } catch (IOException e) {
            AllData.updateAllStatus("Updater.globalUpdate - Ошибка чтения объекта ServerToClientWrapper. Выброшено исключение.");
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

        return false;
    }

    public String getJsonString(Object object) {
        String jsonSerialize = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonSerialize = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            AllData.updateAllStatus("Updater.getJsonString - Ошибка сериализации объекта. Выброшено исключение.");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }
        return jsonSerialize;
    }

    public String getReceivedFromServer(String httpAddress) {

        String result = "";

        try {
            User user = AllUsers.getOneUser(AllUsers.getCurrentUser());
            LoginWrapper loginWrapper = new LoginWrapper(user.getNameLogin(), user.getSecurePassword());

            String jsonSerialize = "";
            ObjectMapper mapper = new ObjectMapper();
            try {
                jsonSerialize = mapper.writeValueAsString(loginWrapper);
            } catch (JsonProcessingException e) {
                AllData.updateAllStatus("updater.getReceivedFromServer - Ошибка сериализации объекта LoginWrapper. Выброшено исключение.");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
            }


            if (!jsonSerialize.isEmpty()) {
                HttpURLConnection connection = null;
                int responceCode = 0;
                try {
                    URL url = new URL(httpAddress);
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
                    AllData.updateAllStatus("updater.getReceivedFromServer - Ошибка соединения: Выброшено исключение java.net.ConnectException");
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

                    result = sb.toString();
                }
            }
        } catch (IOException e) {
            AllData.updateAllStatus("updater.getReceivedFromServer - Ошибка получения ответа от сервера. Выброшено исключение.");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }

        return result;
    }

}