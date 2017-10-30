package com.example.vishal.vtalk;

import java.util.Date;

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
            @Body MessageBody content,
            @Header("Authorization") String token
    );
}

class MessageBody{

    private String content;
    private Integer senderId, receiverId;
    private Date time;

    public MessageBody(String content, Integer senderId, Integer receiverId, Date time) {
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.time = time;
    }
}
