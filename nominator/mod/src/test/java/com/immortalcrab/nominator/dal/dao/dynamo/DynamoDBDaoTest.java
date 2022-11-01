package com.immortalcrab.nominator.dal.dao.dynamo;

import com.google.common.collect.ImmutableMap;
import com.immortalcrab.nominator.dal.dto.EmployeeDto;
import com.immortalcrab.nominator.dal.dto.OrganizationDto;

import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class DynamoDBDaoTest extends PillarDynamoDBDaoTest {

    @Test
    void createAndFindEmployee() {

        EmployeeDto req = new EmployeeDto();
        req.setName("Edwin");
        req.setSurname("Plauchu");
        req.setOptionalSurname("Camacho");
        req.setIdentifier("EMP#PACE8001104V2");
        req.setImss("0f9j0f9j9fj3049jf0");
        req.setCurp("OEAF771012HMCRGR09");
        req.setOrgName("ORG#KACE8001104V0");

        _nominatorDao.createEmployee(req);

        Optional<EmployeeDto> dto = _nominatorDao.searchEmployee(new StringSubstitutor(
                ImmutableMap.of(
                        "v0", req.getName(),
                        "v1", req.getSurname(),
                        "v2", req.getOptionalSurname()))
                .replace("${v0} #${v1} #${v2}"));

        assertTrue(dto.isPresent());
        assertTrue(dto.get().getOrgName().equals(req.getOrgName()));
        assertTrue(dto.get().getIdentifier().equals(req.getIdentifier()));
        assertTrue(dto.get().getCurp().equals(req.getCurp()));
        assertTrue(dto.get().getName().equals(req.getName()));
        assertTrue(dto.get().getSurname().equals(req.getSurname()));
        assertTrue(dto.get().getOptionalSurname().equals(req.getOptionalSurname()));

        // Verification of the full name's formation
        {
            assertTrue(dto.get().getFullName().equals(new StringSubstitutor(
                    ImmutableMap.of(
                            "v0", req.getName(),
                            "v1", req.getSurname(),
                            "v2", req.getOptionalSurname()))
                    .replace("${v0} #${v1} #${v2}")));
        }
    }

    @Test
    void createAndFindOrg() {

        final String orgName = "ORG#KACE8001104V0";
        final Integer regimen = 601;
        final String regimenEmployer = "D4562908100";
        StringSubstitutor sub = new StringSubstitutor(ImmutableMap.of("org", orgName));
        final String orgIdentifier = sub.replace("${org}#PROF00");
        final String aka = "Immortal crab system incorporated";

        _nominatorDao.createOrganization(orgIdentifier, orgName, aka, regimen, regimenEmployer);
        Optional<OrganizationDto> dto = _nominatorDao.searchOrganization(aka);

        assertTrue(dto.isPresent());
        assertTrue(dto.get().getOrgName().equals(orgName));
        assertTrue(dto.get().getIdentifier().equals(orgIdentifier));
        assertTrue(dto.get().getRegimen().equals(regimen));
        assertTrue(dto.get().getRegimenEmployer().equals(regimenEmployer));
        assertTrue(dto.get().getAka().equals(aka));
    }

    @Test
    void createOrgWithEmployee() {

        final String orgName = "ORG#KACE8001104V0";
        final Integer regimen = 601;
        final String regimenEmployer = "D4562908100";
        StringSubstitutor sub = new StringSubstitutor(ImmutableMap.of("org", orgName));
        final String orgIdentifier = sub.replace("${org}#PROF00");
        final String aka = "Immortal crab system incorporated";

        OrganizationDto newerOrg = _nominatorDao.createOrganization(orgIdentifier, orgName, aka, regimen,
                regimenEmployer);

        {
            EmployeeDto req = new EmployeeDto();
            req.setName("Edwin");
            req.setSurname("Plauchu");
            req.setOptionalSurname("Camacho");
            req.setIdentifier("EMP#PACE8001104V2");
            req.setImss("0f9j0f9j9fj3049jf0");
            req.setCurp("OEAF771012HMCRGR09");
            req.setOrgName(newerOrg.getOrgName());

            EmployeeDto newerEmployee = _nominatorDao.createEmployee(req);

            assertTrue(newerEmployee.getOrgName().equals(newerOrg.getOrgName()));
            assertTrue(newerEmployee.getIdentifier().equals(req.getIdentifier()));
            assertTrue(newerEmployee.getCurp().equals(req.getCurp()));
            assertTrue(newerEmployee.getImss().equals(req.getImss()));
            assertTrue(newerEmployee.getName().equals(req.getName()));
            assertTrue(newerEmployee.getSurname().equals(req.getSurname()));
            assertTrue(newerEmployee.getOptionalSurname().equals(req.getOptionalSurname()));

            // Verification of the full name's formation
            {
                assertTrue(newerEmployee.getFullName().equals(new StringSubstitutor(
                        ImmutableMap.of(
                                "v0", req.getName(),
                                "v1", req.getSurname(),
                                "v2", req.getOptionalSurname()))
                        .replace("${v0} #${v1} #${v2}")));
            }
        }

        assertTrue(newerOrg.getOrgName().equals(orgName));
        assertTrue(newerOrg.getIdentifier().equals(orgIdentifier));
        assertTrue(newerOrg.getRegimen().equals(regimen));
        assertTrue(newerOrg.getRegimenEmployer().equals(regimenEmployer));
    }
}
