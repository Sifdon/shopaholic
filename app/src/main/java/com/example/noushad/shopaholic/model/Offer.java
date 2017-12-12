package com.example.noushad.shopaholic.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by noushad on 11/28/17.
 */

public class Offer implements Serializable {

    private String expiry_date;
    private String id;
    private String location;
    private String name;
    private String phone_no;
    private String email;
    private String link;
    private String categories;
    private String title;
    private String price;
    private String discount;
    private String description;
    private String date;

    public Offer() {

    }

    public Offer(String imageLink, String selectedCategory, String title, String price, String discount,
                 String description,String expiryDate, UserInfo userInfo) {

        this.link = imageLink;
        this.categories = selectedCategory;
        this.title = title;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.expiry_date = expiryDate;
        this.date = String.valueOf(new Date().getTime());
        this.email = userInfo.getEmail();
        this.phone_no = userInfo.getPhone();
        this.name = userInfo.getName();
        this.location = userInfo.getLocation();
        this.id = userInfo.getId();
    }


    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getEmail() {
        return email;
    }

    public String getLink() {
        return link;
    }

    public String getCategories() {
        return categories;
    }

    public String getTitle() {
        return title;
    }

    public String getDiscount() {
        return discount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }

    public String getExpiry_date() {
        return expiry_date;
    }
}
