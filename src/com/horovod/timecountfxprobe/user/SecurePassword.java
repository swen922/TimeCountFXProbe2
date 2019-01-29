package com.horovod.timecountfxprobe.user;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "securepassword")
public class SecurePassword {

    private String securePass;
    private String salt;

    private SecurePassword(String newSecurePass, String newSalt) {
        this.securePass = newSecurePass;
        this.salt = newSalt;
    }

    public static SecurePassword getInstance(String newPass) {
        SecurePassword result = null;
        try {
            String slt = PasswordUtil.getSalt(32);
            String securePass = PasswordUtil.generateSecurePassword(newPass, slt);
            result = new SecurePassword(securePass, slt);
        } catch (Error error) {
            return null;
        }
        return result;
    }

    @XmlElement(name = "securepassstring")
    public String getSecurePass() {
        return securePass;
    }

    public void setSecurePass(String newSecurePass) {
        this.securePass = newSecurePass;
    }

    @XmlElement(name = "securesaltstring")
    public String getSalt() {
        return salt;
    }

    public void setSalt(String newSalt) {
        this.salt = newSalt;
    }

}
