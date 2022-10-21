package com.immortalcrab.nominator.dal.dynamo.helpers;

import com.immortalcrab.nominator.dal.EmployeeDto;
import com.immortalcrab.nominator.dal.OrganizationDto;
import com.immortalcrab.nominator.entities.Employee;
import com.immortalcrab.nominator.entities.Organization;

public class DataTransferObjectTranslator {

    public static OrganizationDto render(final Organization origin) {

        OrganizationDto dot = new OrganizationDto();

        dot.setIdentifier(origin.getIdentifier());
        dot.setOrgName(origin.getOrgName());
        dot.setRegimen(origin.getRegimen());
        dot.setAka(origin.getAka());
        return dot;
    }

    public static EmployeeDto render(final Employee origin) {

        EmployeeDto dot = new EmployeeDto();

        dot.setName(origin.getName());
        dot.setSurname(origin.getSurname());
        dot.setOptionalSurname(origin.getOptionalSurname());
        dot.setIdentifier(origin.getIdentifier());
        dot.setOrgName(origin.getOrgName());
        dot.setFullName(origin.getFullName());

        return dot;
    }
}
