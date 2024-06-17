package com.example.spike_player.Templates;

public class UserTemplate {
    private String email,userid;
    private String username,userbio,contactdetail,interests,userprofile;

    public UserTemplate() {
        // Empty Constructor for firebase interactivity
    }

    public UserTemplate(String email, String userid) {
        this.email = email;
        this.userid = userid;
    }

    public UserTemplate(String email, String userid, String username, String userbio, String contactdetail, String interests,String userprofile) {
        this.email = email;
        this.userid = userid;
        this.username = username;
        this.userbio = userbio;
        this.contactdetail = contactdetail;
        this.interests = interests;
        this.userprofile=userprofile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserbio() {
        return userbio;
    }

    public void setUserbio(String userbio) {
        this.userbio = userbio;
    }

    public String getContactdetail() {
        return contactdetail;
    }

    public void setContactdetail(String contactdetail) {
        this.contactdetail = contactdetail;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getUserprofile() {
        return userprofile;
    }

    public void setUserprofile(String userprofile) {
        this.userprofile = userprofile;
    }
}
