package com.example.rename;

public class PersonResponse {

    private String name_string;
    private int age_int;

    public PersonResponse(String name_string, int age_int) {
        this.name_string = name_string;
        this.age_int = age_int;
    }

    public String getName_string() {
        return name_string;
    }

    public int getAge_int() {
        return age_int;
    }
}
