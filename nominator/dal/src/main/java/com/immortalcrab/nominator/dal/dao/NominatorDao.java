package com.immortalcrab.nominator.dal.dao;

import java.util.Optional;

import com.immortalcrab.nominator.dal.dto.EmployeeDto;
import com.immortalcrab.nominator.dal.dto.OrganizationDto;


public interface NominatorDao {

    public EmployeeDto createEmployee(
            final String name,
            final String surname,
            final String optionalSurname,
            final String identifier,
            final String curp,
            final String orgName);

    public Optional<EmployeeDto> searchEmployee(String fullName);

    public OrganizationDto createOrganization(
            final String identifier,
            final String orgName,
            final String aka,
            final Integer regimen);

    public Optional<OrganizationDto> searchOrganization(String aka);
}
