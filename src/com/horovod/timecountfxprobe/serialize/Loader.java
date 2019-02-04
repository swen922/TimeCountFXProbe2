package com.horovod.timecountfxprobe.serialize;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;
import com.horovod.timecountfxprobe.serialize.AllDataWrapper;

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

    private final String fileOptionsName = "/tcprobeFX.xml";
    private final String fileBackupOptionsName = "/tcprobeFXBackup.xml";
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

            AllData.getAllProjects().clear();
            AllData.getActiveProjects().clear();
            AllData.setIdNumber(0);
            AllData.setWorkSumProjects(0);

            AllUsers.getUsers().clear();
            AllUsers.setIDCounterAllUsers(0);
            AllUsers.getUsersPass().clear();
            AllUsers.setCurrentUser(0);
            AllUsers.getUsersLogged().clear();

            System.out.println("all is zeroed");
            System.out.println("AllData.getAllProjects().size() = " + AllData.getAllProjects().size());
            System.out.println("AllData.getIdNumber() = " + AllData.getIdNumber());
            System.out.println("AllData.getWorkSumProjects() = " + AllData.getWorkSumProjects());
            System.out.println("AllUsers.getUsers().size()" + AllUsers.getUsers().size());
            System.out.println("AllUsers.getIDCounterAllUsers()" + AllUsers.getIDCounterAllUsers());
            System.out.println("AllUsers.getUsersPass()" + AllUsers.getUsersPass());
            System.out.println("AllUsers.getCurrentUser()" + AllUsers.getCurrentUser());
            System.out.println(AllUsers.getUsersLogged());
            System.out.println("\n");


            System.out.println("now starting rebuild all...");

            AllUsers.setIDCounterAllUsers(allDataWrapper.getIDCounterAllUsers());
            System.out.println("allDataWrapper.getIDCounterAllUsers() = " + allDataWrapper.getIDCounterAllUsers());
            System.out.println("id project = " + AllUsers.getIDCounterAllUsers());

            AllUsers.getUsers().putAll(allDataWrapper.getSaveDesigners());
            AllUsers.getUsers().putAll(allDataWrapper.getSaveManagers());
            AllUsers.getUsers().putAll(allDataWrapper.getSaveAdmins());
            AllUsers.getUsers().putAll(allDataWrapper.getSaveSurveyors());
            System.out.println(AllUsers.getUsers().size());

            AllUsers.setUsersPass(allDataWrapper.getSaveUsersPass());
            System.out.println(AllUsers.getUsersPass().size());

            AllUsers.setCurrentUser(allDataWrapper.getCurrentUser());
            System.out.println("allDataWrapper.getCurrentUser() = " + allDataWrapper.getCurrentUser());
            System.out.println("cuurrent user = " + AllUsers.getCurrentUser());

            AllUsers.setUsersLogged(allDataWrapper.getSaveUsersLogged());
            System.out.println(AllUsers.getUsersLogged());

            AllData.setIdNumber(allDataWrapper.getAllProjectsIdNumber());
            System.out.println("AllData idNumber = " + AllData.getIdNumber());

            AllData.setAllProjects(allDataWrapper.getAllProjects());
            System.out.println("AllData.getAllProjects().size() = " + AllData.getAllProjects().size());

            AllData.rebuildWorkSum();
            System.out.println(AllData.getWorkSumProjects());

            AllData.rebuildActiveProjects();
            System.out.println(AllData.getActiveProjects().size());

            System.out.println("\n");

            return true;
        }

        /*try {

        } catch (JAXBException e) {
            e.printStackTrace();
        }*/

        return false;
    }

}
