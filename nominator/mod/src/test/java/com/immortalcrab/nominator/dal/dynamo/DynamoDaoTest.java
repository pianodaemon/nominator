package com.immortalcrab.nominator.dal.dynamo;

import com.google.common.collect.ImmutableMap;
import com.immortalcrab.nominator.entities.Employee;
import com.immortalcrab.nominator.entities.Organization;
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

            Employee newerEmployee = _nominatorDao.createEmployee(
                    name, surname, optionalSurname, employeeIdentifier, orgName);

        Optional<Employee> target = _nominatorDao.searchEmployee(new StringSubstitutor(
                        ImmutableMap.of("v0", name, "v1", surname, "v2", optionalSurname))
                        .replace("${v0} #${v1} #${v2}"));

        assertTrue(target.isPresent() && newerEmployee.equals(target.get()));
    }

    @Test
    void createAndFindOrg() {

        final String orgName = "ORG#KACE8001104V0";
        final Integer regimen = 601;
        StringSubstitutor sub = new StringSubstitutor(ImmutableMap.of("org", orgName));
        final String orgIdentifier = sub.replace("${org}#PROF00");
        final String aka = "Immortal crab system incorporated";

        Organization newerOrg = _nominatorDao.createOrganization(orgIdentifier, orgName, aka, regimen);
        Optional<Organization> target = _nominatorDao.searchOrganization(aka);

        assertTrue(target.isPresent() && newerOrg.equals(target.get()));
    }

    @Test
    void createOrgWithEmployee() {

        final String orgName = "ORG#KACE8001104V0";
        final Integer regimen = 601;
        StringSubstitutor sub = new StringSubstitutor(ImmutableMap.of("org", orgName));
        final String orgIdentifier = sub.replace("${org}#PROF00");
        final String aka = "Immortal crab system incorporated";

        Organization newerOrg = _nominatorDao.createOrganization(orgIdentifier, orgName, aka, regimen);

        {
            final String name = "Edwin";
            final String surname = "Plauchu";
            final String optionalSurname = "Camacho";
            final String employeeIdentifier = "EMP#PACE8001104V2";

            Employee newerEmployee = _nominatorDao.createEmployee(
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
