package com.example;

import org.junit.Test;

import static org.junit.Assert.*;

public class PersonMapperTest {

    @Test
    public void testMapping() {
        Person p = new Person(25, "Andrew");
        PersonMapper personMapper = new PersonMapperImpl();
        PersonDto personDto = personMapper.toDto(p);
        System.out.println("personDto = " + personDto);
    }

}
