package com.horovod.timecountfxprobe.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.horovod.timecountfxprobe.project.AllData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;


@JsonAutoDetect
@XmlRootElement(name = "designer")
public class Designer implements User {

    @JsonDeserialize(as = Integer.class)
    private int IDNumber;

    @JsonDeserialize(as = String.class)
    private String nameLogin = "";

    @JsonDeserialize(as = Role.class)
    private Role role = Role.DESIGNER;

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

    /**
     * TODO в графон в создании юзера вставить проверку nameLogin на уникальность,
     * чтобы не было дублей логина в системе
     * TODO убрать проверки на null в отношении fullName – теперь сделал так,
     * что в конструкторе назначается nameLogin,
     * а затем в процессе создания юзера добавляется реальный fullName
     * */


    public Designer(int userID, String nameLogin, SecurePassword newSecurePass) {
        this.IDNumber = userID;
        this.nameLogin = nameLogin.toLowerCase();
        this.fullName = nameLogin;
        this.securePassword = newSecurePass;
    }

    public Designer() {
    }


    @XmlElement(name = "designeridnumber")
    public int getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(int newIDNumber) {
        this.IDNumber = newIDNumber;
    }

    @XmlElement(name = "designernamelogin")
    public String getNameLogin() {
        return nameLogin;
    }

    public void setNameLogin(String newNameLogin) {
        this.nameLogin = newNameLogin;
    }

    @XmlElement(name = "designerrole")
    public Role getRole() {
        return Role.DESIGNER;
    }

    @XmlElement(name = "designerfullname")
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

    @XmlElement(name = "designeremail")
    public String getEmail() {
        return email;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    @XmlElement(name = "designerworkhourvalue")
    public int getWorkHourValue() {
        return workHourValue;
    }

    public void setWorkHourValue(int workHourValue) {
        this.workHourValue = workHourValue;
    }

    @XmlElement(name = "designerisretired")
    public boolean isRetired() {
        return isRetired;
    }

    public void setRetired(boolean retired) {
        isRetired = retired;
    }

    @XmlElement(name = "designersecurepass")
    public SecurePassword getSecurePassword() {
        return securePassword;
    }

    public void setSecurePassword(SecurePassword securePassword) {
        this.securePassword = securePassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Designer designer = (Designer) o;
        return IDNumber == designer.IDNumber;
    }

    @Override
    public int hashCode() {

        return Objects.hash(IDNumber);
    }

    /** TODO В методах equals и hashcode использовать ТОЛЬКО неизменяемые
     * или редкоизменяемые поля, т.к. иначе сломается после смены мейла или стоимости часа */



    @Override
    public String toString() {
        return "Designer{" +
                "IDNumber=" + IDNumber +
                ", nameLogin='" + nameLogin + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                '}' + "\n";
    }
}
