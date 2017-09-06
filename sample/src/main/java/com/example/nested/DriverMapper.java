package com.example.nested;

import com.devindi.mapper.Mapper;
import com.devindi.mapper.Mapping;

@Mapper
public interface DriverMapper {

    @Mapping(source = "license", target = "licenseDto")
    DriverDto toDto(Driver d);

    LicenseDto toDto(DriverLicense license);
}
