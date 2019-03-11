package com.horovod.timecountfxprobe.serialize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.horovod.timecountfxprobe.user.SecurePassword;

@JsonAutoDetect
public class IntegerLoginWrapper {

    @JsonDeserialize(as = Integer.class)
    private int numberToSend;

    @JsonDeserialize(as = String.class)
    private String login;

    @JsonDeserialize(as = SecurePassword.class)
    private SecurePassword securePassword;

    public IntegerLoginWrapper(int numberToSend, String login, SecurePassword securePassword) {
        this.numberToSend = numberToSend;
        this.login = login;
        this.securePassword = securePassword;
    }

    public IntegerLoginWrapper() {
    }

    public int getNumberToSend() {
        return numberToSend;
    }

    public void setNumberToSend(int numberToSend) {
        this.numberToSend = numberToSend;
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
}
