package com.immortalcrab.nominator.dal;

import java.util.Optional;

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
            final String orgName,
            final String aka,
            final Integer regimen);

    public Optional<Organization> searchOrganization(String aka);
}
