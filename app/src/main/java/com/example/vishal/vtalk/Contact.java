package com.example.vishal.vtalk;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vishal on 13/10/17.
 */

class Contact implements Serializable{
    private String name;
    private String username;
    private String message;

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public Contact(String name, String username) {
        this.name = name;
        this.username = username;
    }
    
}
