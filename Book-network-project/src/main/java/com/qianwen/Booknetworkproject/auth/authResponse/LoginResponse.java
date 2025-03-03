package com.qianwen.Booknetworkproject.auth.authResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class LoginResponse {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
