package com.example.nested;

import java.util.Date;

public class LicenseDto {

    private String id;
    private String photoUrl;
    private Date validUntil;

    public LicenseDto(String id, String photoUrl, Date validUntil) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.validUntil = validUntil;
    }

    @Override
    public String toString() {
        return "LicenseDto{" +
                "id='" + id + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", validUntil=" + validUntil +
                '}';
    }
}
