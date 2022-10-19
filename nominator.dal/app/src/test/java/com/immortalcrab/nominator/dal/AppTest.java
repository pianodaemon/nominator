package com.immortalcrab.nominator.dal;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.common.collect.ImmutableMap;
import com.immortalcrab.nominator.entities.Employee;
import com.immortalcrab.nominator.entities.Organization;
import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    static DynamoDBMapper _dynamoDBMapper;
    static AmazonDynamoDB _dynamoDB;
    static TargetDao _bdao;
    static DynamoDBProxyServer _server;

    @BeforeAll
    public static void before() {
        runDynamoDB();
        _dynamoDB = createAmazonDynamoDBClient();
        _dynamoDBMapper = new DynamoDBMapper(_dynamoDB);
        Util.createTables(_dynamoDBMapper, _dynamoDB);
        _bdao = new TargetDao(new DynamoDBMapper(_dynamoDB));
    }

    public static void runDynamoDB() {

        //Need to set the SQLite4Java library path to avoid a linker error
        System.setProperty("sqlite4java.library.path", "./build/libs/");

        // Create an in-memory and in-process instance of DynamoDB Local that runs over HTTP
        final String[] localArgs = {"-inMemory"};

        try {
            _server = ServerRunner.createServerFromCommandLineArgs(localArgs);
            _server.start();

        } catch (Exception e) {
            e.printStackTrace();
            //Assert.fail(e.getMessage());
            //return;
        }
    }

    @AfterAll
    public static void shutdownDynamoDB() {
        if (_server != null) {
            try {
                _server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static AmazonDynamoDB createAmazonDynamoDBClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", Regions.US_EAST_2.getName()))
                .build();
    }

    @Test
    void createOrgWithEmployee() {

        final String orgName = "ORG#KACE8001104V0";
        StringSubstitutor sub = new StringSubstitutor(ImmutableMap.of("org", orgName));
        Organization newerOrg = _bdao.createOrganization(sub.replace("${org}#0001"), orgName);

        {
            final String name = "Edwin";
            final String surname = "Plauchu";
            final String optionalSurname = "Camacho";
            final String employeeIdentifier = "EMP#PACE8001104V2";

            Employee newerEmployee = _bdao.createEmployee(name, surname, optionalSurname, employeeIdentifier, newerOrg.getOrgName());
            boolean result = newerEmployee.getOrgName().equals(newerOrg.getOrgName())
                    && newerEmployee.getIdentifier().equals(employeeIdentifier)
                    && newerEmployee.getName().equals(name)
                    && newerEmployee.getSurname().equals(surname)
                    && newerEmployee.getOptionalSurname().equals(optionalSurname);

            assertTrue(result);
        }

    }
}
