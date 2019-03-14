package com.horovod.timecountfxprobe.serialize;


import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

/** Специальный класс-обертка для всех данных,
 * чтобы сохранить их в XML-файл.
 * Используется классом Loader для сохранения и чтения сохраненных данных
 *
 * Поскольку JAXB не поддерживает сохранение интерфейсов,
 * сохраняем юзеров в индивидуальные списки согласно классам-имплементаторам */


@XmlRootElement(name = "alldatawrapper")
public class AllDataWrapper {

    //поля класса AllData
    @XmlElement(name = "allprojectsidnumber")
    private int allProjectsIdNumber;

    private Map<Integer, Project> allProjects = new HashMap<>();


    //поля класса AllUsers
    @XmlElement(name = "allusersidcounter")
    private int IDCounterAllUsers;

    private Map<Integer, Designer> saveDesigners = new HashMap<>();
    private Map<Integer, Manager> saveManagers = new HashMap<>();
    private Map<Integer, Admin> saveAdmins = new HashMap<>();
    private Map<Integer, Surveyor> saveSurveyors = new HashMap<>();

    /*private Map<Integer, SecurePassword> saveUsersPass = new HashMap<>();*/

    @XmlElement(name = "currentuser")
    private int currentUser;

    private List<String> saveUsersLogged = new ArrayList<>();

    private String savedHttpAddress;

    @XmlElement(name = "globalupdateperiod")
    private int globalUpdatePeriod;

    @XmlElement(name = "checkwaitingperiod")
    private int checkWaitingPeriod;


    public AllDataWrapper() {

        this.allProjectsIdNumber = AllData.createProjectID.get();
        this.allProjects.putAll(AllData.getAllProjects());

        this.IDCounterAllUsers = AllUsers.createUserID.get();

        for (User usr : AllUsers.getUsers().values()) {
            if (usr.getRole().equals(Role.DESIGNER)) {
                Designer des = (Designer) usr;
                this.saveDesigners.put(des.getIDNumber(), des);
            }
            else if (usr.getRole().equals(Role.MANAGER)) {
                Manager man = (Manager) usr;
                this.saveManagers.put(man.getIDNumber(), man);
            }
            else if (usr.getRole().equals(Role.ADMIN)) {
                Admin adm = (Admin) usr;
                this.saveAdmins.put(adm.getIDNumber(), adm);
            }
            else if (usr.getRole().equals(Role.SURVEYOR)) {
                Surveyor sur = (Surveyor) usr;
                this.saveSurveyors.put(sur.getIDNumber(), sur);
            }
        }
        this.currentUser = AllUsers.getCurrentUser();
        this.saveUsersLogged.addAll(AllUsers.getUsersLogged());
        this.savedHttpAddress = new String(AllData.httpAddress);
        this.globalUpdatePeriod = AllData.globalUpdatePeriod;
        this.checkWaitingPeriod = AllData.checkWaitingPeriod;
    }


    //@XmlElement(name = "allprojectsidnumber")
    public int getAllProjectsIdNumber() {
        return allProjectsIdNumber;
    }

    @XmlElement(name = "allprojects")
    public Map<Integer, Project> getAllProjects() {
        return allProjects;
    }

    //@XmlElement(name = "allusersidcounter")
    public int getIDCounterAllUsers() {
        return IDCounterAllUsers;
    }

    @XmlElement(name = "designers")
    public Map<Integer, Designer> getSaveDesigners() {
        return saveDesigners;
    }

    @XmlElement(name = "managers")
    public Map<Integer, Manager> getSaveManagers() {
        return saveManagers;
    }

    @XmlElement(name = "admins")
    public Map<Integer, Admin> getSaveAdmins() {
        return saveAdmins;
    }

    @XmlElement(name = "surveyors")
    public Map<Integer, Surveyor> getSaveSurveyors() {
        return saveSurveyors;
    }

    //@XmlElement(name = "currentuser")
    public int getCurrentUser() {
        return currentUser;
    }

    @XmlElement(name = "userslogged")
    public List<String> getSaveUsersLogged() {
        return saveUsersLogged;
    }

    @XmlElement(name = "savedhttpaddress")
    public String getSavedHttpAddress() {
        return savedHttpAddress;
    }

    public int getGlobalUpdatePeriod() {
        return globalUpdatePeriod;
    }

    public int getCheckWaitingPeriod() {
        return checkWaitingPeriod;
    }

    public void setSavedHttpAddress(String savedHttpAddress) {
        this.savedHttpAddress = savedHttpAddress;
    }
}
