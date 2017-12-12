package com.example.noushad.shopaholic.model;

/**
 * Created by noushad on 11/28/17.
 */

public class UserInfo {

    private   String name;
    private   String email;
    private   String phone;
    private   String id;
    private   String location;

    public UserInfo(String name, String email, String phone, String id, String location) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;
        this.location = location;
    }

    public  String getName() {
        return name;
    }

    public  String getEmail() {
        return email;
    }

    public  String getPhone() {
        return phone;
    }

    public  String getId() {
        return id;
    }



    public UserInfo() {

    }

    public  String getLocation() {
        return location;
    }

}
