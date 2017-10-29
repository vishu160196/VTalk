package com.example.vishal.vtalk;

import java.util.List;

/**
 * Created by vishal on 27/10/17.
 */

class MessageResponse {
    private String content;
    private Integer sender_id;
    private String time;

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    private String message;

    public String getContent() {
        return content;
    }

    public Integer getSender_id() {
        return sender_id;
    }
}
