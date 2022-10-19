package com.immortalcrab.nominator.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.immortalcrab.nominator.entities.Employee;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TargetDao {

    private final @NonNull
    DynamoDBMapper mapper;

    Employee createEmployee(final String name,
            final String surname,
            final String optionalSurname,
            final String identifier,
            final String org) {

        Employee target = new Employee();
        target.setName(name);
        target.setSurname(surname);
        target.setOptionalSurname(Optional.ofNullable(optionalSurname).orElse(""));
        target.setIdentifier(identifier);
        target.setOrg(org);
        mapper.save(target);

        return mapper.load(Employee.class, org, identifier);
    }

    public void deleteEmployee(final String issuer, final String identifier) {
        Employee target = new Employee();
        target.setOrg(issuer);
        target.setIdentifier(identifier);

        mapper.delete(target);
    }

    public List<Employee> getAllEmployees() {
        return mapper.scan(Employee.class, new DynamoDBScanExpression());
    }
}
