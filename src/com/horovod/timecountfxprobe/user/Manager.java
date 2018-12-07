package com.horovod.timecountfxprobe.user;

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

    public Manager(String nameLogin, String password) {
        this.IDNumber = AllUsers.incrementIdNumberAndGet();
        this.nameLogin = nameLogin.toLowerCase();
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
        this.fullName = newFullName;
    }

    @XmlElement(name = "manageremail")
    public String getEmail() {
        return email;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return getIDNumber() == manager.getIDNumber() &&
                Objects.equals(getNameLogin(), manager.getNameLogin()) &&
                getRole() == manager.getRole() &&
                Objects.equals(getFullName(), manager.getFullName()) &&
                Objects.equals(getEmail(), manager.getEmail());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getIDNumber(), getNameLogin(), getRole(), getFullName(), getEmail());
    }

    @Override
    public String toString() {
        return "Manager{" +
                "IDNumber=" + IDNumber +
                ", nameLogin='" + nameLogin + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                '}' + "\n";
    }
}
