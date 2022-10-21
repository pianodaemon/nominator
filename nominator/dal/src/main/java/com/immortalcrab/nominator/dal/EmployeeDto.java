package com.immortalcrab.nominator.dal;

import lombok.Getter;

@Getter
public class EmployeeDto {

    private String orgName;
    private String identifier;
    private String Name;
    private String surname;
    private String optionalSurname;
    private final String nature = "PERSON";
    private String fullName;   

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setOptionalSurname(String optionalSurname) {
        this.optionalSurname = optionalSurname;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}