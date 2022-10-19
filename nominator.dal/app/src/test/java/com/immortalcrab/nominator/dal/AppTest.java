package com.immortalcrab.nominator.dal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.immortalcrab.nominator.entities.Target;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    static DynamoDBMapper _dynamoDBMapper;
    static AmazonDynamoDB _dynamoDB;
    static TargetDao _bdao;

    @BeforeAll
    public static void before() {
        _dynamoDB = DynamoDBEmbedded.create().amazonDynamoDB();
        _dynamoDBMapper = new DynamoDBMapper(_dynamoDB);
        Util.createTables(_dynamoDBMapper, _dynamoDB);
        _bdao = new TargetDao(new DynamoDBMapper(_dynamoDB));
    }

    @Test
    void createAndRetreiveEmployee() {

        final String useraName = "Edwin Plauchu Camacho";
        final String identifier = "EMP#PACE8001104V2";
        final String issuer = "ORG#KACE8001104V0";

        Target target = new Target();
        target.setUserName(useraName);
        target.setIdentifier(identifier);
        target.setIssuer(issuer);
        _bdao.put(target);

        Target newerEmployee = _bdao.get(issuer, identifier);
        System.out.println(newerEmployee.getIdentifier());
        assertTrue(target.equals(newerEmployee));
    }
}
