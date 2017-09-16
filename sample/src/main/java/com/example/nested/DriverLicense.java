package com.example.nested;

import java.util.Date;

public class DriverLicense {
    private final String id;
    private final String photoUrl;
    private final Date validUntil;

    public DriverLicense(String id, String photoUrl, Date validUntil) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.validUntil = validUntil;
    }

    public String getId() {
        return id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Date getValidUntil() {
        return validUntil;
    }
}
