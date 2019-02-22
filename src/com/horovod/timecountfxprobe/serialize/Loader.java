package com.horovod.timecountfxprobe.serialize;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;
import com.horovod.timecountfxprobe.serialize.AllDataWrapper;
import javafx.scene.control.Alert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class Loader {

    private final String fileOptionsName = "/CLIENT_BASE.xml";
    private final String fileBackupOptionsName = "/CLIENT_BASE_Backup.xml";
    private String pathString = AllData.pathToHomeFolder + fileOptionsName;
    private String backupPathString = AllData.pathToHomeFolder + fileBackupOptionsName;

    private Path pathToFile = Paths.get(pathString);
    private Path pathToBackupFile = Paths.get(backupPathString);
    private File file = new File(pathString);
    private File backupFile = new File(backupPathString);


    public boolean save() throws IOException, JAXBException {

        AllDataWrapper allDataWrapper = new AllDataWrapper();

        if (this.file.exists() && this.backupFile.exists()) {
            Files.copy(pathToFile, pathToBackupFile, StandardCopyOption.REPLACE_EXISTING);
        }
        else if (this.file.exists()) {
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

        marshaller.marshal(allDataWrapper, this.file);

        AllData.logger.info(Loader.class.getSimpleName() + " - База успешно сохранена.");

        return true;
    }


    public boolean load() throws JAXBException {

        if (!this.file.exists()) {
            // TODO здесь записа логгера добавить, когда напишу логгер
            return false;
        }

        JAXBContext context = JAXBContext.newInstance(AllDataWrapper.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        AllDataWrapper allDataWrapper = (AllDataWrapper) unmarshaller.unmarshal(this.file);

        if (allDataWrapper != null) {

            AllData.getAllProjects().clear();
            AllData.getActiveProjects().clear();
            AllData.setIdNumber(0);
            AllData.setWorkSumProjects(0);

            AllUsers.getUsers().clear();
            AllUsers.setIDCounterAllUsers(0);
            AllUsers.getUsersPass().clear();
            AllUsers.setCurrentUser(0);
            AllUsers.getUsersLogged().clear();

            AllUsers.setIDCounterAllUsers(allDataWrapper.getIDCounterAllUsers());

            AllUsers.getUsers().putAll(allDataWrapper.getSaveDesigners());
            AllUsers.getUsers().putAll(allDataWrapper.getSaveManagers());
            AllUsers.getUsers().putAll(allDataWrapper.getSaveAdmins());
            AllUsers.getUsers().putAll(allDataWrapper.getSaveSurveyors());
            //System.out.println(AllUsers.getUsers().size());

            AllUsers.setUsersPass(allDataWrapper.getSaveUsersPass());
            //System.out.println(AllUsers.getUsersPass().size());

            AllUsers.setCurrentUser(allDataWrapper.getCurrentUser());
            //AllUsers.setCurrentUser(10);
            /*System.out.println("allDataWrapper.getCurrentUser() = " + allDataWrapper.getCurrentUser());
            System.out.println("cuurrent user = " + AllUsers.getCurrentUser());*/

            AllUsers.setUsersLogged(allDataWrapper.getSaveUsersLogged());
            //System.out.println(AllUsers.getUsersLogged());

            AllData.setIdNumber(allDataWrapper.getAllProjectsIdNumber());
            /*System.out.println("allDataWrapper.getAllProjectsIdNumber() = " + allDataWrapper.getAllProjectsIdNumber());
            System.out.println("AllData idNumber = " + AllData.getIdNumber());*/

            AllData.setAllProjects(allDataWrapper.getAllProjects());
            //System.out.println("AllData.getAllProjects().size() = " + AllData.getAllProjects().size());

            AllData.rebuildWorkSum();
            //System.out.println(AllData.getWorkSumProjects());

            AllData.rebuildActiveProjects();
            //System.out.println(AllData.getActiveProjects().size());

            AllData.logger.info(Loader.class.getSimpleName() + " - База успешно прочитана.");

            return true;
        }

        return false;
    }

}
