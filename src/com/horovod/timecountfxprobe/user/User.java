package com.horovod.timecountfxprobe.user;

public interface User {
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
}
