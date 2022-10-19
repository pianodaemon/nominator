package com.immortalcrab.nominator.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.immortalcrab.nominator.entities.Target;
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

    Target createEmployee(final String name,
            final String surname,
            final String optionalSurname,
            final String identifier,
            final String org) {

        Target target = new Target();
        target.setName(name);
        target.setSurname(surname);
        target.setOptionalSurname(Optional.ofNullable(optionalSurname).orElse(""));
        target.setIdentifier(identifier);
        target.setOrg(org);
        mapper.save(target);

        return mapper.load(Target.class, org, identifier);
    }

    public void deleteEmployee(final String issuer, final String identifier) {
        Target target = new Target();
        target.setOrg(issuer);
        target.setIdentifier(identifier);

        mapper.delete(target);
    }

    public List<Target> getAllEmployees() {
        return mapper.scan(Target.class, new DynamoDBScanExpression());
    }
}
