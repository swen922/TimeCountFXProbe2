package com.horovod.timecountfxprobe.serialize;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;

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

    private final String fileOptionsName = "tcprobeFX.xml";
    private final String fileBackupOptionsName = "tcprobeFXBackup.xml";
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

        /*try {

        } catch (IOException e) {
            System.out.println("Can't save data to file:\n" + file.getPath());
            e.printStackTrace();
            return false;
        } catch (JAXBException e) {
            System.out.println("Can't marshal data");
            e.printStackTrace();
            return false;
        }*/

        return true;
    }


    public boolean load() throws JAXBException {

        if (!this.file.exists()) {
            System.out.println("No data to load");
            return false;
        }

        JAXBContext context = JAXBContext.newInstance(AllDataWrapper.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        AllDataWrapper allDataWrapper = (AllDataWrapper) unmarshaller.unmarshal(this.file);

        if (allDataWrapper != null) {

            AllUsers.setIDCounterAllUsers(allDataWrapper.getIDCounterAllUsers());
            AllUsers.getUsers().putAll(allDataWrapper.getSaveDesigners());
            AllUsers.getUsers().putAll(allDataWrapper.getSaveManagers());
            AllUsers.getUsers().putAll(allDataWrapper.getSaveAdmins());
            AllUsers.getUsers().putAll(allDataWrapper.getSaveSurveyors());
            AllUsers.setUsersPass(allDataWrapper.getSaveUsersPass());
            AllUsers.setCurrentUser(allDataWrapper.getCurrentUser());
            AllUsers.setUsersLogged(allDataWrapper.getSaveUsersLogged());

            AllData.setIdNumber(allDataWrapper.getAllProjectsIdNumber());
            AllData.setAllProjects(allDataWrapper.getAllProjects());
            AllData.rebuildWorkSum();
            AllData.rebuildActiveProjects();

            return true;
        }

        /*try {

        } catch (JAXBException e) {
            e.printStackTrace();
        }*/

        return false;
    }

}
