package com.example.vishal.vtalk;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Login interface
 */

public interface Login {
    @POST("/login")
    Call<LoginResponse> loginUser(
            @Body LoginForm form
    );
}
