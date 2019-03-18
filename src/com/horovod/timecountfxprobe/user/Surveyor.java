package com.horovod.timecountfxprobe.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;


@JsonAutoDetect
@XmlRootElement(name = "surveyor")
public class Surveyor implements User {

    @JsonDeserialize(as = Integer.class)
    private int IDNumber;

    @JsonDeserialize(as = String.class)
    private String nameLogin = "";

    @JsonDeserialize(as = Role.class)
    private Role role = Role.SURVEYOR;

    @JsonDeserialize(as = String.class)
    private String fullName = "";

    @JsonDeserialize(as = String.class)
    private String email;

    @JsonDeserialize(as = Integer.class)
    private int workHourValue = 0;

    @JsonDeserialize(as = Boolean.class)
    private boolean isRetired = false;

    @JsonDeserialize(as = SecurePassword.class)
    private SecurePassword securePassword;

    public Surveyor(int userID, String nameLogin, SecurePassword newSecurePass) {
        this.IDNumber = userID;
        this.nameLogin = nameLogin.toLowerCase();
        this.fullName = nameLogin;
        this.securePassword = newSecurePass;
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
        return Role.SURVEYOR;
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

    @XmlElement(name = "surveyorsecurepass")
    public SecurePassword getSecurePassword() {
        return securePassword;
    }

    @Override
    public void setSecurePassword(SecurePassword securePassword) {
        this.securePassword = securePassword;
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
