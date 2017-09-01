package com.example.rename;

public class Person {

    private final String fullName;
    private final int age;

    public Person(int age, String fullName) {
        this.fullName = fullName;
        this.age = age;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (age != person.age) return false;
        return fullName.equals(person.fullName);

    }

    @Override
    public int hashCode() {
        int result = fullName.hashCode();
        result = 31 * result + age;
        return result;
    }
}
