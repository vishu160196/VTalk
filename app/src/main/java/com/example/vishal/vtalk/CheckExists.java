package com.example.vishal.vtalk;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by vishal on 13/10/17.
 */

interface CheckExists {
    @GET("/auth/user-exists")
    Call<ExistsResponse> userExists(@Query("username") String username, @Header("Authorization") String token);
}
