package com.horovod.timecountfxprobe.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;


@JsonAutoDetect
@XmlRootElement(name = "securepassword")
public class SecurePassword {

    @JsonDeserialize(as = String.class)
    private String securePass;

    @JsonDeserialize(as = String.class)
    private String salt;

    private SecurePassword(String newSecurePass, String newSalt) {
        this.securePass = newSecurePass;
        this.salt = newSalt;
    }

    public SecurePassword() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurePassword that = (SecurePassword) o;
        return Objects.equals(securePass, that.securePass) &&
                Objects.equals(salt, that.salt);
    }

    @Override
    public int hashCode() {

        return Objects.hash(securePass, salt);
    }

    @Override
    public String toString() {
        return "SecurePassword{" +
                "securePass='" + securePass + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
