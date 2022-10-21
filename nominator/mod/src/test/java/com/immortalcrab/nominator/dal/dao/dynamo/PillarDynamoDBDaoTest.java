package com.immortalcrab.nominator.dal.dao.dynamo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.immortalcrab.nominator.dal.dao.NominatorDao;
import com.immortalcrab.nominator.mod.NominatorModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class PillarDynamoDBDaoTest {

    private DynamoDBProxyServer _server;
    private AmazonDynamoDB _dynamoDB;
    private DynamoDBMapper _dynamoDBMapper;
    private Injector _injector;
    protected NominatorDao _nominatorDao;

    @BeforeAll
    public void initAll() {

        // Need to set the SQLite4Java library path to avoid a linker error
        System.setProperty("sqlite4java.library.path", "./build/libs/");

        _dynamoDB = createAmazonDynamoDBClient();
        _dynamoDBMapper = new DynamoDBMapper(_dynamoDB);
        _injector = Guice.createInjector(new NominatorModule(_dynamoDB));
    }

    @BeforeEach
    public void setUpFixture() {

        // It starts up in-memory and in-process instance
        // of DynamoDB Local that runs over HTTP
        {

            final String[] localArgs = { "-inMemory" };

            try {
                _server = ServerRunner.createServerFromCommandLineArgs(localArgs);
                _server.start();

            } catch (Exception e) {
                e.printStackTrace();
                // Assert.fail(e.getMessage());
                // return;
            }
        }

        DynamoDBTableCreator.createTables(_dynamoDBMapper, _dynamoDB);
        _nominatorDao = _injector.getInstance(NominatorDao.class);
    }

    private AmazonDynamoDB createAmazonDynamoDBClient() {

        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000",
                        Regions.US_EAST_2.getName()))
                .withCredentials(credentialsProvider())
                .build();
    }

    private AWSStaticCredentialsProvider credentialsProvider() {

        BasicAWSCredentials creds = new BasicAWSCredentials("fakeId", "fakeSecret");
        return new AWSStaticCredentialsProvider(creds);
    }

    @AfterEach
    public void tearDown() {

        if (_server != null) {
            try {
                _server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @AfterAll
    public void tearDownAll() {

        _injector = null;
    }
}
