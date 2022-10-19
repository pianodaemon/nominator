package com.immortalcrab.nominator.dal;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

class DalTest {

    static DynamoDBMapper _dynamoDBMapper;
    static AmazonDynamoDB _dynamoDB;
    static ServerLessDao _bdao;
    static DynamoDBProxyServer _server;

    @BeforeAll
    public static void before() {
        runDynamoDB();
        _dynamoDB = createAmazonDynamoDBClient();
        _dynamoDBMapper = new DynamoDBMapper(_dynamoDB);
        Util.createTables(_dynamoDBMapper, _dynamoDB);
        _bdao = new ServerLessDao(new DynamoDBMapper(_dynamoDB));
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

    private static AmazonDynamoDB createAmazonDynamoDBClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", Regions.US_EAST_2.getName()))
                .build();
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
}
