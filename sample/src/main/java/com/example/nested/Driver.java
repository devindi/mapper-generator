package com.example.nested;

public class Driver {

    private final String name;
    private final int age;
    private final DriverLicense license;

    public Driver(int age, String name, DriverLicense license) {
        this.name = name;
        this.age = age;
        this.license = license;
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
}
