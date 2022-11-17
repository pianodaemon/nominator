#!/bin/bash -x

SUBSCRIPTOR="dummy"

# Verification for expected buckets
{
    expected_buckets=(cfdi-datalake cfdi-datares)
    for b in "${expected_buckets[@]}"; do
        awslocal s3 ls s3://${b}-${SUBSCRIPTOR}
    done
}
