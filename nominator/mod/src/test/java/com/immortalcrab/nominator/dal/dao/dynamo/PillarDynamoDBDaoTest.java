package com.immortalcrab.nominator.dal.dao.dynamo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
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
    private Injector _injector;
    protected DynamoDBNominatorDao _nominatorDao;

    @BeforeAll
    public void initAll() {

        // Need to set the SQLite4Java library path to avoid a linker error
        System.setProperty("sqlite4java.library.path", "./build/libs/");

        Class<DynamoDBNominatorDao> daoCls = DynamoDBNominatorDao.class;
        _injector = Guice.createInjector(new NominatorModule(daoCls));
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

        _nominatorDao = (DynamoDBNominatorDao) _injector.getInstance(NominatorDao.class);
        DynamoDBTableCreator.inception(_nominatorDao.getConf().getMapper(), _nominatorDao.getConf().getDynamoDB());
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
