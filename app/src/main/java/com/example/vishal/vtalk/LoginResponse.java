package com.example.vishal.vtalk;

/**
 * Created by vishal on 8/10/17.
 */

class LoginResponse {
    private Integer id;
    private String token;
    private String message;

    public String getMessage() {
        return message;
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}
