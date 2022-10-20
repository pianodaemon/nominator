package com.immortalcrab.nominator.dal.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import com.immortalcrab.nominator.entities.Employee;
import com.immortalcrab.nominator.entities.Organization;
import java.util.logging.Level;
import java.util.logging.Logger;

class Util {

    static void createTables(DynamoDBMapper _dynamoDBMapper, AmazonDynamoDB _dynamoDB) {
        createTable(Employee.class, _dynamoDBMapper, _dynamoDB);
        createTable(Organization.class, _dynamoDBMapper, _dynamoDB);
    }

    public static void createTable(Class<?> cls, DynamoDBMapper dynamoDBMapper, AmazonDynamoDB dynamoDB) {

        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(cls);
        createTableRequest.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        if (createTableRequest.getGlobalSecondaryIndexes() != null) {

            createTableRequest.getGlobalSecondaryIndexes().stream().forEach(gsi -> {

                Projection projection = new Projection().withProjectionType("ALL");
                gsi.withProjection(projection);
                gsi.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            });
        }

        if (createTableRequest.getLocalSecondaryIndexes() != null) {

            createTableRequest.getLocalSecondaryIndexes().stream().forEach(lsi -> {

                Projection projection = new Projection().withProjectionType("ALL");
                lsi.withProjection(projection);
            });
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
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
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
