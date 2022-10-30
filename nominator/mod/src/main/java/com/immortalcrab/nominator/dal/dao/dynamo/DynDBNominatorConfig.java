package com.immortalcrab.nominator.dal.dao.dynamo;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import lombok.Getter;

@Getter
class DynDBNominatorConfig {

    private DynamoDBMapper mapper;
    private AmazonDynamoDB dynamoDB;

    public DynDBNominatorConfig(){
        dynamoDB = createAmazonDynamoDBClient();
        mapper = new DynamoDBMapper(dynamoDB);
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
}
