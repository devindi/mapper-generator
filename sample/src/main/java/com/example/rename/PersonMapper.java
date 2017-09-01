package com.example.rename;

import com.devindi.mapper.Mapper;
import com.devindi.mapper.Mapping;

@Mapper
public interface PersonMapper {
    @Mapping(source = "fullName", target = "name")
    PersonDto toDto(Person model);
    @Mapping(source = "name", target = "fullName")
    Person toModel(PersonDto personDto);
}
