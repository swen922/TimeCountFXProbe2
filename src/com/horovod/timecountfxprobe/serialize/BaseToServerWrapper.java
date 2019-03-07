package com.horovod.timecountfxprobe.serialize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;


@JsonAutoDetect
@XmlRootElement(name = "basetoserverwrapper")
public class BaseToServerWrapper {

    // Поля для логина на сервер, если он не пустой
    @JsonDeserialize(as = String.class)
    private String login;

    @JsonDeserialize(as = SecurePassword.class)
    private SecurePassword securePassword;


    // Передаваемые поля
    @JsonDeserialize(as = Integer.class)
    private int allProjectsIdNumber;

    @JsonDeserialize(as = Integer.class)
    private int IDCounterAllUsers;

    @JsonDeserialize(as = HashMap.class)
    private HashMap<Integer, Project> allProjects = new HashMap<>();

    @JsonDeserialize(as = HashMap.class)
    private HashMap<Integer, Designer> saveDesigners = new HashMap<>();

    @JsonDeserialize(as = HashMap.class)
    private HashMap<Integer, Manager> saveManagers = new HashMap<>();

    @JsonDeserialize(as = HashMap.class)
    private HashMap<Integer, Admin> saveAdmins = new HashMap<>();

    @JsonDeserialize(as = HashMap.class)
    private HashMap<Integer, Surveyor> saveSurveyors = new HashMap<>();


    public BaseToServerWrapper() {
        this.login = AllUsers.getOneUser(AllUsers.getCurrentUser()).getNameLogin();
        this.securePassword = AllUsers.getOneUser(AllUsers.getCurrentUser()).getSecurePassword();

        this.allProjectsIdNumber = AllData.createProjectID.get();
        this.IDCounterAllUsers = AllUsers.createUserID.get();
        this.allProjects.putAll(AllData.getAllProjects());
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
    }

    @XmlElement(name = "userlogininwrapper")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @XmlElement(name = "securepassinwrapper")
    public SecurePassword getSecurePassword() {
        return securePassword;
    }

    public void setSecurePassword(SecurePassword securePassword) {
        this.securePassword = securePassword;
    }

    @XmlElement(name = "allprojectsidnumber")
    public int getAllProjectsIdNumber() {
        return allProjectsIdNumber;
    }

    public void setAllProjectsIdNumber(int allProjectsIdNumber) {
        this.allProjectsIdNumber = allProjectsIdNumber;
    }

    @XmlElement(name = "allusersidcounter")
    public int getIDCounterAllUsers() {
        return IDCounterAllUsers;
    }

    public void setIDCounterAllUsers(int IDCounterAllUsers) {
        this.IDCounterAllUsers = IDCounterAllUsers;
    }

    @XmlElement(name = "allprojects")
    public HashMap<Integer, Project> getAllProjects() {
        return allProjects;
    }

    public void setAllProjects(HashMap<Integer, Project> allProjects) {
        this.allProjects = allProjects;
    }

    @XmlElement(name = "designers")
    public HashMap<Integer, Designer> getSaveDesigners() {
        return saveDesigners;
    }

    public void setSaveDesigners(HashMap<Integer, Designer> saveDesigners) {
        this.saveDesigners = saveDesigners;
    }

    @XmlElement(name = "managers")
    public HashMap<Integer, Manager> getSaveManagers() {
        return saveManagers;
    }

    public void setSaveManagers(HashMap<Integer, Manager> saveManagers) {
        this.saveManagers = saveManagers;
    }

    @XmlElement(name = "admins")
    public HashMap<Integer, Admin> getSaveAdmins() {
        return saveAdmins;
    }

    public void setSaveAdmins(HashMap<Integer, Admin> saveAdmins) {
        this.saveAdmins = saveAdmins;
    }

    @XmlElement(name = "surveyors")
    public HashMap<Integer, Surveyor> getSaveSurveyors() {
        return saveSurveyors;
    }

    public void setSaveSurveyors(HashMap<Integer, Surveyor> saveSurveyors) {
        this.saveSurveyors = saveSurveyors;
    }
}
