package com.horovod.timecountfxprobe.serialize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.SecurePassword;

@JsonAutoDetect
public class LoginWrapper {

    @JsonDeserialize(as = String.class)
    private String login;

    @JsonDeserialize(as = SecurePassword.class)
    private SecurePassword securePassword;

    @JsonDeserialize(as = String.class)
    private String lastUpdateTime;

    public LoginWrapper(String login, SecurePassword securePassword) {
        this.login = login;
        this.securePassword = securePassword;
        this.lastUpdateTime = AllData.lastUpdateTime;
    }

    public LoginWrapper() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public SecurePassword getSecurePassword() {
        return securePassword;
    }

    public void setSecurePassword(SecurePassword securePassword) {
        this.securePassword = securePassword;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
