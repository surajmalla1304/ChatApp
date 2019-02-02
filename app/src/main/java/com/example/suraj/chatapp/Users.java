package com.example.suraj.chatapp;



public class Users {

    public String username, status, image, thumb_nail;

    public Users(){

    }


    public Users(String username, String status, String image, String thumb_nail) {
        this.username = username;
        this.status = status;
        this.image = image;
        this.thumb_nail = thumb_nail;
    }

    public Users(String username){
        this.username = username;
        this.status = "Hey there! I am using Chat App";
        this.image = "default";
        this.thumb_nail = "default";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_nail() {
        return thumb_nail;
    }

    public void setThumb_nail(String thumb_nail) {
        this.thumb_nail = thumb_nail;
    }

}
