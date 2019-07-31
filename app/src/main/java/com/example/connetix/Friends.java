package com.example.connetix;

public class Friends {

    public String date, profileImage, fullname;

    public Friends() {

    }

    public Friends(String date,String profileImage, String fullname) {
        this.date = date;
        this.profileImage = profileImage;
        this.fullname = fullname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
