package com.example.nested;

import java.util.Date;

public class Photo {

    private String url;
    private Date createdAt;

    public Photo(String url, Date createdAt) {
        this.url = url;
        this.createdAt = createdAt;
    }

    public String getUrl() {
        return url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
