package com.example.vishal.vtalk;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Register interface
 */

interface Register {
    @POST("/auth/create-user")
    Call<RegisterResponse> registerNewUser(
            @Body RegistrationForm form
    );
}
