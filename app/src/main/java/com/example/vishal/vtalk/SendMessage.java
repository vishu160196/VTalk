package com.example.vishal.vtalk;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by vishal on 29/10/17.
 */

public interface SendMessage {
    @POST("/send-message")
    Call<String> sendMessage(
            @Body String content,
            @Header("Authorization") String token
    );
}
