# mapper-generator
An annotation processor for generating bean mappers

## IDE support
This annotation processor is supported by Android Studio out-of-box.  
You have to set-up processor and generated code usage in your IntelliJ IDEA project.


## Usage
This processor generates mapper from getters to constructor args.

```java
//gradle deps (don't forget to apply 'net.ltgt.apt' or other annotation plugin):
dependencies {
    compile 'com.devindi.mapper:library:0.1'
    apt 'com.devindi.mapper:processor:0.1.1'
}

//Source:   
public class PersonDto {

    private String name;
    private int age;

    //constructor, setters or other methods

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

//Target:
public class Person {

    private final String name;
    private final int age;

    public Person(int age, String name) {
        this.name = name;
        this.age = age;
    }
}

//Mapper definition:
@Mapper
public interface PersonMapper {
    Person toModel(PersonDto dto);
}

//Generated code:
public class PersonMapperImpl implements PersonMapper {
  @Override
  public Person toModel(PersonDto dto) {
    return new com.example.Person(dto.getAge(),dto.getName());
  }
}
```


## ToDo list

* Type converters (String <-> Long, Double <-> Float, custom converters)
* Collection mapping (List<Source> -> List<Target>)
* Recursive mapping 


