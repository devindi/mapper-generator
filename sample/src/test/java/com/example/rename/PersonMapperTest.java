package com.example.rename;

import org.junit.Assert;
import org.junit.Test;

public class PersonMapperTest {

    @Test
    public void testMapping() {
        Person p = new Person(26, "Andrew");
        PersonMapper personMapper = new PersonMapperImpl();
        PersonDto personDto = personMapper.toDto(p);
        System.out.println("personDto = " + personDto);
        Person mapped = personMapper.toModel(personDto);
        Assert.assertEquals(p, mapped);
    }

}