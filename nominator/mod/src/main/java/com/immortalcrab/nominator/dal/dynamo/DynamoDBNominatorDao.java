package com.immortalcrab.nominator.dal.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.immortalcrab.nominator.dal.NominatorDao;
import com.immortalcrab.nominator.entities.Employee;
import com.immortalcrab.nominator.entities.Organization;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DynamoDBNominatorDao implements NominatorDao {

    private final @NonNull DynamoDBMapper mapper;

    public Employee createEmployee(
            final String name,
            final String surname,
            final String optionalSurname,
            final String identifier,
            final String orgName) {

        Employee target = new Employee();
        target.setName(name);
        target.setSurname(surname);
        target.setOptionalSurname(Optional.ofNullable(optionalSurname).orElse(""));
        target.setIdentifier(identifier);
        target.setOrgName(orgName);
        mapper.save(target);

        return mapper.load(Employee.class, orgName, identifier);
    }

    @Override
    public Optional<Employee> searchEmployee(String fullName) {
        // TODO Auto-generated method stub
        return Optional.empty();
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
    public Organization createOrganization(
            final String identifier,
            final String orgName,
            final String aka,
            final Integer regimen) {

        final String nature = "ORGANIZATION";

        Organization target = new Organization();
        target.setIdentifier(identifier);
        target.setOrgName(orgName);
        target.setRegimen(regimen);
        target.setNature(nature);
        target.setAka(aka);
        mapper.save(target);

        return mapper.load(Organization.class, orgName, identifier);
    }

    @Override
    public Optional<Organization> searchOrganization(String aka) {

        final String nature = "ORGANIZATION";

        DynamoDBQueryExpression<Organization> qe = new DynamoDBQueryExpression<Organization>();

        Organization target = new Organization();
        target.setNature(nature);
        target.setAka(aka);

        qe.withHashKeyValues(target).withConsistentRead(false);
        PaginatedQueryList<Organization> rl = mapper.query(Organization.class, qe);

        return Optional.ofNullable(rl.isEmpty() ? null : rl.get(0));
    }
}
