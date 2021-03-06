package com.example.nested;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DriverMapperTest {

    @Test
    public void testNestedMapping() {
        Date date = new Date();
        Driver d = new Driver(25, "John Doe", new DriverLicense("42", new Photo("https://google.com", date), date), new Photo("https://google.com", date));
        DriverMapperImpl driverMapper = new DriverMapperImpl();
        DriverDto driverDto = driverMapper.toDto(d);
        assertEquals(25, driverDto.getAge());
        assertEquals("John Doe", driverDto.getName());
        assertEquals("42", driverDto.getLicenseDto().getId());
        assertEquals(date, driverDto.getLicenseDto().getPhoto().getCreatedAt());
        assertEquals("https://google.com", driverDto.getLicenseDto().getPhoto().getUrl());
        assertEquals(date, driverDto.getLicenseDto().getValidUntil());
    }

    @Test
    public void testAutoMapping() {
        Date date = new Date();
        Driver d = new Driver(25, "John Doe", new DriverLicense("42", new Photo("https://google.com", date), date), new Photo("https://google.com", date));
        AutoDriverMapper driverMapper = new AutoDriverMapperImpl();
        DriverDto driverDto = driverMapper.toDto(d);
        assertEquals(25, driverDto.getAge());
        assertEquals("John Doe", driverDto.getName());
        assertEquals("42", driverDto.getLicenseDto().getId());
        assertEquals(date, driverDto.getLicenseDto().getPhoto().getCreatedAt());
        assertEquals("https://google.com", driverDto.getLicenseDto().getPhoto().getUrl());
        assertEquals(date, driverDto.getLicenseDto().getValidUntil());
    }
}