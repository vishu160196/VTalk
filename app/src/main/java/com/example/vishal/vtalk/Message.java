package com.example.vishal.vtalk;

/**
 * Created by vishal on 28/10/17.
 */

public class Message {
    private String content;
    private Integer state;

    public String getContent() {
        return content;
    }

    public Integer getState() {
        return state;
    }

    public Message(String content, Integer state) {

        this.content = content;
        this.state = state;
    }
}
