package com.example.nested;

public class Driver {

    private final String name;
    private final int age;
    private final DriverLicense license;
    private final Photo photo;

    public Driver(int age, String name, DriverLicense license, Photo photo) {
        this.name = name;
        this.age = age;
        this.license = license;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public DriverLicense getLicense() {
        return license;
    }

    public Photo getPhoto() {
        return photo;
    }
}
