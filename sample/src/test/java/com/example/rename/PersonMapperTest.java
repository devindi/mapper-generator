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

    @Test
    public void testMultipleMapping() {
        PersonResponse response = new PersonResponse("User", 42);
        PersonMapper personMapper = new PersonMapperImpl();
        Person person = personMapper.toModel(response);
        Assert.assertEquals("User", person.getFullName());
        Assert.assertEquals(42, person.getAge());
    }

}