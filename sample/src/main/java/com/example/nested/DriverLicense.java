package com.example.nested;

import java.util.Date;

public class DriverLicense {
    private final String id;
    private final Photo photo;
    private final Date validUntil;

    public DriverLicense(String id, Photo photo, Date validUntil) {
        this.id = id;
        this.photo = photo;
        this.validUntil = validUntil;
    }

    public String getId() {
        return id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public Date getValidUntil() {
        return validUntil;
    }
}
