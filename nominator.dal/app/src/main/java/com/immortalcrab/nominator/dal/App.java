package com.immortalcrab.nominator.dal;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.immortalcrab.nominator.entities.Target;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration())
                .withCredentials(credentialsProvider())
                .build();

        //BLogicDao bdao = new BLogicDao().mapper(new DynamoDBMapper(client));
        TargetDao bdao = new TargetDao(new DynamoDBMapper(client));

        Target target = new Target();
        target.setFirst_name("Edwin");
        target.setIdentifier("PACE8001104V2");
        target.setIssuer("KACE8001104V0");

        bdao.put(target);
        System.out.println(bdao.getAll()
                .stream()
                .map(Target::toString)
                .collect(Collectors.joining("\n")));
    }

    private static AwsClientBuilder.EndpointConfiguration endpointConfiguration() {
        return new AwsClientBuilder.EndpointConfiguration(
                "http://localhost:8000/",
                Regions.US_EAST_2.getName()
        );
    }

    private static AWSStaticCredentialsProvider credentialsProvider() {
        return new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                        "fakeId",
                        "fakeSecret"
                )
        );
    }

}
