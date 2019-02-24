package com.horovod.timecountfxprobe.user;

import com.horovod.timecountfxprobe.project.AllData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "manager")
public class Manager implements User {
    private int IDNumber;
    private String nameLogin;
    private Role role = Role.MANAGER;
    private String fullName;
    private String email;
    private int workHourValue = 0;
    private boolean isRetired = false;

    public Manager(String nameLogin) {
        this.IDNumber = AllUsers.incrementIdNumberAndGet();
        this.nameLogin = nameLogin.toLowerCase();
        this.fullName = nameLogin;
    }

    public Manager() {
    }

    @XmlElement(name = "manageridnumber")
    public int getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(int IDNumber) {
        this.IDNumber = IDNumber;
    }

    @XmlElement(name = "managernamelogin")
    public String getNameLogin() {
        return nameLogin;
    }

    public void setNameLogin(String newNameLogin) {
        this.nameLogin = newNameLogin;

    }

    @XmlElement(name = "managerrole")
    public Role getRole() {
        return role;
    }

    public void setRole(Role newrole) {
        this.role = newrole;
    }

    @XmlElement(name = "managerfullname")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String newFullName) {

        String oldFullName = this.fullName;

        this.fullName = newFullName;

        if (AllUsers.isUsersLoggedContainsUser(oldFullName)) {
            AllUsers.deleteLoggedUser(oldFullName);
            AllUsers.addLoggedUser(newFullName);
        }
    }

    @XmlElement(name = "manageremail")
    public String getEmail() {
        return email;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    @XmlElement(name = "managerworkhourvalue")
    public int getWorkHourValue() {
        return workHourValue;
    }

    public void setWorkHourValue(int workHourValue) {
        this.workHourValue = workHourValue;
    }

    @XmlElement(name = "managerisretired")
    public boolean isRetired() {
        return isRetired;
    }

    public void setRetired(boolean retired) {
        isRetired = retired;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return IDNumber == manager.IDNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(IDNumber);
    }

    @Override
    public String toString() {
        return this.fullName;
    }
}
