package com.immortalcrab.nominator.dal;

import java.util.Optional;


public interface NominatorDao {

    public EmployeeDto createEmployee(
            final String name,
            final String surname,
            final String optionalSurname,
            final String identifier,
            final String orgName);

    public Optional<EmployeeDto> searchEmployee(String fullName);

    public OrganizationDto createOrganization(
            final String identifier,
            final String orgName,
            final String aka,
            final Integer regimen);

    public Optional<OrganizationDto> searchOrganization(String aka);
}
