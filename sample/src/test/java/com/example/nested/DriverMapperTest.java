package com.example.nested;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DriverMapperTest {

    @Test
    public void testNestedMapping() {
        Date date = new Date();
        Driver d = new Driver(25, "John Doe", new DriverLicense("42", null, date));
        DriverMapperImpl driverMapper = new DriverMapperImpl();
        DriverDto driverDto = driverMapper.toDto(d);
        assertEquals(25, driverDto.getAge());
        assertEquals("John Doe", driverDto.getName());
        assertEquals("42", driverDto.getLicenseDto().getId());
        assertNull(driverDto.getLicenseDto().getPhotoUrl());
        assertEquals(date, driverDto.getLicenseDto().getValidUntil());
    }
}