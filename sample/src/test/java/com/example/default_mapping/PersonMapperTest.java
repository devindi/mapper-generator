package com.example.default_mapping;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonMapperTest {
    @Test
    public void defaultMappingTest() throws Exception {

        PersonMapper mapper = new PersonMapperImpl();
        PersonDto personDto = mapper.toDto(null);
        Assert.assertSame(PersonDto.UNKNOWN, personDto);
    }

}