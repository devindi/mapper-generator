package com.example.simple;

import org.junit.Test;

public class PersonMapperTest {

    @Test
    public void testMapping() {
        Person p = new Person(25, "Andrew");
        PersonMapper personMapper = new PersonMapperImpl();
        PersonDto personDto = personMapper.toDto(p);
        System.out.println("personDto = " + personDto);
    }

}
