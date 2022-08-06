package com.application.issue_reporter;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Report implements Serializable {


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReportid() {
        return reportid;
    }

    public void setReportid(String reportid) {
        this.reportid = reportid;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPushkey() {
        return pushkey;
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;
    private String status;
    private String uid;
    private String reportid;
    private String sort;
    private String detail;
    private String imageurl;

    public String getLocationname() {
        return locationname;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    private String locationname;
    private String latitude;
    private String longitude;
    private String pushkey;

    public Report(){
    }
    public Report(String uid, String reportid, String sort, String locationname, String latitude, String longitude,
                  String detail, String imageurl, String pushkey, String status, String time){

        this.longitude = longitude;
        this.latitude = latitude;
        this.locationname = locationname;
        this.uid = uid;
        this.reportid = reportid;
        this.sort = sort;
        this.detail = detail;
        this.imageurl = imageurl;
        this.pushkey = pushkey;
        this.status = status;
        this.time = time;
    }
}
