package com.horovod.timecountfxprobe.serialize;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import com.horovod.timecountfxprobe.serialize.AllDataWrapper;
import javafx.scene.control.Alert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class Loader {

    private final String fileBaseName = "/CLIENT_BASE.xml";
    private final String fileBackupBaseName = "/CLIENT_BASE_Backup.xml";
    private final String fileWaitingTasksName = "/CLIENT_WAITING_TASKS.xml";
    private final String updateBaseName = "/UPDATE_CLIENT_BASE.xml";


    private String pathBase = AllData.pathToHomeFolder + fileBaseName;
    private String backupPathBase = AllData.pathToHomeFolder + fileBackupBaseName;
    private String pathWaitingTasks = AllData.pathToHomeFolder + fileWaitingTasksName;
    private String updatePathBase = AllData.pathToHomeFolder + updateBaseName;


    private Path pathToFile = Paths.get(pathBase);
    private Path pathToBackupFile = Paths.get(backupPathBase);
    private Path pathToWaitingTasks = Paths.get(pathWaitingTasks);
    private Path pathToUpdateFile = Paths.get(updatePathBase);


    private File fileBase = new File(pathBase);
    private File backupFileBase = new File(backupPathBase);
    private File waitingTasksFile = new File(pathWaitingTasks);
    private File updateFileBase = new File(updatePathBase);



    public boolean save() {

        try {
            AllDataWrapper allDataWrapper = new AllDataWrapper();

            if (this.fileBase.exists() && this.backupFileBase.exists()) {
                Files.copy(pathToFile, pathToBackupFile, StandardCopyOption.REPLACE_EXISTING);
            }
            else if (this.fileBase.exists()) {
                Files.createFile(pathToBackupFile);
                Files.copy(pathToFile, pathToBackupFile, StandardCopyOption.REPLACE_EXISTING);
            }
            else {
                Files.createDirectories(pathToFile.getParent());
                Files.createFile(pathToFile);
            }

            JAXBContext context = JAXBContext.newInstance(AllDataWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(allDataWrapper, this.fileBase);

            AllData.updateAllStatus("Loader.save() - База успешно сохранена.");
            AllData.logger.info(AllData.status);
            AllData.logger.info("---");
            AllData.logger.info("---");
            AllData.logger.info("---");

            return true;

        } catch (IOException e) {
            AllData.updateAllStatus("Loader.save() -  - Не удалось записать базу в файл: IOException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        } catch (JAXBException e) {
            AllData.updateAllStatus("Loader.save() -  - Не удалось записать базу в файл. Ошибка сериализации в XML: JAXBException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }

        return false;
    }


    public boolean load() {

        if (!this.fileBase.exists()) {
            AllData.updateAllStatus("Loader.load() - Ошибка загрузки базы. Отсутствует файл базы.");
            AllData.logger.error(AllData.status);
            return false;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(AllDataWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            AllDataWrapper allDataWrapper = (AllDataWrapper) unmarshaller.unmarshal(this.fileBase);

            if (allDataWrapper != null) {

                AllData.getAllProjects().clear();
                AllData.getActiveProjects().clear();
                AllData.createProjectID.set(0);
                AllData.setWorkSumProjects(0);

                AllUsers.getUsers().clear();
                AllUsers.createUserID.set(0);
                AllUsers.setCurrentUser(0);
                AllUsers.getUsersLogged().clear();

                AllUsers.createUserID.set(allDataWrapper.getIDCounterAllUsers());
                AllData.createProjectID.set(allDataWrapper.getAllProjectsIdNumber());

                AllUsers.getUsers().putAll(allDataWrapper.getSaveDesigners());
                AllUsers.getUsers().putAll(allDataWrapper.getSaveManagers());
                AllUsers.getUsers().putAll(allDataWrapper.getSaveAdmins());
                AllUsers.getUsers().putAll(allDataWrapper.getSaveSurveyors());

                AllUsers.setCurrentUser(allDataWrapper.getCurrentUser());

                AllUsers.setUsersLogged(allDataWrapper.getSaveUsersLogged());

                AllData.setAllProjects(allDataWrapper.getAllProjects());

                AllData.rebuildWorkSum();

                AllData.rebuildActiveProjects();


                AllData.updateAllStatus("Loader.load() - База успешно прочитана.");
                AllData.logger.info(AllData.status);

                if (AllUsers.getCurrentUser() == 0) {
                    if (!AllUsers.getUsers().isEmpty()) {
                        for (User u : AllUsers.getUsers().values()) {
                            if (u.getRole().equals(Role.DESIGNER)) {
                                AllUsers.setCurrentUser(u.getIDNumber());
                                break;
                            }
                        }
                    }
                }

                return true;
            }
        } catch (JAXBException e) {
            AllData.updateAllStatus("Loader.load() - Не удалось прочитать базу из файла. Ошибка десериализации из XML: JAXBException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }

        return false;
    }


    public boolean saveWatingTasks() {

        try {
            if (!AllData.waitingTasks.isEmpty()) {

                if (!this.waitingTasksFile.exists()) {
                    Files.createDirectories(pathToWaitingTasks.getParent());
                    Files.createFile(pathToWaitingTasks);
                }

                WaitingTasksWrapper wrapper = new WaitingTasksWrapper();

                JAXBContext context = JAXBContext.newInstance(WaitingTasksWrapper.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                marshaller.marshal(wrapper, this.waitingTasksFile);

                AllData.updateAllStatus("Loader.saveWatingTasks() - Список неисполненных обновлений базы сохранен в файл.");
                AllData.logger.info(AllData.status);
            }
            else {
                if (this.waitingTasksFile.exists()) {
                    Files.delete(pathToWaitingTasks);

                    AllData.updateAllStatus("Loader.saveWatingTasks() - Файл со списком неисполненных обновлений базы удален.");
                    AllData.logger.info(AllData.status);
                }
            }

            return true;

        } catch (IOException e) {
            AllData.updateAllStatus("Loader.saveWatingTasks() - Не удалось записать список неисполненных обновлений базы в файл: IOException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        } catch (JAXBException e) {
            AllData.updateAllStatus("Loader.saveWatingTasks() - Не удалось записать список неисполненных обновлений базы в файл. Ошибка сериализации в XML: JAXBException");
            AllData.logger.error(AllData.status);
            AllData.logger.error(e.getMessage(), e);
        }

        return false;
    }

    public boolean loadWaitingTasks() {

        // Здесь на входе не нужно записывать ошибку в лог, если файла нет,
        // т.к. если его нет – значит просто нет неисполненных задач, и ничего читать не нужно
        if (this.waitingTasksFile.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(WaitingTasksWrapper.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                WaitingTasksWrapper wrapper = (WaitingTasksWrapper) unmarshaller.unmarshal(this.waitingTasksFile);

                if (wrapper != null) {
                    AllData.waitingTasks.clear();
                    AllData.waitingTasks.addAll(wrapper.getWaitingTasks().keySet());

                    AllData.updateAllStatus("Loader.loadWaitingTasks - Список неисполненных обновлений базы прочитан из файла.");
                    AllData.logger.info(AllData.status);

                    return true;
                }
            } catch (JAXBException e) {
                AllData.updateAllStatus("Loader.loadWaitingTasks - Не удалось прочитать список неисполненных обновлений базы из файла. Ошибка десериализации из XML: JAXBException");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
            }
        }

        return false;
    }

}
