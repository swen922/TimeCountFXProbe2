package com.horovod.timecountfxprobe.serialize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@JsonAutoDetect
@XmlRootElement(name = "serializewrapper")
public class SerializeWrapper {

    @JsonDeserialize(as = UpdateType.class)
    private UpdateType updateType;

    @JsonDeserialize(as = WorkTime.class)
    private WorkTime workTime;

    @JsonDeserialize(as = Project.class)
    private Project project;

    @JsonDeserialize(as = Designer.class)
    private Designer designer;

    @JsonDeserialize(as = Manager.class)
    private Manager manager;

    @JsonDeserialize(as = Admin.class)
    private Admin admin;

    @JsonDeserialize(as = Surveyor.class)
    private Surveyor surveyor;



    @JsonDeserialize(as = String.class)
    private String login;

    @JsonDeserialize(as = SecurePassword.class)
    private SecurePassword securePassword;


    public SerializeWrapper(UpdateType updateType, Object object) {
        this.updateType = updateType;

        if (updateType.equals(UpdateType.UPDATE_TIME)) {
            this.workTime = (WorkTime) object;
        }
        else if (updateType.equals(UpdateType.UPDATE_PROJECT)) {
            this.project = (Project) object;
        }
        else if (updateType.equals(UpdateType.UPDATE_DESIGNER)) {
            this.designer = (Designer) object;
        }
        else if (updateType.equals(UpdateType.UPDATE_MANAGER)) {
            this.manager = (Manager) object;
        }
        else if (updateType.equals(UpdateType.UPDATE_ADMIN)) {
            this.admin = (Admin) object;
        }
        else if (updateType.equals(UpdateType.UPDATE_SURVEYOR)) {
            this.surveyor = (Surveyor) object;
        }

        this.login = AllUsers.getOneUser(AllUsers.getCurrentUser()).getNameLogin();
        this.securePassword = AllUsers.getSecurePassForUser(AllUsers.getCurrentUser());
    }

    public SerializeWrapper() {
    }

    @XmlElement(name = "updatetype")
    public UpdateType getUpdateType() {
        return updateType;
    }

    @XmlElement(name = "worktimeinwrapper")
    public WorkTime getWorkTime() {
        return workTime;
    }

    @XmlElement(name = "projectinwrapper")
    public Project getProject() {
        return project;
    }

    @XmlElement(name = "designerinwrapper")
    public Designer getDesigner() {
        return designer;
    }

    @XmlElement(name = "managerinwrapper")
    public Manager getManager() {
        return manager;
    }

    @XmlElement(name = "admininwrapper")
    public Admin getAdmin() {
        return admin;
    }

    @XmlElement(name = "surveyorinwrapper")
    public Surveyor getSurveyor() {
        return surveyor;
    }


    @XmlElement(name = "userlogininwrapper")
    public String getLogin() {
        return login;
    }

    @XmlElement(name = "securepassinwrapper")
    public SecurePassword getSecurePassword() {
        return securePassword;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public void setWorkTime(WorkTime workTime) {
        this.workTime = workTime;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setDesigner(Designer designer) {
        this.designer = designer;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void setSurveyor(Surveyor surveyor) {
        this.surveyor = surveyor;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setSecurePassword(SecurePassword securePassword) {
        this.securePassword = securePassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializeWrapper that = (SerializeWrapper) o;
        return updateType == that.updateType &&
                Objects.equals(workTime, that.workTime) &&
                Objects.equals(project, that.project) &&
                Objects.equals(designer, that.designer) &&
                Objects.equals(manager, that.manager) &&
                Objects.equals(admin, that.admin) &&
                Objects.equals(surveyor, that.surveyor) &&
                Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {

        return Objects.hash(updateType, workTime, project, designer, manager, admin, surveyor, login);
    }

    @Override
    public String toString() {
        return "SerializeWrapper{" +
                "updateType=" + updateType +
                ", workTime=" + workTime +
                ", project=" + project +
                ", designer=" + designer +
                ", manager=" + manager +
                ", admin=" + admin +
                ", surveyor=" + surveyor +
                ", login='" + login + '\'' +
                '}';
    }
}
