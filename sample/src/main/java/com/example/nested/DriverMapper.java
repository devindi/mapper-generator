package com.example.nested;

import com.devindi.mapper.Mapper;

@Mapper
public interface DriverMapper {

    DriverDto toDto(Driver d);
}
