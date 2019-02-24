package com.horovod.timecountfxprobe.user;

import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

public class Surveyor implements User {

    private int IDNumber;
    private String nameLogin;
    private Role role = Role.SURVEYOR;
    private String fullName;
    private String email;
    private int workHourValue = 0;
    private boolean isRetired = false;

    public Surveyor(String nameLogin) {
        this.IDNumber = AllUsers.incrementIdNumberAndGet();
        this.nameLogin = nameLogin.toLowerCase();
        this.fullName = nameLogin;
    }

    public Surveyor() {
    }

    @XmlElement(name = "surveyoridnumber")
    public int getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(int newIDNumber) {
        this.IDNumber = newIDNumber;
    }

    @XmlElement(name = "surveyornamelogin")
    public String getNameLogin() {
        return nameLogin;
    }

    public void setNameLogin(String newNameLogin) {
        this.nameLogin = newNameLogin;
    }

    @XmlElement(name = "surveyorrole")
    public Role getRole() {
        return role;
    }

    public void setRole(Role newrole) {
        this.role = newrole;
    }

    @XmlElement(name = "surveyorfullname")
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

    @XmlElement(name = "surveyoremail")
    public String getEmail() {
        return email;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    @XmlElement(name = "surveyorworkhourvalue")
    public int getWorkHourValue() {
        return workHourValue;
    }

    public void setWorkHourValue(int workHourValue) {
        this.workHourValue = workHourValue;
    }

    @XmlElement(name = "surveyorisretired")
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
        Surveyor surveyor = (Surveyor) o;
        return IDNumber == surveyor.IDNumber;
    }

    @Override
    public int hashCode() {

        return Objects.hash(IDNumber);
    }

    @Override
    public String toString() {
        return "Surveyor{" +
                "IDNumber=" + IDNumber +
                ", nameLogin='" + nameLogin + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                '}' + "\n";
    }
}
