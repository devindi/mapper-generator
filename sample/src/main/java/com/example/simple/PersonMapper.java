package com.example.simple;

import com.devindi.mapper.Mapper;

@Mapper
public interface PersonMapper {

    PersonDto toDto(Person model);

    Person toModel(PersonDto dto);
}
