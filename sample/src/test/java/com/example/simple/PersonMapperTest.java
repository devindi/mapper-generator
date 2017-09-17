package com.example.simple;

import org.junit.Assert;
import org.junit.Test;

public class PersonMapperTest {

    @Test
    public void testMapping() {
        Person p = new Person(25, "Andrew");
        PersonMapper personMapper = new PersonMapperImpl();
        PersonDto personDto = personMapper.toDto(p);
        Assert.assertEquals(25, personDto.getAge());
        Assert.assertEquals("Andrew", personDto.getName());
    }

    @Test
    public void testNullMapping() {
        PersonMapper personMapper = new PersonMapperImpl();
        PersonDto personDto = personMapper.toDto(null);
        Assert.assertNull(personDto);
    }

}
