package com.example.idempotentapi.dto;

import com.example.idempotentapi.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IdemUser {

    @NotBlank
    private String idemKey;

    @Valid
    @NotNull
    private User userData;

    public IdemUser() {
    }

    public IdemUser(String idemKey, User userData) {
        this.idemKey = idemKey;
        this.userData = userData;
    }

    public String getIdemKey() {
        return idemKey;
    }

    public void setIdemKey(String idemKey) {
        this.idemKey = idemKey;
    }

    public User getUserData() {
        return userData;
    }

    public void setUserData(User userData) {
        this.userData = userData;
    }

    @Override
    public String toString() {
        return "IdemUser{" +
                "idemKey='" + idemKey + '\'' +
                ", userData=" + userData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdemUser idemUser = (IdemUser) o;

        if (idemKey != null ? !idemKey.equals(idemUser.idemKey) : idemUser.idemKey != null) return false;
        return userData != null ? userData.equals(idemUser.userData) : idemUser.userData == null;
    }

    @Override
    public int hashCode() {
        int result = idemKey != null ? idemKey.hashCode() : 0;
        result = 31 * result + (userData != null ? userData.hashCode() : 0);
        return result;
    }
}
