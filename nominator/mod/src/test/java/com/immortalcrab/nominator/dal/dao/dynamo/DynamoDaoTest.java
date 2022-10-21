package com.immortalcrab.nominator.dal.dynamo;

import com.google.common.collect.ImmutableMap;
import com.immortalcrab.nominator.dal.dto.EmployeeDto;
import com.immortalcrab.nominator.dal.dto.OrganizationDto;

import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class DynamoDaoTest extends PillarDynamoDBDaoTest {

    @Test
    void createAndFindEmployee() {

        final String orgName = "ORG#KACE8001104V0";
        final String name = "Edwin";
        final String surname = "Plauchu";
        final String optionalSurname = "Camacho";
        final String employeeIdentifier = "EMP#PACE8001104V2";

        _nominatorDao.createEmployee(
                name, surname, optionalSurname, employeeIdentifier, orgName);

        Optional<EmployeeDto> dto = _nominatorDao.searchEmployee(new StringSubstitutor(
                ImmutableMap.of("v0", name, "v1", surname, "v2", optionalSurname))
                .replace("${v0} #${v1} #${v2}"));

        assertTrue(dto.isPresent());
        assertTrue(dto.get().getOrgName().equals(orgName));
        assertTrue(dto.get().getIdentifier().equals(employeeIdentifier));
        assertTrue(dto.get().getName().equals(name));
        assertTrue(dto.get().getSurname().equals(surname));
        assertTrue(dto.get().getOptionalSurname().equals(optionalSurname));

        // Verification of the full name's formation
        {
            assertTrue(dto.get().getFullName().equals(new StringSubstitutor(
                    ImmutableMap.of("v0", name, "v1", surname, "v2", optionalSurname))
                    .replace("${v0} #${v1} #${v2}")));
        }
    }

    @Test
    void createAndFindOrg() {

        final String orgName = "ORG#KACE8001104V0";
        final Integer regimen = 601;
        StringSubstitutor sub = new StringSubstitutor(ImmutableMap.of("org", orgName));
        final String orgIdentifier = sub.replace("${org}#PROF00");
        final String aka = "Immortal crab system incorporated";

        _nominatorDao.createOrganization(orgIdentifier, orgName, aka, regimen);
        Optional<OrganizationDto> dto = _nominatorDao.searchOrganization(aka);

        assertTrue(dto.isPresent());
        assertTrue(dto.get().getOrgName().equals(orgName));
        assertTrue(dto.get().getIdentifier().equals(orgIdentifier));
        assertTrue(dto.get().getRegimen().equals(regimen));
        assertTrue(dto.get().getAka().equals(aka));
    }

    @Test
    void createOrgWithEmployee() {

        final String orgName = "ORG#KACE8001104V0";
        final Integer regimen = 601;
        StringSubstitutor sub = new StringSubstitutor(ImmutableMap.of("org", orgName));
        final String orgIdentifier = sub.replace("${org}#PROF00");
        final String aka = "Immortal crab system incorporated";

        OrganizationDto newerOrg = _nominatorDao.createOrganization(orgIdentifier, orgName, aka, regimen);

        {
            final String name = "Edwin";
            final String surname = "Plauchu";
            final String optionalSurname = "Camacho";
            final String employeeIdentifier = "EMP#PACE8001104V2";

            EmployeeDto newerEmployee = _nominatorDao.createEmployee(
                    name, surname, optionalSurname, employeeIdentifier, newerOrg.getOrgName());
            assertTrue(newerEmployee.getOrgName().equals(newerOrg.getOrgName()));
            assertTrue(newerEmployee.getIdentifier().equals(employeeIdentifier));
            assertTrue(newerEmployee.getName().equals(name));
            assertTrue(newerEmployee.getSurname().equals(surname));
            assertTrue(newerEmployee.getOptionalSurname().equals(optionalSurname));

            // Verification of the full name's formation
            {
                assertTrue(newerEmployee.getFullName().equals(new StringSubstitutor(
                        ImmutableMap.of("v0", name, "v1", surname, "v2", optionalSurname))
                        .replace("${v0} #${v1} #${v2}")));
            }
        }

        assertTrue(newerOrg.getOrgName().equals(orgName));
        assertTrue(newerOrg.getIdentifier().equals(orgIdentifier));
        assertTrue(newerOrg.getRegimen().equals(regimen));
    }
}
