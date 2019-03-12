package com.horovod.timecountfxprobe.serialize;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "alldataserverwrapper")
public class AllDataWrapperServer {

    private ServerToClientWrapper serverToClientWrapper;

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

    //private Map<Integer, SecurePassword> saveUsersPass = new HashMap<>();


    public AllDataWrapperServer(ServerToClientWrapper serverToClientWrapper) {

        this.serverToClientWrapper = serverToClientWrapper;

        this.allProjectsIdNumber = serverToClientWrapper.getAllProjectsIdNumber();
        this.allProjects = serverToClientWrapper.getAllProjects();

        this.IDCounterAllUsers = serverToClientWrapper.getIDCounterAllUsers();
        this.saveDesigners = serverToClientWrapper.getSaveDesigners();
        this.saveManagers = serverToClientWrapper.getSaveManagers();
        this.saveAdmins = serverToClientWrapper.getSaveAdmins();
        this.saveSurveyors = serverToClientWrapper.getSaveSurveyors();
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
}
