package com.immortalcrab.nominator.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.immortalcrab.nominator.entities.Target;
import java.util.List;
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
            final String issuer) {

        Target target = new Target();
        target.setName(name);
        target.setSurname(surname);
        target.setOptionalSurname(optionalSurname);
        target.setIdentifier(identifier);
        target.setIssuer(issuer);
        mapper.save(target);

        return mapper.load(Target.class, issuer, identifier);
    }

    public void delete(final String issuer, final String identifier) {
        Target target = new Target();
        target.setIssuer(issuer);
        target.setIdentifier(identifier);

        mapper.delete(target);
    }

    public List<Target> getAll() {
        return mapper.scan(Target.class, new DynamoDBScanExpression());
    }
}
