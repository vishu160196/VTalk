package com.example.vishal.vtalk;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by vishal on 27/10/17.
 */

interface MessageClient {
    @GET("/get-messages")
    Call<List<MessageResponse>> getMessages(@Query("id") Integer id, @Header("Authorization") String token);
    
}
