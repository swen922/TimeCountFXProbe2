package com.horovod.timecountfxprobe.user;

public enum Role {
    ADMIN("АДМИНИСТРАТОР"),
    DESIGNER("ДИЗАЙНЕР"),
    MANAGER("МЕНЕДЖЕР"),
    SURVEYOR("НАБЛЮДАТЕЛЬ");

    private String textRole;

    Role(String textRole) {
        this.textRole = textRole;
    }

    public String getTextRole() {
        return textRole;
    }
}
