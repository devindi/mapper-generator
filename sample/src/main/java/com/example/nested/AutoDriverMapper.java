package com.example.nested;

import com.devindi.mapper.Mapper;
import com.devindi.mapper.Mapping;

@Mapper
public interface AutoDriverMapper {
    @Mapping(source = "license", target = "licenseDto")
    DriverDto toDto(Driver d);
}
