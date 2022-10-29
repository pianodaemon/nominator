package com.immortalcrab.nominator.dal.dao.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.google.common.collect.ImmutableMap;
import com.immortalcrab.nominator.dal.dao.NominatorDao;
import com.immortalcrab.nominator.dal.dto.EmployeeDto;
import com.immortalcrab.nominator.dal.dto.OrganizationDto;
import com.immortalcrab.nominator.dal.entities.dynamo.Employee;
import com.immortalcrab.nominator.dal.entities.dynamo.Organization;

import java.util.List;
import java.util.Optional;

import org.apache.commons.text.StringSubstitutor;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@RequiredArgsConstructor
@Getter
@CommonsLog
public class DynamoDBNominatorDao implements NominatorDao {

    protected enum Nature {
        ORGANIZATION,
        PERSON,
        HIGH
    }

    private final @NonNull DynamoDBMapper mapper;

    public EmployeeDto createEmployee(
            final String name,
            final String surname,
            final String optionalSurname,
            final String identifier,
            final String curp,
            final String imss,
            final String orgName) {

        final String nature = Nature.PERSON.name();

        Employee target = new Employee();
        target.setName(name);
        target.setSurname(surname);
        target.setOptionalSurname(Optional.ofNullable(optionalSurname).orElse(""));
        target.setIdentifier(identifier);
        target.setCurp(curp);
        target.setImss(imss);
        target.setOrgName(orgName);
        target.setNature(nature);

        // Full name's formation
        {
            StringSubstitutor sub = new StringSubstitutor(
                    ImmutableMap.of("v0", target.getName(),
                            "v1", target.getSurname(),
                            "v2", target.getOptionalSurname()));
            target.setFullName(sub.replace("${v0} #${v1} #${v2}"));
        }

        mapper.save(target);
        log.info("Employee " + target.toString() + " has been created");

        return DataTransferObjectConverter.basic(mapper.load(Employee.class, orgName, identifier));
    }

    @Override
    public Optional<EmployeeDto> searchEmployee(String fullName) {

        final String nature = Nature.PERSON.name();
        DynamoDBQueryExpression<Employee> qe = new DynamoDBQueryExpression<Employee>();

        Employee target = new Employee();
        target.setNature(nature);
        target.setFullName(fullName);

        qe.withHashKeyValues(target).withConsistentRead(false);
        PaginatedQueryList<Employee> rl = mapper.query(Employee.class, qe);

        return Optional.ofNullable(rl.isEmpty() ? null : DataTransferObjectConverter.basic(rl.get(0)));
    }


    public void deleteEmployee(final String issuer, final String identifier) {
        Employee target = new Employee();
        target.setOrgName(issuer);
        target.setIdentifier(identifier);

        mapper.delete(target);
    }

    public List<Employee> getAllEmployees() {
        return mapper.scan(Employee.class, new DynamoDBScanExpression());
    }

    @Override
    public OrganizationDto createOrganization(
            final String identifier,
            final String orgName,
            final String aka,
            final Integer regimen,
            final String regimenEmployer) {

        final String nature = Nature.ORGANIZATION.name();

        Organization target = new Organization();
        target.setIdentifier(identifier);
        target.setOrgName(orgName);
        target.setRegimen(regimen);
        target.setRegimenEmployer(regimenEmployer);
        target.setNature(nature);
        target.setAka(aka);
        mapper.save(target);

        return DataTransferObjectConverter.basic(mapper.load(Organization.class, orgName, identifier));
    }

    @Override
    public Optional<OrganizationDto> searchOrganization(String aka) {

        final String nature = Nature.ORGANIZATION.name();

        DynamoDBQueryExpression<Organization> qe = new DynamoDBQueryExpression<Organization>();

        Organization target = new Organization();
        target.setNature(nature);
        target.setAka(aka);

        qe.withHashKeyValues(target).withConsistentRead(false);
        PaginatedQueryList<Organization> rl = mapper.query(Organization.class, qe);

        return Optional.ofNullable(rl.isEmpty() ? null : DataTransferObjectConverter.basic(rl.get(0)));
    }
}
