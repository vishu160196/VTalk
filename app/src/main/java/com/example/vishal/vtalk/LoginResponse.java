package com.example.vishal.vtalk;

/**
 * Created by vishal on 8/10/17.
 */

class LoginResponse {
    private String message;
    private String token;
    private Integer id;

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
