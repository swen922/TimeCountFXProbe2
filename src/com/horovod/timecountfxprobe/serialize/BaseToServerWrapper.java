package com.horovod.timecountfxprobe.serialize;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "basetoserverwrapper")
public class BaseToServerWrapper {

    private String login;
    private SecurePassword securePassword;

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

    public BaseToServerWrapper() {

        this.allProjectsIdNumber = AllData.createProjectID.get();

        this.allProjects.putAll(AllData.getAllProjects());

        this.IDCounterAllUsers = AllUsers.createUserID.get();

        for (User usr : AllUsers.getUsers().values()) {
            if (usr.getRole().equals(Role.DESIGNER)) {
                //System.out.println(usr);
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

    @XmlElement(name = "securepassinwrapper")
    public SecurePassword getSecurePassword() {
        return securePassword;
    }

    public int getAllProjectsIdNumber() {
        return allProjectsIdNumber;
    }

    @XmlElement(name = "allprojects")
    public Map<Integer, Project> getAllProjects() {
        return allProjects;
    }

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
