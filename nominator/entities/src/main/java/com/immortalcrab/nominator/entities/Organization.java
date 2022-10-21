package com.immortalcrab.nominator.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DynamoDBTable(tableName = "nominator.organizations")
public class Organization {

    @DynamoDBHashKey
    private String orgName;

    @DynamoDBRangeKey
    private String identifier;

    @DynamoDBAttribute
    private Integer regimen;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "gsiNature")
    private String nature;

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "gsiNature", localSecondaryIndexName = "gskAka")
    private String aka;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Organization temp = (Organization) o;
        boolean result = temp.getOrgName().equals(this.getOrgName())
                && temp.getIdentifier().equals(this.getIdentifier())
                && temp.getNature().equals(this.getNature())
                && temp.getAka().equals(this.getAka())
                && temp.getRegimen().equals(this.getRegimen());

        return result;
    }
}
