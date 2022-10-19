package com.immortalcrab.nominator.mod;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.inject.AbstractModule;
import com.immortalcrab.nominator.dal.DynamoDBNominatorDao;
import com.immortalcrab.nominator.dal.NominatorDao;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NominatorModule extends AbstractModule {

    protected AmazonDynamoDB _dynamoDB;
    
    @Override
    protected void configure() {
        bind(NominatorDao.class).toInstance(new DynamoDBNominatorDao(new DynamoDBMapper(_dynamoDB)));
    }
}
