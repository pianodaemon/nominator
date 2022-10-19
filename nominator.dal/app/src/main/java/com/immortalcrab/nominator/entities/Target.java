package com.immortalcrab.nominator.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DynamoDBTable(tableName = "nominator.targets")
public class Target {

    @DynamoDBHashKey
    private String issuer;

    @DynamoDBRangeKey
    private String identifier;

    @DynamoDBAttribute
    private String userName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Target temp = (Target) o;
        boolean result = temp.getIssuer().equals(this.getIssuer()) &&
                temp.getIdentifier().equals(this.getIdentifier())  &&
                temp.getUserName().equals(this.getUserName());

        return result;
    }
}
