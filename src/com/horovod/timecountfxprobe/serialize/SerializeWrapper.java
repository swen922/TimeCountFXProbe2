package com.horovod.timecountfxprobe.serialize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.SecurePassword;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@JsonAutoDetect
@XmlRootElement(name = "serializewrapper")
public class SerializeWrapper {

    @JsonDeserialize(as = UpdateType.class)
    private UpdateType updateType;

    @JsonDeserialize(as = ArrayList.class)
    private ArrayList list = new ArrayList();

    @JsonDeserialize(as = String.class)
    private String login;

    @JsonDeserialize(as = SecurePassword.class)
    private SecurePassword securePassword;


    public SerializeWrapper(UpdateType updateType, Object object) {
        this.updateType = updateType;

        if (updateType.equals(UpdateType.UPDATE_TIME)) {
            WorkTime workTime = (WorkTime) object;
            list.add(workTime);
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

    @XmlElement(name = "updatelist")
    public ArrayList getList() {
        return list;
    }

    @XmlElement(name = "userlogin")
    public String getLogin() {
        return login;
    }

    @XmlElement(name = "secureps")
    public SecurePassword getSecurePassword() {
        return securePassword;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public void setList(ArrayList list) {
        this.list = list;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setSecurePassword(SecurePassword securePassword) {
        this.securePassword = securePassword;
    }
}
