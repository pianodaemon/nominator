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
@DynamoDBTable(tableName = "nominator.entities")
public class Employee {

    @DynamoDBHashKey
    private String orgName;

    @DynamoDBRangeKey
    private String identifier;

    @DynamoDBAttribute
    private String Name;

    @DynamoDBAttribute
    private String surname;

    @DynamoDBAttribute
    private String optionalSurname;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Employee temp = (Employee) o;
        boolean result = temp.getOrgName().equals(this.getOrgName())
                && temp.getIdentifier().equals(this.getIdentifier())
                && temp.getName().equals(this.getName())
                && temp.getSurname().equals(this.getSurname())
                && temp.getOptionalSurname().equals(this.getOptionalSurname());

        return result;
    }
}
