package com.example.connetix;

public class Posts {
    public String uid;
    public String time;
    public String date;
    public String postImage;
    public String profileImage;
    public String description;
    public String fullname;

    public Posts() {

    }

    public Posts(String uid, String time, String date, String postImage, String profileImage, String description, String fullname) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postImage = postImage;
        this.profileImage = profileImage;
        this.description = description;
        this.fullname = fullname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
