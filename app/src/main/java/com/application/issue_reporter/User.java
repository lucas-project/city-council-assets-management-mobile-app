package com.application.issue_reporter;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {


    public String getFristname() {
        return fristname;
    }

    public void setFristname(String fristname) {
        this.fristname = fristname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String fristname;
    private String lastname;
    private String uid;

    public User(){
    }
    public User(String fristname, String lastname, String uid){

        this.fristname = fristname;
        this.lastname = lastname;
        this.uid = uid;
    }
}
