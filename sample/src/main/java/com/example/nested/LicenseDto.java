package com.example.nested;

import java.util.Date;

public class LicenseDto {

    private String id;
    private PhotoDto photoDto;
    private Date validUntil;

    public LicenseDto(String id, PhotoDto photo, Date validUntil) {
        this.id = id;
        this.photoDto = photo;
        this.validUntil = validUntil;
    }

    public String getId() {
        return id;
    }

    public PhotoDto getPhoto() {
        return photoDto;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    @Override
    public String toString() {
        return "LicenseDto{" +
                "id='" + id + '\'' +
                ", photo='" + photoDto + '\'' +
                ", validUntil=" + validUntil +
                '}';
    }
}
