#!/bin/sh

PROJ_PREFIX=nominator
EMU_HOST="localhost:8000"


echo "Creating targets table"
aws dynamodb create-table \
    --endpoint-url "http://${EMU_HOST}" \
    --table-name "${PROJ_PREFIX}".targets \
    --attribute-definitions \
	AttributeName=issuer,AttributeType=S \
        AttributeName=identifier,AttributeType=S \
     --key-schema \
        AttributeName=issuer,KeyType=HASH \
        AttributeName=identifier,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --table-class STANDARD
