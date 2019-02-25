package com.horovod.timecountfxprobe.user;

import com.horovod.timecountfxprobe.project.AllData;

public interface User {

    //private void updateAllStatus(String change) {}

    int getIDNumber();
    void setIDNumber(int newIDNumber);
    String getNameLogin();
    void setNameLogin(String newNameLogin);
    Role getRole();
    void setRole(Role newrole);
    String getFullName();
    void setFullName(String newFullName);
    String getEmail();
    void setEmail(String newEmail);
    int getWorkHourValue();
    void setWorkHourValue(int workHourValue);
    boolean isRetired();
    void setRetired(boolean retired);
    SecurePassword getSecurePassword();
    void setSecurePassword(SecurePassword securePassword);
    String toString();

}
