package com.immortalcrab.nominator.dal;

public class EmployeeDto {

    private String orgName;
    private String identifier;
    private String Name;
    private String surname;
    private String optionalSurname;
    private final String nature = "PERSON";
    private String fullName;   

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getOptionalSurname() {
        return optionalSurname;
    }

    public void setOptionalSurname(String optionalSurname) {
        this.optionalSurname = optionalSurname;
    }

    public String getNature() {
        return nature;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}