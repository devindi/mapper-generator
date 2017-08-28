package com.example;


public class Main {

    public static void main(String[] args) {
        PersonMapper personMapper = new PersonMapperImpl();
        PersonDto dto = personMapper.toDto(new Person(25, "Andrew"));
        System.out.println("dto = " + dto);
    }
}
