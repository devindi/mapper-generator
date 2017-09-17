package com.example.default_mapping;

import com.devindi.mapper.DefaultValue;
import com.devindi.mapper.Mapper;

@Mapper
public interface PersonMapper {

    @DefaultValue("PersonDto.UNKNOWN")
    PersonDto toDto(Person model);
}
