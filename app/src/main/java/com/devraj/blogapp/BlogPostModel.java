package com.devraj.blogapp;

import java.util.Date;
import java.sql.Timestamp;

public class BlogPostModel {
    String image_thumb;
    Date timestamp;
    String desc;
    String image_url;
    String user_id;


    public BlogPostModel() {

    }


    public BlogPostModel(String desc, String image_url, String user_id, String image_thumb, Date timestamp) {
        this.desc = desc;
        this.image_url = image_url;
        this.user_id = user_id;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
