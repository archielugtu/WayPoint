package com.springvuegradle.team200.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class SearchActivityRequest {

    private String name = "";
    private String hashtag = "";
    private String hashtagSearchType = "";
    private String activityType = "";
    private String activityTypeSearchType = "";
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date date_start;
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date date_end;


    private boolean viewportRestricted;

    private Float ne_lng = 180f;
    private Float ne_lat = 90f;
    private Float sw_lat = -180f;
    private Float sw_lng = -90f;

    private int p = 0;
    private int size = 20;

    private String sort = "date";

    public SearchActivityRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getHashtagSearchType() {
        return hashtagSearchType;
    }

    public void setHashtagSearchType(String hashtagSearchType) {
        this.hashtagSearchType = hashtagSearchType;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityTypeSearchType() {
        return activityTypeSearchType;
    }

    public void setActivityTypeSearchType(String activityTypeSearchType) {
        this.activityTypeSearchType = activityTypeSearchType;
    }

    public Date getDate_start() {
        return date_start;
    }

    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public Date getDate_end() {
        return date_end;
    }

    public void setDate_end(Date date_end) {
        this.date_end = date_end;
    }

    public Float getNe_lng() {
        return ne_lng;
    }

    public void setNe_lng(Float ne_lng) {
        this.ne_lng = ne_lng;
    }

    public Float getNe_lat() {
        return ne_lat;
    }

    public void setNe_lat(Float ne_lat) {
        this.ne_lat = ne_lat;
    }

    public Float getSw_lat() {
        return sw_lat;
    }

    public void setSw_lat(Float sw_lat) {
        this.sw_lat = sw_lat;
    }

    public Float getSw_lng() {
        return sw_lng;
    }

    public void setSw_lng(Float sw_lng) {
        this.sw_lng = sw_lng;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isViewportRestricted() {
        return viewportRestricted;
    }

    public void setViewportRestricted(boolean viewportRestricted) {
        this.viewportRestricted = viewportRestricted;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "SearchActivityRequest{" +
                "name='" + name + '\'' +
                ", hashtag='" + hashtag + '\'' +
                ", hashtagSearchType='" + hashtagSearchType + '\'' +
                ", activityType='" + activityType + '\'' +
                ", activityTypeSearchType='" + activityTypeSearchType + '\'' +
                ", date_start=" + date_start +
                ", date_end=" + date_end +
                ", viewportRestricted=" + viewportRestricted +
                ", ne_lng=" + ne_lng +
                ", ne_lat=" + ne_lat +
                ", sw_lat=" + sw_lat +
                ", sw_lng=" + sw_lng +
                ", p=" + p +
                ", size=" + size +
                '}';
    }
}
