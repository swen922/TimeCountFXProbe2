package com.horovod.timecountfxprobe.serialize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.horovod.timecountfxprobe.project.AllData;
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

    @JsonDeserialize(as = Integer.class)
    private Integer deletedProjectID;

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
        else if (updateType.equals(UpdateType.UPDATE_PROJECT) || updateType.equals(UpdateType.CREATE_PROJECT)) {
            this.project = (Project) object;
        }
        else if (updateType.equals(UpdateType.DELETE_PROJECT)) {
            this.deletedProjectID = (Integer) object;
        }
        else if (updateType.equals(UpdateType.UPDATE_DESIGNER) || updateType.equals(UpdateType.CREATE_DESIGNER)) {
            this.designer = (Designer) object;
        }
        else if (updateType.equals(UpdateType.UPDATE_MANAGER) || updateType.equals(UpdateType.CREATE_MANAGER)) {
            this.manager = (Manager) object;
        }
        else if (updateType.equals(UpdateType.UPDATE_ADMIN) || updateType.equals(UpdateType.CREATE_ADMIN)) {
            this.admin = (Admin) object;
        }
        else if (updateType.equals(UpdateType.UPDATE_SURVEYOR) || updateType.equals(UpdateType.CREATE_SURVEYOR)) {
            this.surveyor = (Surveyor) object;
        }

        if (AllData.tableProjectsManagerController == null && AllData.adminWindowController == null) {
            if (updateType.equals(UpdateType.CREATE_ADMIN)) {
                this.login = this.admin.getNameLogin();
                this.securePassword = this.admin.getSecurePassword();
            }
            else if (updateType.equals(UpdateType.CREATE_MANAGER)) {
                this.login = this.manager.getNameLogin();
                this.securePassword = this.manager.getSecurePassword();
            }
            else if (updateType.equals(UpdateType.CREATE_DESIGNER)) {
                this.login = this.designer.getNameLogin();
                this.securePassword = this.designer.getSecurePassword();
            }
            else if (updateType.equals(UpdateType.CREATE_SURVEYOR)) {
                this.login = this.surveyor.getNameLogin();
                this.securePassword = this.surveyor.getSecurePassword();
            }
        }
        else if (AllUsers.isUserExist(AllUsers.getCurrentUser())) {
            this.login = AllUsers.getOneUser(AllUsers.getCurrentUser()).getNameLogin();
            this.securePassword = AllUsers.getOneUser(AllUsers.getCurrentUser()).getSecurePassword();
        }
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

    @XmlElement(name = "deletedprojectidinwrapper")
    public Integer getDeletedProjectID() {
        return deletedProjectID;
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

    public void setDeletedProjectID(Integer deletedProjectID) {
        this.deletedProjectID = deletedProjectID;
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
        SerializeWrapper wrapper = (SerializeWrapper) o;
        return updateType == wrapper.updateType &&
                Objects.equals(workTime, wrapper.workTime) &&
                Objects.equals(project, wrapper.project) &&
                Objects.equals(deletedProjectID, wrapper.deletedProjectID) &&
                Objects.equals(designer, wrapper.designer) &&
                Objects.equals(manager, wrapper.manager) &&
                Objects.equals(admin, wrapper.admin) &&
                Objects.equals(surveyor, wrapper.surveyor) &&
                Objects.equals(login, wrapper.login) &&
                Objects.equals(securePassword, wrapper.securePassword);
    }

    @Override
    public int hashCode() {

        return Objects.hash(updateType, workTime, project, deletedProjectID, designer, manager, admin, surveyor, login, securePassword);
    }

    @Override
    public String toString() {
        return "SerializeWrapper{" +
                "updateType=" + updateType +
                ", workTime=" + workTime +
                ", project=" + project +
                ", deletedProjectID=" + deletedProjectID +
                ", designer=" + designer +
                ", manager=" + manager +
                ", admin=" + admin +
                ", surveyor=" + surveyor +
                ", login='" + login + '\'' +
                ", securePassword=" + securePassword +
                '}';
    }
}
