package com.horovod.timecountfxprobe.user;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "designer")
public class Designer implements User {
    private int IDNumber;
    private String nameLogin;
    private Role role = Role.DESIGNER;
    private String fullName;
    private String email;
    private int workHourValue = 0;
    private boolean isRetired = false;

    /**
     * TODO в графон в создании юзера вставить проверку nameLogin на уникальность,
     * чтобы не было дублей логина в системе
     * TODO убрать проверки на null в отношении fullName – теперь сделал так,
     * что в конструкторе назначается nameLogin,
     * а затем в процессе создания юзера добавляется реальный fullName
     * */


    public Designer(String nameLogin, String password) {
        this.IDNumber = AllUsers.incrementIdNumberAndGet();
        this.nameLogin = nameLogin.toLowerCase();
        this.fullName = nameLogin;
    }

    @XmlElement(name = "designeridnumber")
    public int getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(int newIDNumber) {
        this.IDNumber = newIDNumber;
    }

    @XmlElement(name = "desigernamelogin")
    public String getNameLogin() {
        return nameLogin;
    }

    public void setNameLogin(String newNameLogin) {
        this.nameLogin = newNameLogin;
    }

    @XmlElement(name = "designerrole")
    public Role getRole() {
        return role;
    }

    public void setRole(Role newrole) {
        this.role = newrole;
    }

    @XmlElement(name = "designerfullname")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String newFullName) {
        String oldFullName = this.fullName;

        this.fullName = newFullName;

        if (AllUsers.isUsersLoggedContainsUser(this.IDNumber)) {
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

    /** TODO В методах equals и hashcode использовать ТОЛЬКО неизменяемые
     * или редкоизменяемые поля, т.к. иначе сломается после смены мейла или стоимости часа */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Designer designer = (Designer) o;
        return IDNumber == designer.IDNumber &&
                Objects.equals(nameLogin, designer.nameLogin) &&
                role == designer.role;
    }

    @Override
    public int hashCode() {

        return Objects.hash(IDNumber, nameLogin, role);
    }

    @Override
    public String toString() {
        return this.fullName;
    }
}
