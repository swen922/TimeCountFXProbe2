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
    double getWorkHourValue();
    void setWorkHourValue(double workHourValue);


}
