package com.immortalcrab.nominator.dal.dao.dynamo;

import com.immortalcrab.nominator.dal.dto.EmployeeDto;
import com.immortalcrab.nominator.dal.dto.OrganizationDto;
import com.immortalcrab.nominator.dal.entities.dynamo.Employee;
import com.immortalcrab.nominator.dal.entities.dynamo.Organization;

public class DataTransferObjectConverter {

    public static OrganizationDto basic(final Organization origin) {

        OrganizationDto dot = new OrganizationDto();

        dot.setIdentifier(origin.getIdentifier());
        dot.setOrgName(origin.getOrgName());
        dot.setRegimen(origin.getRegimen());
        dot.setAka(origin.getAka());
        dot.setNature(origin.getNature());
        return dot;
    }

    public static EmployeeDto basic(final Employee origin) {

        EmployeeDto dot = new EmployeeDto();

        dot.setName(origin.getName());
        dot.setSurname(origin.getSurname());
        dot.setOptionalSurname(origin.getOptionalSurname());
        dot.setIdentifier(origin.getIdentifier());
        dot.setCurp(origin.getCurp());
        dot.setOrgName(origin.getOrgName());
        dot.setFullName(origin.getFullName());
        dot.setNature(origin.getNature());

        return dot;
    }
}
