package com.example.vishal.vtalk;

import java.util.List;

/**
 * Created by vishal on 15/10/17.
 */

class SearchResponse {
    private List<ContactResponse> responseList;

    public List<ContactResponse> getResponseList() {
        return responseList;
    }
}

class ContactResponse{
    private String name;
    private String username;

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}