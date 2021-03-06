package com.horovod.timecountfxprobe.serialize;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.threads.*;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;
import javafx.concurrent.Task;


import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

public class Updater {

    public static final BlockingQueue<Runnable> tasksQueue = new LinkedBlockingQueue<>();

    private static final ExecutorService service = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, tasksQueue);
    //private static ExecutorService service = Executors.newFixedThreadPool(1);

    private static ScheduledExecutorService repeatWaitingTaskService = Executors.newSingleThreadScheduledExecutor();
    //private static ScheduledExecutorService repeatUpdater = Executors.newScheduledThreadPool(5);

    private static ScheduledExecutorService globalUpdateTaskService = Executors.newSingleThreadScheduledExecutor();


    /*public static ExecutorService getService() {
        return service;
    }*/

    public static ScheduledExecutorService getGlobalUpdateTaskService() {
        return globalUpdateTaskService;
    }

    public static ScheduledExecutorService getRepeatWaitingTaskService() {
        return repeatWaitingTaskService;
    }

    public static void setGlobalUpdateTaskService(ScheduledExecutorService globalUpdateTaskService) {
        Updater.globalUpdateTaskService = globalUpdateTaskService;
    }

    public static void setRepeatWaitingTaskService(ScheduledExecutorService repeatWaitingTaskService) {
        Updater.repeatWaitingTaskService = repeatWaitingTaskService;
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

    /*public static String updateString(Task<String> task) {

        String result = "false";
        synchronized (service) {
            try {
                Future<String> resultFuture = (Future<String>) service.submit(task);
                try {
                    result = resultFuture.get(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    AllData.updateAllStatus("Updater.updateString(Task<String> task) - Ошибка выполнения новой нити. Выброшено исключение.");
                    AllData.logger.error(AllData.status);
                    AllData.logger.error(e.getMessage(), e);
                } catch (ExecutionException e) {
                    AllData.updateAllStatus("Updater.updateString(Task<String> task) - Ошибка выполнения новой нити. Выброшено исключение.");
                    AllData.logger.error(AllData.status);
                    AllData.logger.error(e.getMessage(), e);
                } catch (TimeoutException e) {
                    AllData.updateAllStatus("Updater.updateString(Task<String> task) - Ошибка выполнения новой нити. Выброшено исключение.");
                    AllData.logger.error(AllData.status);
                    AllData.logger.error(e.getMessage(), e);
                }
            } catch (Exception e) {
                AllData.updateAllStatus("Updater.updateString(Task<String> task) - Ошибка выполнения новой нити. Выброшено исключение.");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
            }
        }
        return result;
    }*/

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
            resultFuture = service.submit(new ThreadIncrementAndGetProjectID());
        } catch (Exception e) {
            AllData.updateAllStatus("Updater.getProjectID - Ошибка выполнения новой нити ThreadGetProjectID. Выброшено исключение.");
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
            resultFuture = service.submit(new ThreadIncrementAndGetUserID());
        } catch (Exception e) {
            AllData.updateAllStatus("Updater.getUserID - Ошибка выполнения новой нити ThreadGetUserID. Выброшено исключение.");
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

        if (wrapper != null && !wrapper.getSaveAdmins().isEmpty()) {

            AllData.getAllProjects().clear();
            AllData.getActiveProjects().clear();
            AllData.setWorkSumProjects(0);
            AllUsers.getUsers().clear();

            AllUsers.createUserID.set(wrapper.getIDCounterAllUsers());
            AllData.createProjectID.set(wrapper.getAllProjectsIdNumber());
            AllData.lastUpdateTime = wrapper.getLastUpdateTime();
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

        String result = "false";

        try {

            LoginWrapper loginWrapper = null;
            if (AllUsers.getUsers().isEmpty()) {
                loginWrapper = new LoginWrapper("", null);
            }
            else {
                User user = AllUsers.getOneUser(AllUsers.getCurrentUser());
                loginWrapper = new LoginWrapper(user.getNameLogin(), user.getSecurePassword());
            }

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