package com.horovod.timecountfxprobe.serialize;

// TODO вставить аннотации для сериализации

public class SerializeWrapper {

    private UpdateType updateType;
    private Object object;
    private String login;
    private String password;

    public SerializeWrapper(UpdateType updateType, Object object, String login, String password) {
        this.updateType = updateType;
        this.object = object;
        this.login = login;
        this.password = password;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
