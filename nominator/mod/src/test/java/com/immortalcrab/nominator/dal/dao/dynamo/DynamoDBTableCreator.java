package com.immortalcrab.nominator.dal.dao.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import com.immortalcrab.nominator.dal.entities.dynamo.Employee;
import com.immortalcrab.nominator.dal.entities.dynamo.Organization;

import java.util.logging.Level;
import java.util.logging.Logger;

class DynamoDBTableCreator {

    static void inception(DynamoDBMapper _dynamoDBMapper, AmazonDynamoDB _dynamoDB) {

        Class<?> catalog[] = {Organization.class, Employee.class};
        for(int idx = 0; idx < catalog.length; idx++){
            createTable(catalog[idx], _dynamoDBMapper, _dynamoDB);
        }
    }

    public static void createTable(Class<?> cls, DynamoDBMapper dynamoDBMapper, AmazonDynamoDB dynamoDB) {

        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(cls);
        createTableRequest.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        if (createTableRequest.getGlobalSecondaryIndexes() != null) {

            for(GlobalSecondaryIndex gsi : createTableRequest.getGlobalSecondaryIndexes()){

                gsi.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

                Projection projection = new Projection().withProjectionType("ALL");
                gsi.withProjection(projection);
            }
        }

        if (createTableRequest.getLocalSecondaryIndexes() != null) {

            for(LocalSecondaryIndex lsi : createTableRequest.getLocalSecondaryIndexes()) {

                Projection projection = new Projection().withProjectionType("ALL");
                lsi.withProjection(projection);
            }
        }

        if (!tableExists(dynamoDB, createTableRequest)) {
            dynamoDB.createTable(createTableRequest);
        }

        waitForTableCreated(createTableRequest.getTableName(), dynamoDB);
    }

    private static void waitForTableCreated(String tableName, AmazonDynamoDB dynamoDB) {

        for (;;) {
            try {
                Thread.sleep(500);
                DescribeTableRequest dtr = new DescribeTableRequest(tableName);
                TableDescription table = dynamoDB.describeTable(dtr).getTable();
                if (table == null) {
                    continue;
                }

                String tableStatus = table.getTableStatus();
                if (tableStatus.equals(TableStatus.ACTIVE.toString())) {
                    return;
                }
            } catch (ResourceNotFoundException ex) {
                // ignored for now
            } catch (InterruptedException ex) {
                Logger.getLogger(DynamoDBTableCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static boolean tableExists(AmazonDynamoDB dynamoDB, CreateTableRequest createTableRequest) {

        try {
            dynamoDB.describeTable(createTableRequest.getTableName());
            return true;
        } catch (ResourceNotFoundException ex) {
            return false;
        }
    }

}
