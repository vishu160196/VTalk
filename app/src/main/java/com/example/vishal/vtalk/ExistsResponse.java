package com.example.vishal.vtalk;

/**
 * Created by vishal on 13/10/17.
 */

class ExistsResponse {
    private Boolean exists;
    private String message;
    private Integer id;

    public Integer getId() {
        return id;
    }

    public Boolean getExists() {
        return exists;

    }

    public String getMessage() {
        return message;
    }
}
