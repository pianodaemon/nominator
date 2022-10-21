package com.immortalcrab.nominator.dal.dynamo;

import com.google.common.collect.ImmutableMap;
import com.immortalcrab.nominator.entities.Employee;
import com.immortalcrab.nominator.entities.Organization;
import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DynamoDaoTest extends PillarDynamoDBDaoTest {

    @Test
    void createAndFindOrg() {

        final String orgName = "ORG#KACE8001104V0";
        final Integer regimen = 601;
        StringSubstitutor sub = new StringSubstitutor(ImmutableMap.of("org", orgName));
        final String orgIdentifier = sub.replace("${org}#PROF00");
        final String aka = "Immortal crab system incorporated";

        Organization newerOrg = _nominatorDao.createOrganization(orgIdentifier, orgName, aka, regimen);
        Organization target = _nominatorDao.searchOrganization(aka);

        assertTrue(newerOrg.equals(target));
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

            Employee newerEmployee = _nominatorDao.createEmployee(name, surname, optionalSurname, employeeIdentifier, newerOrg.getOrgName());
            assertTrue(newerEmployee.getOrgName().equals(newerOrg.getOrgName()));
            assertTrue(newerEmployee.getIdentifier().equals(employeeIdentifier));
            assertTrue(newerEmployee.getName().equals(name));
            assertTrue(newerEmployee.getSurname().equals(surname));
            assertTrue(newerEmployee.getOptionalSurname().equals(optionalSurname));
        }

        assertTrue(newerOrg.getOrgName().equals(orgName));
        assertTrue(newerOrg.getIdentifier().equals(orgIdentifier));
        assertTrue(newerOrg.getRegimen().equals(regimen));
    }
}
