package com.immortalcrab.nominator.dal.dao;

import java.util.Optional;

import com.immortalcrab.nominator.dal.dto.EmployeeDto;
import com.immortalcrab.nominator.dal.dto.OrganizationDto;

public interface NominatorDao {

        public EmployeeDto createEmployee(final EmployeeDto dto);

        public Optional<EmployeeDto> searchEmployee(String fullName);

        public OrganizationDto createOrganization(final OrganizationDto dto);

        public Optional<OrganizationDto> searchOrganization(String aka);
}
