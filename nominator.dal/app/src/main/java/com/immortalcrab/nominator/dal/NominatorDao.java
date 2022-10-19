package com.immortalcrab.nominator.dal;

import com.immortalcrab.nominator.entities.Employee;
import com.immortalcrab.nominator.entities.Organization;

public interface NominatorDao {

    public Employee createEmployee(
            final String name,
            final String surname,
            final String optionalSurname,
            final String identifier,
            final String orgName);

    public Organization createOrganization(
            final String identifier,
            final String orgName);
}
