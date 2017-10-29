package com.example.vishal.vtalk;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by vishal on 15/10/17.
 */

interface SearchClient {
    @GET("/search")
    Call<List<Contact>> searchUser(@Query("contact") String name, @Header("Authorization") String token);
}
