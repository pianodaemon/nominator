package com.immortalcrab.nominator.dal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationDto {

    private String orgName;
    private String identifier;
    private Integer regimen;
    private String nature;
    private String aka;
}