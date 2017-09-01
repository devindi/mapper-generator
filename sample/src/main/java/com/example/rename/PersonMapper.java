package com.example.rename;

import com.devindi.mapper.Mapper;
import com.devindi.mapper.Mapping;
import com.devindi.mapper.Mappings;

@Mapper
public interface PersonMapper {
    @Mapping(source = "fullName", target = "name")
    PersonDto toDto(Person model);
    @Mapping(source = "name", target = "fullName")
    Person toModel(PersonDto personDto);

    @Mappings({
            @Mapping(source = "name_string", target = "fullName"),
            @Mapping(source = "age_int", target = "age")
    })
    Person toModel(PersonResponse response);
}
