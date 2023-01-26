package com.comsats.aistoryteller;

public class reviewModel {

    String r_name, r_phone, r_rating,r_comment;

    public reviewModel(String r_name, String r_phone, String r_rating, String r_comment) {
        this.r_name = r_name;
        this.r_phone = r_phone;
        this.r_rating = r_rating;
        this.r_comment = r_comment;
    }

    public reviewModel() {
    }

    public String getR_name() {
        return r_name;
    }

    public void setR_name(String r_name) {
        this.r_name = r_name;
    }

    public String getR_phone() {
        return r_phone;
    }

    public void setR_phone(String r_phone) {
        this.r_phone = r_phone;
    }

    public String getR_rating() {
        return r_rating;
    }

    public void setR_rating(String r_rating) {
        this.r_rating = r_rating;
    }

    public String getR_comment() {
        return r_comment;
    }

    public void setR_comment(String r_comment) {
        this.r_comment = r_comment;
    }
}