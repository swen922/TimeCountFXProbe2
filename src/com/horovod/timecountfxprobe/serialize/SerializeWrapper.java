package com.horovod.timecountfxprobe.serialize;

// TODO вставить аннотации для сериализации

public class SerializeWrapper {

    private UpdateType updateType;
    private Object object;

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
}
