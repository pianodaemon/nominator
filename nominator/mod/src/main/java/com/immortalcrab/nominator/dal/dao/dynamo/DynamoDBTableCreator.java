package com.immortalcrab.nominator.dal.dao.dynamo;

import java.util.logging.Level;
import java.util.logging.Logger;

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

import lombok.AllArgsConstructor;

@AllArgsConstructor
class DynamoDBTableCreator {

    private DynamoDBMapper _mapper;
    private AmazonDynamoDB _dynDB;

    static void inception(DynamoDBMapper _mapper, AmazonDynamoDB _dynDB) {

        Class<?> catalog[] = { Organization.class, Employee.class };
        DynamoDBTableCreator ic = new DynamoDBTableCreator(_mapper, _dynDB);
        for (int idx = 0; idx < catalog.length; idx++) {
            ic.createTable(catalog[idx]);
        }
    }

    private void createTable(Class<?> cls) {

        CreateTableRequest createTableRequest = _mapper.generateCreateTableRequest(cls);
        createTableRequest.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        if (createTableRequest.getGlobalSecondaryIndexes() != null) {

            for (GlobalSecondaryIndex gsi : createTableRequest.getGlobalSecondaryIndexes()) {

                gsi.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

                Projection projection = new Projection().withProjectionType("ALL");
                gsi.withProjection(projection);
            }
        }

        if (createTableRequest.getLocalSecondaryIndexes() != null) {

            for (LocalSecondaryIndex lsi : createTableRequest.getLocalSecondaryIndexes()) {

                Projection projection = new Projection().withProjectionType("ALL");
                lsi.withProjection(projection);
            }
        }

        try {
            _dynDB.describeTable(createTableRequest.getTableName());
        } catch (ResourceNotFoundException ex) {
            _dynDB.createTable(createTableRequest);
        }

        waitForTableCreated(createTableRequest.getTableName());
    }

    private void waitForTableCreated(String tableName) {

        for (;;) {
            try {
                Thread.sleep(500);
                DescribeTableRequest dtr = new DescribeTableRequest(tableName);
                TableDescription table = _dynDB.describeTable(dtr).getTable();
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
}