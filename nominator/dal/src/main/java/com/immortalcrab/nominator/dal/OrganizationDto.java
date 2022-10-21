package com.immortalcrab.nominator.dal;

import lombok.Getter;

@Getter
public class OrganizationDto {

    private String orgName;

    private String identifier;

    private Integer regimen;

    private final String nature = "ORGANIZATION";

    private String aka;

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setRegimen(Integer regimen) {
        this.regimen = regimen;
    }

    public void setAka(String aka) {
        this.aka = aka;
    }
}