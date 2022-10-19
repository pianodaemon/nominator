package com.immortalcrab.nominator.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.immortalcrab.nominator.entities.Employee;
import com.immortalcrab.nominator.entities.Organization;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DynamoDBNominatorDao {

    private final @NonNull
    DynamoDBMapper mapper;

    public Employee createEmployee(final String name,
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

    public void deleteEmployee(final String issuer, final String identifier) {
        Employee target = new Employee();
        target.setOrgName(issuer);
        target.setIdentifier(identifier);

        mapper.delete(target);
    }

    public List<Employee> getAllEmployees() {
        return mapper.scan(Employee.class, new DynamoDBScanExpression());
    }

    public Organization createOrganization(final String identifier, final String orgName) {

        Organization target = new Organization();
        target.setIdentifier(identifier);
        target.setOrgName(orgName);
        mapper.save(target);

        return mapper.load(Organization.class, orgName, identifier);
    }
}
