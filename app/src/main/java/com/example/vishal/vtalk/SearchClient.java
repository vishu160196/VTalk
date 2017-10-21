package com.example.vishal.vtalk;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by vishal on 15/10/17.
 */

interface SearchClient {
    @GET("/search")
    Call<SearchResponse> searchUser(@Query("contact") String name);
}
