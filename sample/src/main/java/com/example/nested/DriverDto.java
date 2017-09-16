package com.example.nested;

public class DriverDto {

    private String name;
    private int age;
    private LicenseDto licenseDto;

    public DriverDto(String name, int age, LicenseDto licenseDto) {
        this.name = name;
        this.age = age;
        this.licenseDto = licenseDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LicenseDto getLicenseDto() {
        return licenseDto;
    }

    public void setLicenseDto(LicenseDto licenseDto) {
        this.licenseDto = licenseDto;
    }

    @Override
    public String toString() {
        return "DriverDto{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", licenseDto=" + licenseDto +
                '}';
    }
}
